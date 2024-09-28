package com.massivecraft.factions.task;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.CacheLocations;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskPlaceSand extends ModuloRepeatTask {

    private static final TaskPlaceSand i = new TaskPlaceSand();
    public MassiveMap<String, CacheLocations> sandbotCacheLocations = new MassiveMap<>();

    public static TaskPlaceSand get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return MConf.get().placeSandDelay;
    }

    @Override
    public void invoke(long l) {
        if (Factions.get().getPluginEnableMillis() + MConf.get().delaySandPlacementXMillisAfterPluginEnable >= System.currentTimeMillis()) return;

        Set<Sandbot> sandbotsToRemove = new HashSet<>();

        for (Faction faction : FactionColl.get().getAll()) {
            if (faction.getSandbots().isEmpty()) continue;
            for (Sandbot sandbot : faction.getSandbots().values()) {
                if (sandbot == null || sandbot.isDespawned()) continue;

                Location location = sandbot.getLocation();
                Faction factionAt = BoardColl.get().getFactionAt(sandbot.getPs());

                if (factionAt.getId().equalsIgnoreCase(Factions.ID_NONE) || factionAt.getId().equalsIgnoreCase(Factions.ID_WARZONE) || factionAt.getId().equalsIgnoreCase(Factions.ID_SAFEZONE)) {
                    continue;
                }

                World world = location.getWorld();

                HashMap<Chunk, Set<Location>> toCheckAndProcess = new HashMap<>();
                CacheLocations cacheLocations = sandbotCacheLocations.get(sandbot.getUuidString());

                if (cacheLocations != null && (MConf.get().refreshSandbotBlocksEveryXMillis < System.currentTimeMillis() - cacheLocations.getLastCheckMillis())) {
                    cacheLocations = null;
                }

                if (cacheLocations == null) {
                    for (int x = (location.getBlockX() - MConf.get().sandbotRadius); x <= (location.getBlockX() + MConf.get().sandbotRadius); x++) {
                        for (int y = (location.getBlockY() - MConf.get().sandbotYRadius); y <= (location.getBlockY() + MConf.get().sandbotYRadius); y++) {
                            for (int z = (location.getBlockZ() - MConf.get().sandbotRadius); z <= (location.getBlockZ() + MConf.get().sandbotRadius); z++) {
                                if (sandbotsToRemove.contains(sandbot)) continue;

                                if (y < 0 || y > 256) continue;

                                Block block = world.getBlockAt(x, y, z);

                                if (!MConf.get().sandbotTriggerBlocks.contains(block.getType()) || block.getLocation() == location)
                                    continue;

                                Block relativeBlock = block.getRelative(BlockFace.DOWN);

                                if (relativeBlock == null || relativeBlock.getType() != Material.AIR || relativeBlock == location.getBlock())
                                    continue;

                                Chunk chunk = block.getChunk();

                                if (!toCheckAndProcess.containsKey(chunk)) {
                                    Set<Location> blocks = new HashSet<>();
                                    blocks.add(relativeBlock.getLocation());
                                    toCheckAndProcess.put(chunk, blocks);
                                } else {
                                    toCheckAndProcess.get(chunk).add(relativeBlock.getLocation());
                                }
                            }
                        }
                    }
                    cacheLocations = new CacheLocations(toCheckAndProcess, System.currentTimeMillis());
                    sandbotCacheLocations.put(sandbot.getUuidString(), cacheLocations);
                } else {
                    toCheckAndProcess = cacheLocations.getToCheckAndProcess();
                }

                for (Chunk chunk : toCheckAndProcess.keySet()) {
                    Faction chunkFactionAt = BoardColl.get().getFactionAt(PS.valueOf(chunk.getBlock(8, 8, 8)));
                    if (factionAt.getId().equals(chunkFactionAt.getId())) {
                        Set<Location> blocksToSand = toCheckAndProcess.get(chunk);

                        double sandPrice = blocksToSand.size() * MConf.get().pricePerSand;

                        if (MConf.get().pricePerSand == 0 || (Money.enabled() && Money.despawn(factionAt, null, sandPrice, "Factions"))) {
                            for (Location loc : blocksToSand) {
                                loc.getWorld().spawnFallingBlock(loc, Material.SAND, (byte) 0);
                            }
                        }
                    } else {
                        sandbotsToRemove.add(sandbot);
                    }
                }
            }

            sandbotsToRemove.forEach(sbot -> {
                if (sbot != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            faction.despawnSandbot(sbot);
                            faction.msg(MConf.get().sandbotDespawnedNoMoneyInFbankMsg);
                        }
                    }.runTaskLater(Factions.get(), 2L);
                }
            });
        }
    }
}