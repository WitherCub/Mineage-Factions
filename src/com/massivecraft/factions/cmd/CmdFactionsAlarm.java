package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdFactionsAlarm extends FactionsCommand
{
	
	public CmdFactionsAlarm()
	{
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg("&bYou must join a faction to use this command!");
			return;
		}
		
		if (!MPerm.getPermCheck().has(msender, msenderFaction, true)) return;
		
		if (msenderFaction.isAlarmEnabled())
		{
			msenderFaction.msg(LangConf.get().alarmDisabledMsg.replace("%player%", msender.getName()));
			msenderFaction.setAlarmEnabled(false);
		}
		else
		{
			msenderFaction.msg(LangConf.get().alarmSoundedMsg.replace("%player%", msender.getName()));
			msenderFaction.setAlarmEnabled(true);
		}
	}
	
}