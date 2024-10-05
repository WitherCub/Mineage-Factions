package com.massivecraft.factions.coll;

import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class MissionUpgradeConfColl extends Coll<MissionUpgradeConf>
{
	
	private static MissionUpgradeConfColl i = new MissionUpgradeConfColl();
	
	public static MissionUpgradeConfColl get()
	{
		return i;
	}
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		if (!active) return;
		MissionUpgradeConf.set(this.get(MassiveCore.INSTANCE, true));
	}
	
}