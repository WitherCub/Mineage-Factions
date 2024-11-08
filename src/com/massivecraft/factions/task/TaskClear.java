package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ModuloRepeatTask;
import org.bukkit.entity.Player;

public class TaskClear extends ModuloRepeatTask
{
	private static TaskClear i = new TaskClear();
	
	public static TaskClear get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return MConf.get().checkWallReminderMillis;
	}
	
	@Override
	public void invoke(long l)
	{
		for (Faction faction : FactionColl.get().getAll())
		{
			if (faction.getNotificationTimeMinutes() == 0) continue;
			
			if (faction.getLastCheckedMillis() + (faction.getNotificationTimeMinutes() * 60000) >= System.currentTimeMillis())
				continue;
			
			for (Player player : faction.getOnlinePlayers())
			{
				MPlayer mPlayer = MPlayer.get(player);
				mPlayer.msg(LangConf.get().checkWallsNotifMsg.replace("%notificationTime%", String.valueOf(faction.getNotificationTimeMinutes())));
			}
		}
	}
}