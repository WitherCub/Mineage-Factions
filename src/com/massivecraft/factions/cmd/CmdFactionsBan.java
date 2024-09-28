package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.entity.Player;

public class CmdFactionsBan extends FactionsCommand
{
	
	public CmdFactionsBan()
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
		
		if (msenderFaction.getBannedPlayerUuids().contains(player.getUniqueId().toString()))
		{
			msg("<i>%s is already banned from your faction.", player.getName());
		}
		else
		{
			CmdFactionsKick.get().execute(sender, MUtil.list(player.getName()));
			msenderFaction.banPlayer(player.getUniqueId().toString());
			msg("<i>%s has banned player %s from your faction.", me.getName(), player.getName());
		}
	}
	
}