package com.massivecraft.factions.cmd.alt;

import com.massivecraft.factions.cmd.FactionsCommand;

public class CmdFactionsAlts extends FactionsCommand
{
	
	public CmdFactionsAltsInvite cmdFactionsAltsInvite = CmdFactionsAltsInvite.get();
	public CmdFactionsAltsRevoke cmdFactionsAltsRevoke = CmdFactionsAltsRevoke.get();
	public CmdFactionsAltsInvitelist cmdFactionsAltsInvitelist = new CmdFactionsAltsInvitelist();
	public CmdFactionsAltsList cmdFactionsAltsList = new CmdFactionsAltsList();
	public CmdFactionsAltsOpen cmdFactionsAltsOpen = new CmdFactionsAltsOpen();
	public CmdFactionsAltsClose cmdFactionsAltsClose = new CmdFactionsAltsClose();
	
}
