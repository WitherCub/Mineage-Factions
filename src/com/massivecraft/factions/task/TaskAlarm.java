package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.mixin.MixinMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TaskAlarm extends ModuloRepeatTask
{
	
	private static TaskAlarm i = new TaskAlarm();
	
	public static TaskAlarm get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return MConf.get().alarmDelayMillis;
	}
	
	@Override
	public void invoke(long l)
	{
		for (Faction faction : FactionColl.get().getAll())
		{
			if (!faction.isAlarmEnabled()) continue;
			
			for (Player player : faction.getOnlinePlayers())
			{
				MPlayer mPlayer = MPlayer.get(player);
				
				if (!mPlayer.isAlertSoundEnabled()) continue;
				
				MixinMessage.get().msgOne(player, MConf.get().alarmSoundedMsg.replace("%player%", player.getName()));
				player.playSound(player.getLocation(), Sound.valueOf(MConf.get().alarmSound), MConf.get().alarmVolume, MConf.get().alarmPitch);
			}
		}
	}
}