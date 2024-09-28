package com.massivecraft.factions.cmd.access;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdFactionsAccess extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccess()
	{
		this.addChild(new CmdFactionsAccessView());
		this.addChild(new CmdFactionsAccessPlayer());
		this.addChild(new CmdFactionsAccessFaction());
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}
	
}
