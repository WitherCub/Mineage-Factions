package com.massivecraft.factions.entity.objects;

import com.massivecraft.factions.entity.Faction;

public class FactionBreach
{

    public Long timeOfBreach;
    public Integer valueLost;
    private String factionBreachedId;
    private String factionBreachingId;
    private String factionBreachedName;
    private String factionBreachingName;

    public FactionBreach(Faction factionBreached, Faction factionBreaching, Long timeOfBreach, Integer valueLost)
    {
        this.factionBreachedId = factionBreached.getId();
        this.factionBreachingId = factionBreaching.getId();
        this.factionBreachedName = factionBreached.getName();
        this.factionBreachingName = factionBreaching.getName();
        this.timeOfBreach = timeOfBreach;
        this.valueLost = valueLost;
    }

    public String getFactionBreachedId()
    {
        return factionBreachedId;
    }

    public String getFactionBreachingId()
    {
        return factionBreachingId;
    }

    public String getFactionBreachedName()
    {
        // Get faction
        Faction faction = Faction.get(factionBreachedId);
        // If faction exists get latest name else return latest cached name
        return faction == null ? factionBreachedName : faction.getName();
    }

    public void setFactionBreachedName(String factionBreachedName)
    {
        this.factionBreachedName = factionBreachedName;
    }

    public String getFactionBreachingName()
    {
        // Get faction
        Faction faction = Faction.get(factionBreachingId);
        // If faction exists get latest name else return latest cached name
        return faction == null ? factionBreachingName : faction.getName();
    }

    public void setFactionBreachingName(String factionBreachingName)
    {
        this.factionBreachingName = factionBreachingName;
    }

}