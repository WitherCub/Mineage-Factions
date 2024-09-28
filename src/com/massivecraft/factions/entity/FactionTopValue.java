package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.entity.EntityType;

import java.util.Map;

@EditorName("config")
public class FactionTopValue extends Entity<FactionTopValue> {

	private static FactionTopValue i;
	public static void set(FactionTopValue newI) { i = newI; }

	public Map<EntityType, Long> spawnerValues = MUtil.map(EntityType.CREEPER, 10000L);

	public static FactionTopValue get() {
		return i;
	}

	@Override
	public FactionTopValue load(FactionTopValue that) {
		super.load(that);
		this.setSpawnerValues(that.spawnerValues);
		return this;
	}

	public Map<EntityType, Long> getSpawnerValues() {
		return spawnerValues;
	}

	public void setSpawnerValues(Map<EntityType, Long> spawnerValues) {
		this.spawnerValues = spawnerValues;
	}
}