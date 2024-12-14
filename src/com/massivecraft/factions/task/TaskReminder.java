package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.Txt;

public class TaskReminder extends ModuloRepeatTask {
	
	private static TaskReminder i = new TaskReminder();
	
	public static TaskReminder get() {
		return i;
	}
	
	@Override
	public long getDelayMillis() {
		return MConf.get().reminderDelayMillis;
	}
	
	@Override
	public void invoke(long l) {
		for (Faction faction : FactionColl.get().getAll()) {
			if (faction.isSystemFaction()) continue;
			
			if (faction.getBaseRegionPs() == null || faction.getBaseRegionPs().isEmpty()) {
				faction.msg(Txt.parse(LangConf.get().setBaseRegionReminderMsg));
			}
		}
	}
}