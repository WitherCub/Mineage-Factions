package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.missions.MissionsManager;

public class CmdFactionsMission extends FactionsCommand
{
	
	public CmdFactionsMission()
	{
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You must be in a faction to use this command!"));
			return;
		}
		
		if (msenderFaction.isSpinningMission())
		{
			msender.msg(Txt.parse("<b>Someone is currently spinning a new mission... Please wait..."));
			return;
		}
		
		msender.getPlayer().openInventory(MissionsManager.get().getMissionsInventory(msenderFaction));
	}
}