package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;

import java.util.UUID;

@EditorName("config")
public class NameReserves extends Entity<NameReserves>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	private static NameReserves i;
	public static void set(NameReserves newI) { i = newI; }

	public MassiveMap<String, UUID> nameReservedForPlayerUuid = new MassiveMap<>();
	
	// -------------------------------------------- //
	// RESERVED NAMES COLL
	// -------------------------------------------- //
	
	public static NameReserves get()
	{
		return i;
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public NameReserves load(NameReserves that)
	{
		super.load(that);
		this.setNameReservedForPlayerUuid(that.nameReservedForPlayerUuid);
		return this;
	}
	
	private void setNameReservedForPlayerUuid(MassiveMap<String, UUID> nameReservedForPlayerUuid)
	{
		this.nameReservedForPlayerUuid = nameReservedForPlayerUuid;
		this.changed();
	}
	
	public void reserveName(String factionName, UUID playerId)
	{
		this.nameReservedForPlayerUuid.put(factionName, playerId);
		this.changed();
	}
	
	public void removeReservedName(String factionName)
	{
		this.nameReservedForPlayerUuid.remove(factionName);
	}
	
	public boolean isNameReserved(String factionName)
	{
		return nameReservedForPlayerUuid.containsKey(factionName);
	}
	
	public UUID getNameReservedForWho(String factionName)
	{
		return nameReservedForPlayerUuid.get(factionName);
	}
	
	public void unreserveName(String factionName)
	{
		nameReservedForPlayerUuid.remove(factionName);
	}
	
	public boolean canPlayerClaimName(String factionName, MPlayer mplayer)
	{
		if (!isNameReserved(factionName)) return true;
		if (mplayer.isOverriding()) return true;
		return nameReservedForPlayerUuid.get(factionName).equals(mplayer.getUuid());
	}
	
}