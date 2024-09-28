package com.massivecraft.factions.cmd.alt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsAltsClose extends FactionsCommand
{
	
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You must be in a faction to use this command!"));
			return;
		}
		
		if (!MPerm.getPermFlags().has(msender, msenderFaction, true)) return;
		
		if (msenderFaction.isAltsOpen())
		{
			msenderFaction.msg("<b>Your faction is no longer joinable using /f altjoin by those without an invitation.");
			msenderFaction.setAltsOpen(false);
		}
		else
		{
			msenderFaction.msg("<b>Your faction is already closed for alts without an invitation.");
		}
	}
	
}