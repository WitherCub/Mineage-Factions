package com.massivecraft.factions.cmd.flag;

import com.massivecraft.factions.cmd.CmdFactionsRank;
import com.massivecraft.factions.cmd.FactionsCommand;

public class CmdFactionsFlag extends FactionsCommand
{
	private static CmdFactionsFlag i = new CmdFactionsFlag();
	public static CmdFactionsFlag get() { return i; }
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	CmdFactionsFlagList cmdFactionsFlagList = new CmdFactionsFlagList();
	CmdFactionsFlagShow cmdFactionsFlagShow = new CmdFactionsFlagShow();
	CmdFactionsFlagSet cmdFactionsFlagSet = new CmdFactionsFlagSet();

}
