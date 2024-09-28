package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.store.EntityInternal;
import org.bukkit.entity.EntityType;

import java.util.Set;

public class FactionValueSpawnerTypeValue extends EntityInternal<FactionValueSpawnerTypeValue>
{
	
	private final EntityType entityType;
	private Set<String> factionValueLocationStrings;
	private int amountOfSpawners;
	private long totalSpawnerValues;
	
	public FactionValueSpawnerTypeValue(EntityType entityType, Set<String> factionValueLocationStrings, int amountOfSpawners, long totalSpawnerValues)
	{
		this.entityType = entityType;
		this.factionValueLocationStrings = factionValueLocationStrings;
		this.amountOfSpawners = amountOfSpawners;
		this.totalSpawnerValues = totalSpawnerValues;
	}
	
	public EntityType getEntityType()
	{
		return entityType;
	}
	
	public Set<String> getFactionValueLocationStrings()
	{
		return factionValueLocationStrings;
	}
	
	public void setFactionValueLocationStrings(Set<String> factionValueLocationStrings)
	{
		this.factionValueLocationStrings = factionValueLocationStrings;
		this.changed();
	}
	
	public int getAmountOfSpawners()
	{
		return amountOfSpawners;
	}
	
	public void setAmountOfSpawners(int amountOfSpawners)
	{
		this.amountOfSpawners = amountOfSpawners;
		this.changed();
	}
	
	public long getTotalSpawnerValues()
	{
		return totalSpawnerValues;
	}
	
	public void setTotalSpawnerValues(long totalSpawnerValues)
	{
		this.totalSpawnerValues = totalSpawnerValues;
		this.changed();
	}
}