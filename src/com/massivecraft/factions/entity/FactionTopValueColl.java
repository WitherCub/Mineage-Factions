package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class FactionTopValueColl extends Coll<FactionTopValue> {
	
	private static FactionTopValueColl i;
	
	public static FactionTopValueColl get() { return i; }
	
	@Override
	public void onTick() {
		super.onTick();
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		if (!active) return;
		FactionTopValue.set(this.get(MassiveCore.INSTANCE, true));
	}
	
	static {
		FactionTopValueColl.i = new FactionTopValueColl();
	}
	
}
