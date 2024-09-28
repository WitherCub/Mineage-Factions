package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import org.bukkit.ChatColor;

public class CmdFactionsCheckpaypal extends FactionsCommand
{
	
	public CmdFactionsCheckpaypal()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		
		final Faction faction = this.readArg(msenderFaction);
		
		if (faction.isSystemFaction())
		{
			msender.msg(ChatColor.RED + "You can not check a system's factions paypal!");
			return;
		}
		
		if (!msender.isOverriding())
		{
			if (!faction.getId().equalsIgnoreCase(msender.getFaction().getId()))
			{
				msender.msg(ChatColor.RED + "You may not check another faction's paypal!");
				return;
			}
			
			if (!faction.getLeader().getUuid().toString().equalsIgnoreCase(msender.getUuid().toString()))
			{
				msender.msg("You may not check this faction's paypal!");
				return;
			}
		}
		
		msender.msg(MConf.get().checkFactionPaypalMsg.replace("%factionName%", faction.getName()).replace("%paypal%", faction.getFactionPayPal()));
	}
}