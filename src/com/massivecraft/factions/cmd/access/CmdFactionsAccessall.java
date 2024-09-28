package com.massivecraft.factions.cmd.access;

import com.massivecraft.factions.cmd.FactionsCommand;

public class CmdFactionsAccessall extends FactionsCommand
{
	public CmdFactionsAccessall() {
		this.addChild(new CmdFactionsAccessallPlayer());
		this.addChild(new CmdFactionsAccessallFaction());
	}
}
