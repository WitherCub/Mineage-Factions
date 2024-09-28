package com.massivecraft.factions.entity.objects;

import com.massivecraft.factions.entity.FactionTopValue;
import com.massivecraft.factions.entity.FactionValueSpawnerTypeValue;
import com.massivecraft.massivecore.collections.MassiveSet;
import org.bukkit.entity.EntityType;

public class FactionValue {

	private final String factionID;
	private MassiveSet<FactionValueSpawnerTypeValue> spawnerValues;
	private long totalPotentialValue;
	private long totalSpawnerValue;

	public FactionValue(String factionID, MassiveSet<FactionValueSpawnerTypeValue> spawnerValues, long totalPotentialValue, long totalSpawnerValue) {
		this.factionID = factionID;
		this.spawnerValues = spawnerValues;
		this.totalPotentialValue = totalPotentialValue;
		this.totalSpawnerValue = totalSpawnerValue;
	}

	public MassiveSet<FactionValueSpawnerTypeValue> getSpawnerValues() {
		return spawnerValues;
	}

	public void setSpawnerValues(MassiveSet<FactionValueSpawnerTypeValue> spawnerValues) {
		this.spawnerValues = spawnerValues;
	}

	public void updateTotalValue() {
		totalPotentialValue = 0;

		spawnerValues.forEach(factionValueSpawnerTypeValue -> totalSpawnerValue = totalSpawnerValue + factionValueSpawnerTypeValue.getTotalSpawnerValues());
		spawnerValues.forEach(factionValueSpawnerTypeValue -> totalPotentialValue = totalPotentialValue + (factionValueSpawnerTypeValue.getAmountOfSpawners() * FactionTopValue.get().getSpawnerValues().get(factionValueSpawnerTypeValue.getEntityType())));
	}

	public FactionValueSpawnerTypeValue getSpawnerValueEntityType(EntityType entityType) {
		return spawnerValues.stream().filter(factionValueSpawnerTypeValue -> entityType == factionValueSpawnerTypeValue.getEntityType()).findFirst().orElse(null);
	}

	public String getFactionID() {
		return factionID;
	}

	public long getTotalSpawnerValue() {
		return totalSpawnerValue;
	}

	public void setTotalSpawnerValue(long totalSpawnerValue) {
		this.totalSpawnerValue = totalSpawnerValue;
	}

	public long getTotalPotentialValue() {
		return totalPotentialValue;
	}

}