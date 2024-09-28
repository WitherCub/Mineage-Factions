package com.massivecraft.factions.engine;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.CmdFactionsSpawnerchunk;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.FactionBreach;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.util.ChunkPos;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import net.minecraft.server.v1_8_R3.SpawnerCreature;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.util.*;

public class EngineSpawnerchunk extends Engine {

    private static final EngineSpawnerchunk i = new EngineSpawnerchunk();

    public static EngineSpawnerchunk get() {
        return i;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent event) {
        PS psAt = PS.valueOf(event.getBlock());
        Faction factionAt = BoardColl.get().getFactionAt(psAt);
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        ChunkPos chunkPos = new ChunkPos(event.getBlock().getWorld().getName(), chunk.getX(), chunk.getZ());

        CmdFactionsSpawnerchunk.SpawnerChunkRequest spawnerChunkRequest = CmdFactionsSpawnerchunk.get().playersSettingSpawnerChunks.getIfPresent(player.getUniqueId());

        if (spawnerChunkRequest != null) {
            if (psAt.getWorld(true).equals(spawnerChunkRequest.ps.getWorld(true)) && psAt.getChunkX(true).equals(spawnerChunkRequest.ps.getChunkX(true)) && psAt.getChunkZ(true).equals(spawnerChunkRequest.ps.getChunkZ(true))) {
                MPlayer mPlayer = MPlayer.get(player);

                if (factionAt == null || factionAt.isSystemFaction() || factionAt != mPlayer.getFaction()) {
                    MixinMessage.get().msgOne(player, MConf.get().onlySetSpawnerchunkOwnTerritoryMsg);
                    event.setCancelled(true);
                    return;
                }

                if (!MPerm.getPermSpawnerchunk().has(mPlayer, factionAt, true)) return;

                if (factionAt.getSpawnerChunks().contains(chunkPos)) {
                    MixinMessage.get().msgOne(player, MConf.get().chunkAlreadySpawnerchunkMsg);
                    event.setCancelled(true);
                    return;
                }

//                boolean nextToSpawnerChunk = true;
//
//                if (!factionAt.getSpawnerChunks().isEmpty()) {
//                    nextToSpawnerChunk = false;
//
//                    // Check surrounding chunks for spawner chunks
//                    for (int x = (chunkPos.getX() - 1); x <= (chunkPos.getX() + 1); x++) {
//                        for (int z = (chunkPos.getZ() - 1); z <= (chunkPos.getZ() + 1); z++) {
//                            PS checkPs = PS.valueOf(event.getBlock().getWorld().getName(), x, z);
//                            if (BoardColl.get().getFactionAt(checkPs).getId().equals(factionAt.getId()) && factionAt.getSpawnerChunks().contains(new ChunkPos(event.getBlock().getWorld().getName(), x, z))) {
//                                nextToSpawnerChunk = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                if (!nextToSpawnerChunk) {
//                    MixinMessage.get().msgOne(player, MConf.get().mustBeNextToSpawnerchunkMsg);
//                    event.setCancelled(true);
//                    return;
//                }

                Set<UUID> cacheKeysToRemove = new HashSet<>();

                for (UUID uuid : CmdFactionsSpawnerchunk.get().playersSettingSpawnerChunks.asMap().keySet()) {
                    CmdFactionsSpawnerchunk.SpawnerChunkRequest chunkRequest = CmdFactionsSpawnerchunk.get().playersSettingSpawnerChunks.getIfPresent(uuid);
                    if (chunkRequest != null && chunkRequest.ps.getWorld(true).equals(spawnerChunkRequest.ps.getWorld(true)) && chunkRequest.ps.getChunkX(true).equals(spawnerChunkRequest.ps.getChunkX(true)) && chunkRequest.ps.getChunkZ(true).equals(spawnerChunkRequest.ps.getChunkZ(true))) {
                        cacheKeysToRemove.add(uuid);
                    }
                }

                CmdFactionsSpawnerchunk.get().playersSettingSpawnerChunks.invalidateAll(cacheKeysToRemove);

                factionAt.msg(MConf.get().spawnerChunkMarkedAt
                        .replace("%chunkX%", Factions.get().getPriceFormat().format(psAt.getChunkX(true)))
                        .replace("%chunkZ%", Factions.get().getPriceFormat().format(psAt.getChunkZ(true)))
                        .replace("%world%", psAt.getWorld())
                        .replace("%player%", player.getName())
                );

                factionAt.addSpawnerChunk(chunkPos);
            }
            return;
        }

        if (player.hasPermission("factions.override")) {
            return;
        }

        if (factionAt == null || factionAt.isNone()) {
            MixinMessage.get().msgOne(player, MConf.get().cantPlaceSpawnersInWild);
            event.setCancelled(true);
            return;
        }

        if (!factionAt.getSpawnerChunks().contains(chunkPos)) {
            MixinMessage.get().msgOne(player, MConf.get().youCanOnlyPlaceSpawnersInSpawnerchunksMsg);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            PS explosionLocation = PS.valueOf(event.getLocation());
            Faction factionBreached = BoardColl.get().getFactionAt(explosionLocation);

            PS sourceLocation = PS.valueOf(((TNTPrimed) event.getEntity()).getSourceLoc());
            Faction factionBreaching = BoardColl.get().getFactionAt(sourceLocation);

            if (factionBreaching.isSystemFaction()) return;

            if (factionBreached == factionBreaching) return;

            Chunk chunk = event.getLocation().getChunk();
            ChunkPos chunkPos = new ChunkPos(event.getLocation().getWorld().getName(), chunk.getX(), chunk.getZ());

            if (factionBreached.getSpawnerChunks().contains(chunkPos)) {
                if (!canBeBreached(factionBreached)) return;
                RaidDataStorage.get().setBreachedAtMillis(factionBreached);
            }
        }
    }

