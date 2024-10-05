package com.massivecraft.factions.task;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.coll.FactionColl;
import com.massivecraft.factions.engine.EngineFtop;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.FactionValue;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TaskFactionTopCalculate extends ModuloRepeatTask {

    private static final TaskFactionTopCalculate i = new TaskFactionTopCalculate();
    private boolean running = false;
    private int taskID = 0;

    public static TaskFactionTopCalculate get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return MConf.get().taskFactionTopCalculateMillis;
    }

    public boolean isRunning() {
        return running;
    }

    public String getLastRunTime() {
        return TimeUtil.formatTime(System.currentTimeMillis() - FactionTopData.get().getLastRunTime()).trim();
    }

    public void updateFactionTopValues() {
        if (!MConf.get().enableFactionsTop) return;
        if (running) return;

        FactionTopData.get().setLastRunTime(System.currentTimeMillis());

        running = true;

        MixinMessage.get().msgAll(LangConf.get().ftopTotalsResynchronizeStartMsg);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Factions.get(), () -> {
            ConcurrentHashMap<Faction, Set<PS>> toCalculate = new ConcurrentHashMap<>();

            if (MConf.get().ftopOnlyCalculateSpawnerChunks) {
                for (Faction fac : FactionColl.get().getAll()) {
                    if (fac != null && !fac.isSystemFaction() && fac.getSpawnerChunks() != null && !fac.getSpawnerChunks().isEmpty()) {
                        Set<PS> chunksToAdd = fac.getSpawnerChunks().stream().map(chunkPos -> PS.valueOf(chunkPos.getChunk())).collect(Collectors.toSet());
                        toCalculate.put(fac, chunksToAdd);
                    }
                }
            } else {
                Map<Faction, Set<PS>> map = BoardColl.get().getFactionToChunks();
                for (Faction fac : map.keySet()) {
                    if (fac != null && !fac.isSystemFaction()) {
                        toCalculate.put(fac, map.get(fac));
                    }
                }
            }

            FactionTopData.get().backupData();

            FactionTopData.get().getFactionValues().values().forEach(val -> val.setTotalSpawnerValue(0));
            FactionTopData.get().getFactionValues().values().forEach(val -> val.setSpawnerValues(new MassiveSet<>()));

            taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Factions.get(), () -> {
                Faction factionToProcess = toCalculate.keySet().stream().findFirst().orElse(null);

                if (factionToProcess == null) {
                    finishCalculations();
                    return;
                }

                if (factionToProcess.detached()) {
                    toCalculate.remove(factionToProcess);
                    return;
                }

                Set<PS> chunksToProcessPs = toCalculate.get(factionToProcess);
                Set<PS> chunksProcessedPs = new HashSet<>();

                chunksToProcessPs.stream().limit(MConf.get().ftopChunksToCalculatePerSecond).forEach(ps -> {
                    calculatePs(factionToProcess, ps);
                    chunksProcessedPs.add(ps);
                });

                chunksToProcessPs.removeAll(chunksProcessedPs);

                if (chunksToProcessPs.isEmpty()) {
                    toCalculate.remove(factionToProcess);
                } else {
                    toCalculate.put(factionToProcess, chunksToProcessPs);
                }

                if (toCalculate.isEmpty()) {
                    finishCalculations();
                }
            }, 0L, 20L);
        });
    }

    public void finishCalculations() {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Factions.get(), () -> {
            FactionTopData.get().getFactionValues().values().forEach(FactionValue::updateTotalValue);
            FactionTopData.get().sortData();

            running = false;

            LangConf.get().ftopTotalsResynchronizedMsg.forEach(s -> MixinMessage.get().msgAll(s));
        });

        Bukkit.getServer().getScheduler().cancelTask(taskID);
    }

    public void calculatePs(Faction faction, PS ps) {
        if (faction == null || faction.isSystemFaction()) return;

        FactionValue factionValue = FactionTopData.get().getFactionValues().get(faction.getId());

        if (factionValue == null) {
            return;
        }

        calculatePs(factionValue, ps);
    }

    public void calculatePs(FactionValue factionValue, PS ps) {
        if (ps == null || ps.getLocation() == null) return;

        World world = ps.asBukkitWorld(true);

        Integer chunkX = ps.getChunkX(true);
        Integer chunkZ = ps.getChunkZ(true);

        if (chunkX == null || chunkZ == null) return;

        try {
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                ChunkProviderServer cps = ((CraftWorld) world).getHandle().chunkProviderServer;

                cps.getChunkAt(chunkX, chunkZ, () ->
                        world.getChunkAtAsync(chunkX, chunkZ, chunk -> {
                            processSpawners(factionValue,
                                    Arrays.stream(chunk.getTileEntities())
                                            .filter(blockState -> blockState instanceof CreatureSpawner)
                                            .map(blockState -> (CreatureSpawner) blockState)
                                            .collect(Collectors.toList())
                            );

                            chunk.unload(false, true);
                        })
                );
            } else {
                world.getChunkAtAsync(chunkX, chunkZ, chunk -> processSpawners(factionValue,
                        Arrays.stream(chunk.getTileEntities())
                                .filter(blockState -> blockState instanceof CreatureSpawner)
                                .map(blockState -> (CreatureSpawner) blockState)
                                .collect(Collectors.toList())
                ));
            }
        } catch (Exception ignored) {
        }
    }

    private void processSpawners(FactionValue factionValue, List<CreatureSpawner> creatureSpawners) {
        if (!creatureSpawners.isEmpty()) {
            creatureSpawners.stream().filter(Objects::nonNull).forEach(spawner -> {
                if (!FactionTopValue.get().getSpawnerValues().containsKey(spawner.getSpawnedType()))
                    return;

                if (FactionTopValue.get().getSpawnerValues().get(spawner.getSpawnedType()) <= 0)
                    return;

                if (factionValue.getSpawnerValues() == null) {
                    factionValue.setSpawnerValues(new MassiveSet<>());
                }

                int amountOfSpawnersAtLocation = 1;

                int calculatedSpawnerWorth = EngineFtop.get().calculateSpawnerWorth(spawner, FactionTopValue.get().getSpawnerValues().get(spawner.getSpawnedType())) * amountOfSpawnersAtLocation;

                FactionValueSpawnerTypeValue factionValueSpawnerTypeValue = factionValue.getSpawnerValueEntityType(spawner.getSpawnedType());

                if (factionValueSpawnerTypeValue == null) {
                    factionValue.getSpawnerValues().add(new FactionValueSpawnerTypeValue(spawner.getSpawnedType(), MUtil.set(EngineFtop.blockToString(spawner)), amountOfSpawnersAtLocation, calculatedSpawnerWorth));
                } else {
                    factionValueSpawnerTypeValue.getFactionValueLocationStrings().add(EngineFtop.blockToString(spawner));
                    factionValueSpawnerTypeValue.setAmountOfSpawners(factionValueSpawnerTypeValue.getAmountOfSpawners() + amountOfSpawnersAtLocation);
                    factionValueSpawnerTypeValue.setTotalSpawnerValues(factionValueSpawnerTypeValue.getTotalSpawnerValues() + calculatedSpawnerWorth);
                }
            });
        }
    }

    @Override
    public void invoke(long now) {
        if (System.currentTimeMillis() - FactionTopData.get().getLastRunTime() >= MConf.get().autoRecalculateMillis) {
            updateFactionTopValues();
        }
    }

}