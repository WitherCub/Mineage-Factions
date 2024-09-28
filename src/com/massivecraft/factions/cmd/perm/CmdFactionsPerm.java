package com.massivecraft.factions.cmd.perm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.massivecore.MassiveException;

import java.util.Collections;

public class CmdFactionsPerm extends FactionsCommand
{

	private static CmdFactionsPerm i = new CmdFactionsPerm();
	public static  CmdFactionsPerm get() { return i; }

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	CmdFactionsPermList cmdFactionsPermList = new CmdFactionsPermList();
	CmdFactionsPermShow cmdFactionsPermShow = new CmdFactionsPermShow();
	CmdFactionsPermSet cmdFactionsPermSet = new CmdFactionsPermSet();
	public CmdFactionsPermGui cmdFactionsPermGui = new CmdFactionsPermGui();
	
	@Override
	public void perform() throws MassiveException
	{
		this.getHelpCommand().execute(this.sender, this.getArgs());
		cmdFactionsPermGui.execute(sender, Collections.emptyList());
	}
	
}
