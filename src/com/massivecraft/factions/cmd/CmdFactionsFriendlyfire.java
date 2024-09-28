package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import org.bukkit.ChatColor;

public class CmdFactionsFriendlyfire extends FactionsCommand
{
	public CmdFactionsFriendlyfire()
	{
		this.addParameter(TypeBooleanYes.get(), "on/off", "flip");
	}
	
	public void perform() throws MassiveException
	{
		Boolean value = readArg(!msender.isFriendlyFire());
		
		if (value)
		{
			if (msender.isFriendlyFire())
			{
				me.sendMessage(ChatColor.RED + "Friendly fire is already " + ChatColor.GREEN + "ON" + ChatColor.YELLOW + ".");
			}
			else
			{
				me.sendMessage(ChatColor.YELLOW + "Friendly fire is now " + ChatColor.GREEN + "ON" + ChatColor.YELLOW + ".");
				msender.setFriendlyFire(true);
			}
		}
		else if (!msender.isFriendlyFire())
		{
			me.sendMessage(ChatColor.RED + "Friendly fire is already " + ChatColor.RED + "OFF" + ChatColor.YELLOW + ".");
		}
		else
		{
			me.sendMessage(ChatColor.YELLOW + "Friendly fire is now " + ChatColor.RED + "OFF" + ChatColor.YELLOW + ".");
			
			msender.setFriendlyFire(false);
		}
	}
}
