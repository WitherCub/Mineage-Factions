package com.massivecraft.factions.task;

import com.massivecraft.factions.cmd.CmdFactionsShield;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.ModuloRepeatTask;

public class TaskUpdateShield extends ModuloRepeatTask
{
	private static TaskUpdateShield i = new TaskUpdateShield();
	
	public static TaskUpdateShield get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return 1000;
	}
	
	@Override
	public void invoke(long l)
	{
		for (Faction faction : FactionColl.get().getAll())
		{
			if (faction.getShieldedHoursChangeRequestMillis() == null || faction.getShieldedHoursChangeRequestNewStartTime() == null)
				continue;
			
			if (System.currentTimeMillis() - faction.getShieldedHoursChangeRequestMillis() >= MConf.get().shieldHoursChangeTimeBeforeUpdate)
			{
				String startHour = CmdFactionsShield.get().getTimeFormatted(faction.getShieldedHoursChangeRequestNewStartTime(), 0);
				String endHour = CmdFactionsShield.get().getTimeFormatted(MConf.get().shieldStartEndHours.get(faction.getShieldedHoursChangeRequestNewStartTime()), 0);
				faction.msg(MConf.get().shieldHoursUpdatedMsg.replace("%startHour%", startHour).replace("%endHour%", endHour));
				faction.setShieldedHoursStartTime(faction.getShieldedHoursChangeRequestNewStartTime());
				faction.requestChangeShieldedHours(null, null);
			}
		}
	}
	
}