    public boolean canBeBreached(Faction faction) {
        ArrayList<FactionBreach> factionBreaches = FactionBreachs.get().factionBreaches;
        Collections.reverse(factionBreaches);

        for (FactionBreach factionBreach : FactionBreachs.get().factionBreaches) {
            if (!factionBreach.getFactionBreachedName().equals(faction.getName())) continue;
            if (System.currentTimeMillis() - factionBreach.timeOfBreach < MConf.get().breachCooldown) return false;
        }
        return true;
    }

    public long getLatestBreachTime(Faction faction) {
        ArrayList<FactionBreach> factionBreaches = FactionBreachs.get().factionBreaches;
        Collections.reverse(factionBreaches);

        for (FactionBreach factionBreach : FactionBreachs.get().factionBreaches) {
            if (!factionBreach.getFactionBreachedName().equals(faction.getName())) continue;
            if (System.currentTimeMillis() - factionBreach.timeOfBreach < MConf.get().breachCooldown)
                return factionBreach.timeOfBreach;
        }
        return 0;
    }

    public int getFactionsBreached(Faction faction) {
        int times = 0;
        for (FactionBreach factionBreach : FactionBreachs.get().factionBreaches) {
            if (!factionBreach.getFactionBreachingName().equals(faction.getName())) continue;
            times++;
        }
        return times;
    }

    public int getTimesBreached(Faction faction) {
        int times = 0;
        for (FactionBreach factionBreach : FactionBreachs.get().factionBreaches) {
            if (!factionBreach.getFactionBreachedName().equals(faction.getName())) continue;
            times++;
        }
        return times;
    }

    @EventHandler
    public void onFactionsNameChange(EventFactionsNameChange event) {
        for (FactionBreach factionBreach : FactionBreachs.get().factionBreaches) {
            if (event.getFaction().getId().equals(factionBreach.getFactionBreachingId())) {
                factionBreach.setFactionBreachingName(event.getNewName());
            } else if (event.getFaction().getId().equals(factionBreach.getFactionBreachedId())) {
                factionBreach.setFactionBreachedName(event.getNewName());
            }
        }
    }
}