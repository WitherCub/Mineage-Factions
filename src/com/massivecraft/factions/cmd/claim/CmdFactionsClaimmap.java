package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import org.bukkit.ChatColor;

public class CmdFactionsClaimmap extends FactionsCommand
{
	public CmdFactionsClaimmap()
	{
		this.addAliases("cmap");
		this.addParameter(TypeBooleanYes.get(), "on/off", "flip");
	}
	
	public void perform() throws MassiveException
	{
		Boolean value = readArg(!msender.isMapClickToClaim());
		
		if (value)
		{
			if (msender.isMapClickToClaim())
			{
				me.sendMessage(ChatColor.RED + "The clickable (to claim/unclaim) faction map is already " + ChatColor.GREEN + "ON" + ChatColor.YELLOW + ".");
			}
			else
			{
				me.sendMessage(ChatColor.YELLOW + "The clickable (to claim/unclaim) faction map is now " + ChatColor.GREEN + "ON" + ChatColor.YELLOW + ".");
				msender.setMapClickToClaim(true);
			}
		}
		else if (!msender.isMapClickToClaim())
		{
			me.sendMessage(ChatColor.RED + "The clickable (to claim/unclaim) faction map is already " + ChatColor.RED + "OFF" + ChatColor.YELLOW + ".");
		}
		else
		{
			me.sendMessage(ChatColor.YELLOW + "The clickable (to claim/unclaim) faction map is now " + ChatColor.RED + "OFF" + ChatColor.YELLOW + ".");
			
			msender.setMapClickToClaim(false);
		}
	}
}
