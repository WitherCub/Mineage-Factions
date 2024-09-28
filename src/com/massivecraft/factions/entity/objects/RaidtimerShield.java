package com.massivecraft.factions.entity.objects;

public class RaidtimerShield
{
    private final String factionShieldedId;
    private final Long shieldStartMillis;

    public RaidtimerShield(String factionShieldedId, Long shieldStartMillis)
    {
        this.factionShieldedId = factionShieldedId;
        this.shieldStartMillis = shieldStartMillis;
    }

    public String getFactionShieldedId()
    {
        return factionShieldedId;
    }

    public Long getShieldStartMillis()
    {
        return shieldStartMillis;
    }
}