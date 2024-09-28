package com.massivecraft.factions.entity;

import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.factions.entity.objects.RaidtimerShield;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.store.Entity;

public class RaidDataStorage extends Entity<RaidDataStorage>
{

    private static RaidDataStorage i;
    public static void set(RaidDataStorage newI) { i = newI; }

    public MassiveList<RaidData> raidData = new MassiveList<>();
    public MassiveList<RaidtimerShield> phaseThreeShieldFactions = new MassiveList<>();

    public MassiveList<RaidtimerShield> getPhaseThreeShieldFactions()
    {
        return phaseThreeShieldFactions;
    }

    public void setPhaseThreeShieldFactions(MassiveList<RaidtimerShield> phaseThreeShieldFactions)
    {
        this.phaseThreeShieldFactions = phaseThreeShieldFactions;
        this.changed();
    }

    public RaidtimerShield getFactionRaidtimerShield(Faction faction)
    {
        return phaseThreeShieldFactions.stream().filter(raidtimerShield -> raidtimerShield.getFactionShieldedId().equals(faction.getId())).findFirst().orElse(null);
    }

    public void addRaidtimerShield(Faction faction) {
        this.phaseThreeShieldFactions.add(new RaidtimerShield(faction.getId(), System.currentTimeMillis()));
        this.changed();
    }

    public static RaidDataStorage get()
    {
        return i;
    }

    public MassiveList<RaidData> getRaidData()
    {
        return raidData;
    }

    public void setRaidData(MassiveList<RaidData> raidData)
    {
        this.raidData = raidData;
        this.changed();
    }

    public void startRaid(Faction raidingFaction, Faction raidedFaction)
    {
        this.raidData.add(new RaidData(raidingFaction.getId(), raidedFaction.getId(), System.currentTimeMillis(), raidedFaction.getFactionValue()));
        this.changed();
    }

    public RaidtimerShield isPhaseThreeProtected(Faction raidedFaction) {
        return phaseThreeShieldFactions.stream().filter(raidtimerShield -> raidtimerShield.getFactionShieldedId().equals(raidedFaction.getId())).findFirst().orElse(null);
    }

    public RaidData isBeingRaided(Faction raidedFaction)
    {
        return raidData.stream().filter(raidData -> raidData.getFactionRaidedId().equals(raidedFaction.getId())).findFirst().orElse(null);
    }

    public RaidData isFactionRaiding(Faction raidingFaction)
    {
        return raidData.stream().filter(raidData -> raidData.getFactionRaidingId().equals(raidingFaction.getId())).findFirst().orElse(null);
    }

    public void setBreachedAtMillis(Faction raidedFaction) {
        RaidData raidData = isBeingRaided(raidedFaction);
        if (raidData != null) {
            raidData.setBreachedAtMillis(System.currentTimeMillis());
            this.changed();
        }
    }

    @Override
    public RaidDataStorage load(RaidDataStorage that)
    {
        this.setRaidData(that.raidData);
        this.setPhaseThreeShieldFactions(that.phaseThreeShieldFactions);

        return this;
    }

}