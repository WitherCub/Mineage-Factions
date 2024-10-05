package com.massivecraft.factions.coll;

import com.massivecraft.factions.entity.FactionPermissions;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;
public class FactionPermissionsColl extends Coll<FactionPermissions>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionPermissionsColl i = new FactionPermissionsColl();
	public static FactionPermissionsColl get() { return i; }
	
	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		if (!active) return;
		FactionPermissions.set(this.get(MassiveCore.INSTANCE, true));
	}
	
}