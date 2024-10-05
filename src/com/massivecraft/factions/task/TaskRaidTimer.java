package com.massivecraft.factions.task;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.coll.MPlayerColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.FactionBreach;
import com.massivecraft.factions.entity.objects.FactionValue;
import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.TimeUnit;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskRaidTimer extends ModuloRepeatTask {

    private static final TaskRaidTimer i = new TaskRaidTimer();
    private final Map<String, Integer> factionAndTaskId = new HashMap<>();

    public static TaskRaidTimer get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 2000L;
    }

    @Override
    public void invoke(long l) {
        long raidtimerPhaseTwoStartMillis = MConf.get().phaseOneLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE;

        RaidDataStorage.get().getRaidData().forEach(raidData -> {
            long timeElapsed = System.currentTimeMillis() - raidData.getRaidStartMillis();
            if (raidtimerPhaseTwoStartMillis <= timeElapsed && timeElapsed <= raidtimerPhaseTwoStartMillis + 10000L) {
                //Faction factionRaided = Faction.get(raidData.getFactionRaidedId());
                //if (factionRaided != null && factionRaided.isNormal()) {
                //    factionRaided.msg(MConf.get().endOfPhaseOneMsg);
                //}
                //Faction factionRaiding = Faction.get(raidData.getFactionRaidingId());
                //if (factionRaided != null && factionRaided.isNormal()) {
                //    factionRaiding.msg(MConf.get().endOfPhaseOneFactionRaidingMsg.replace("%faction%", factionRaided.getName()));
                //}
            }
        });

        RaidDataStorage.get().getPhaseThreeShieldFactions().removeIf(raidtimerShield -> (MConf.get().phaseThreeLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE) < System.currentTimeMillis() - raidtimerShield.getShieldStartMillis());

        long raidtimerLengthMinutes = raidtimerPhaseTwoStartMillis + (MConf.get().phaseTwoLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE);

        RaidDataStorage.get().getRaidData().removeIf(raidData -> {
            if (raidtimerLengthMinutes < System.currentTimeMillis() - raidData.getRaidStartMillis()) {
                Faction factionRaided = Faction.get(raidData.getFactionRaidedId());
                Faction factionRaiding = Faction.get(raidData.getFactionRaidingId());

                if (factionRaiding != null && factionRaiding.isNormal()) {
                    if (raidData.getBreachedAtMillis() != null) {
                        processBreach(raidData);
                    }

                    factionRaiding.msg(LangConf.get().raidHasEndedMsg.replace("%faction%", factionRaided.getName()));

                    // Phase III shield
                    RaidDataStorage.get().addRaidtimerShield(factionRaided);
                    factionRaided.msg(LangConf.get().startOfPhaseThreeMsg);
                }
                return true;
            }
            return false;
        });
    }

    public void processBreach(RaidData raidData) {
        Faction factionRaided = Faction.get(raidData.getFactionRaidedId());
        Faction factionRaiding = Faction.get(raidData.getFactionRaidingId());

        if (factionRaided == null || factionRaiding == null || factionRaiding.isSystemFaction() || factionRaiding.isSystemFaction()) {
            return;
        }

        if (factionRaided.getSpawnerChunks() != null && !factionRaided.getSpawnerChunks().isEmpty()) {
            FactionValue factionValue = new FactionValue(factionRaided.getId(), new MassiveSet<>(), 0, 0);

            Set<PS> chunksToProcessPs = factionRaided.getSpawnerChunks().stream().map(chunkPos -> PS.valueOf(chunkPos.getChunk())).collect(Collectors.toSet());

            factionAndTaskId.put(factionRaided.getId(), Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Factions.get(), () -> {
                Set<PS> chunksProcessedPs = new HashSet<>();

                chunksToProcessPs.stream().limit(MConf.get().ftopChunksToCalculatePerSecond).forEach(ps -> {
                    TaskFactionTopCalculate.get().calculatePs(factionValue, ps);
                    chunksProcessedPs.add(ps);
                });

                chunksToProcessPs.removeAll(chunksProcessedPs);

                if (chunksToProcessPs.isEmpty()) {
                    factionValue.updateTotalValue(); // Update total faction value first

                    int taskId = factionAndTaskId.get(factionRaided.getId());
                    factionAndTaskId.remove(factionRaided.getId());

                    int valueLost = (int) (raidData.getValueAtRaidStart() - factionValue.getTotalSpawnerValue());

                    if (valueLost > 0) {
                        for (MPlayer mPlayer : MPlayerColl.get().getAllOnline()) {
                            for (String line : LangConf.get().msgFactionBreached) {
                                mPlayer.msg(line
                                        .replace("%factionRaiding%", factionRaiding.describeTo(mPlayer, true))
                                        .replace("%factionRaided%", factionRaided.describeTo(mPlayer, true))
                                        .replace("%amount%", Factions.get().getPriceFormat().format(valueLost))
                                );
                            }
                        }

                        FactionBreachs.get().factionBreaches.add(new FactionBreach(factionRaided, factionRaiding, raidData.getBreachedAtMillis(), valueLost));
                    }

                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                }
            }, 0L, 20L));
        }
    }
}