package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import org.bukkit.entity.Player;

public class CmdFactionsUnban extends FactionsCommand
{
	
	public CmdFactionsUnban()
	{
		this.addRequirements(RequirementIsPlayer.get());
		this.addParameter(TypePlayer.get(), "player");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Player player = this.readArg();
		
		if (msenderFaction == null || msenderFaction.isSystemFaction())
		{
			msg("&cYou must be a part of a faction to use this command!");
			return;
		}
		
		if (!MPerm.getPermBan().has(msender, msenderFaction, true)) return;
		
		if (!msenderFaction.getBannedPlayerUuids().contains(player.getUniqueId().toString()))
		{
			msenderFaction.unbanPlayer(player.getUniqueId().toString());
			msg("<i>%s has unbanned player %s from your faction.", me.getName(), player.getName());
		}
		else
		{
			msg("<i>%s is not currently banned from your faction.", player.getName());
		}
	}
	
}