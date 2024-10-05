package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdFactionsClear extends FactionsCommand
{
	
	public CmdFactionsClear()
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
		
		if (msenderFaction.getNotificationTimeMinutes() == 0)
		{
			msender.msg(LangConf.get().checkWallNotifsDisabled);
			return;
		}
		
		msenderFaction.setLastCheckedMillis(System.currentTimeMillis());
		msender.msg(LangConf.get().checkWallClearMsg);
	}
	
}