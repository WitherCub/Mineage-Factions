package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FactionsCommand extends MassiveCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public MPlayer msender;
	public Faction msenderFaction;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsCommand()
	{
		this.setSetupEnabled(true);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void senderFields(boolean set)
	{
		this.msender = set ? MPlayer.get(sender) : null;
		this.msenderFaction = set ? this.msender.getFaction() : null;
	}
	
	@Override
	public Mson getTemplate(boolean addDesc, boolean onlyFirstAlias, CommandSender sender)
	{
		// Create Ret
		Mson ret = TEMPLATE_CORE;
		
		// Get commands
		List<MassiveCommand> commands = this.getChain(true);
		
		// Add commands
		boolean first = true;
		for (MassiveCommand command : commands)
		{
			Mson mson = mson(Collections.min(command.getAliases(), Comparator.comparing(String::length)));
			
			if (sender != null && !command.isRequirementsMet(sender, false))
			{
				mson = mson.color(ChatColor.RED);
			}
			else
			{
				mson = mson.color(ChatColor.AQUA);
			}
			
			if (!first) ret = ret.add(Mson.SPACE);
			ret = ret.add(mson);
			first = false;
		}
		
		// Check if last command is parentCommand and make command suggestable/clickable
		if (commands.get(commands.size() - 1).isParent())
		{
			ret = ret.command(this);
		}
		else
		{
			ret = ret.suggest(this);
		}
		
		// Add args
		for (Mson parameter : this.getTemplateParameters(sender))
		{
			ret = ret.add(Mson.SPACE);
			ret = ret.add(parameter.color(ChatColor.DARK_AQUA));
		}
		
		// Add desc
		if (addDesc)
		{
			ret = ret.add(Mson.SPACE);
			ret = ret.add(mson(this.getDesc()).color(ChatColor.YELLOW));
		}
		
		// Return Ret
		return ret;
	}
	
}
