package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdFactionsCoords extends FactionsCommand
{
	
	public CmdFactionsCoords()
	{
		this.addAliases("coords", "location");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg("<b>You must be in a faction to use this command.");
			return;
		}
		
		msenderFaction.msg("<i>%s <i>has pinged their location at <k>%s, <k>%s, <k>%s <i>in <k>%s<i>!", msender.describeTo(msenderFaction, true), me.getLocation().getBlockX(), me.getLocation().getBlockY(), me.getLocation().getBlockZ(), me.getLocation().getWorld().getName());
	}
	
}