package com.massivecraft.factions.cmd.chest;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.massivecore.MassiveException;

import java.util.Collections;

public class CmdFactionsChest extends FactionsCommand
{
	
	public CmdFactionsChestOpen cmdFactionsChestOpen = new CmdFactionsChestOpen();
	public CmdFactionsChestLog cmdFactionsChestLog = new CmdFactionsChestLog();
	
	@Override
	public void perform() throws MassiveException
	{
		this.getHelpCommand().execute(this.sender, this.getArgs());
		cmdFactionsChestOpen.execute(sender, Collections.emptyList());
	}
}
