package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.factions.cmd.req.ReqHasFaction;

public class CmdFactionsLeave extends FactionsCommand
{
	private static CmdFactionsLeave i = new CmdFactionsLeave();
	public static CmdFactionsLeave get() { return i; }
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsLeave()
	{
		// Requirements
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		msender.leave();
	}
	
}
