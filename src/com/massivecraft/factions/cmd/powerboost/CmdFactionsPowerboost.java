package com.massivecraft.factions.cmd.powerboost;

import com.massivecraft.factions.cmd.FactionsCommand;

public class CmdFactionsPowerboost extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsPowerboostPlayer cmdFactionsPowerboostPlayer = new CmdFactionsPowerboostPlayer();
	public CmdFactionsPowerboostFaction cmdFactionsPowerboostFaction = new CmdFactionsPowerboostFaction();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPowerboost()
	{
		// Child
		this.addChild(this.cmdFactionsPowerboostPlayer);
		this.addChild(this.cmdFactionsPowerboostFaction);
	}
	
}
