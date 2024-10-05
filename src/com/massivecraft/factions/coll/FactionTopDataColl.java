package com.massivecraft.factions.coll;

import com.massivecraft.factions.entity.FactionTopData;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class FactionTopDataColl extends Coll<FactionTopData> {
	
	private static FactionTopDataColl i;
	
	public static FactionTopDataColl get() { return i; }
	
	@Override
	public void onTick() {
		super.onTick();
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		if (!active) return;
		FactionTopData.set(this.get(MassiveCore.INSTANCE, true));
	}
	
	static {
		FactionTopDataColl.i = new FactionTopDataColl();
	}
	
}
