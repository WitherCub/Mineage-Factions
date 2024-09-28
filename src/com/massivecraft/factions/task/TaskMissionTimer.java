package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.entity.Player;

public class TaskMissionTimer extends ModuloRepeatTask
{
	
	private static TaskMissionTimer i = new TaskMissionTimer();
	
	public static TaskMissionTimer get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return TimeUnit.MILLIS_PER_MINUTE;
	}
	
	@Override
	public void invoke(long l)
	{
		for (Faction faction : FactionColl.get().getAll())
		{
			if (faction.getActiveMission() == null) continue;
			
			if (System.currentTimeMillis() - faction.getMissionStartTime() >= MConf.get().missionTimerHours)
			{
				for (Player player : faction.getOnlinePlayers())
				{
					if (player.getInventory().getTitle().equalsIgnoreCase(Txt.parse("&aMissions")))
					{
						player.closeInventory();
					}
					
					MixinMessage.get().msgOne(player, "&cYour faction was unable to complete the current mission. Better luck next time!");
				}
				
				faction.setMissionStartTime(0);
				faction.setMissionRequirementComplete(0);
				faction.setActiveMission(null);
				faction.setSpinningMission(false);
			}
		}
	}
}