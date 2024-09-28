package com.massivecraft.factions.entity.objects;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.RaidDataStorage;
import com.massivecraft.massivecore.util.TimeUnit;

public class RaidData {
    private final String factionRaidingId;
    private final String factionRaidedId;
    private Long raidStartMillis;
    private final long valueAtRaidStart;
    private Long breachedAtMillis = null;

    public RaidData(String factionRaidingId, String factionRaidedId, Long raidStartMillis, long valueAtRaidStart) {
        this.factionRaidingId = factionRaidingId;
        this.factionRaidedId = factionRaidedId;
        this.raidStartMillis = raidStartMillis;
        this.valueAtRaidStart = valueAtRaidStart;
    }

    public String getFactionRaidingId() {
        return factionRaidingId;
    }

    public String getFactionRaidedId() {
        return factionRaidedId;
    }

    public Long getRaidStartMillis() {
        return raidStartMillis;
    }

    public void restartRaidStartMillis(Long millis) {
        this.raidStartMillis = millis;
        RaidDataStorage.get().changed();
    }

    public int getPhase() {
        long phaseOneLengthMillis = MConf.get().phaseOneLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE;
        long timeElapsed = System.currentTimeMillis() - raidStartMillis;
        return timeElapsed > phaseOneLengthMillis ? 2 : 1;
    }

    public int getTotalMillisRemaining() {
        long phaseOneLengthMillis = MConf.get().phaseOneLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE;
        long phaseTwoLengthMillis = MConf.get().phaseTwoLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE;
        long totalRaidtimerLengthMillis = phaseOneLengthMillis + phaseTwoLengthMillis;
        long timeElapsed = System.currentTimeMillis() - raidStartMillis;
        return (int) (totalRaidtimerLengthMillis - timeElapsed);
    }

    public long getValueAtRaidStart() {
        return valueAtRaidStart;
    }

    public Long getBreachedAtMillis() {
        return breachedAtMillis;
    }

    public void setBreachedAtMillis(Long breachedAtMillis) {
        this.breachedAtMillis = breachedAtMillis;
    }
}