package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import org.bukkit.entity.Player;

public class CmdFactionsUnignore extends FactionsCommand
{
	
	public CmdFactionsUnignore()
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
		
		if (msender.getIgnoredPlayerUuids().contains(player.getUniqueId().toString()))
		{
			msender.unignorePlayer(player.getUniqueId().toString());
			msg("<i>You have unignored player %s in factions chat.", player.getName());
		}
		else
		{
			msg("<i>You do not have player %s ignored in factions chat.", player.getName());
		}
	}
	
}