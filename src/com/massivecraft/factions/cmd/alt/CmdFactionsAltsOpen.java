package com.massivecraft.factions.cmd.alt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsAltsOpen extends FactionsCommand
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
			msender.msg("<b>Your faction is already open for alts. Anyone can currently join using /f altjoin.");
		}
		else
		{
			msenderFaction.msg("%s<i> opened your faction for anyone to join as an alt.", me.getName());
			msenderFaction.setAltsOpen(true);
		}
	}
	
}
