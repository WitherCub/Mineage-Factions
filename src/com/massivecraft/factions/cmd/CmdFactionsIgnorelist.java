package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CmdFactionsIgnorelist extends FactionsCommand
{
	
	@Override
	public void perform() throws MassiveException
	{
		ArrayList<String> ignores = new ArrayList<>();
		
		for (String uuid : msender.getIgnoredPlayerUuids())
		{
			ignores.add(MPlayer.get(uuid).getName());
		}
		
		msender.msg(Txt.parse("<a>Showing your players ignored in faction chat:"));
		
		StringBuilder sb = new StringBuilder();
		
		if (!ignores.isEmpty())
		{
			sb.append(ChatColor.YELLOW).append(String.join(ChatColor.GOLD + ", " + ChatColor.YELLOW, ignores));
		}
		
		msender.message(sb.toString());
	}
	
}