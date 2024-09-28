package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CmdFactionsBanlist extends FactionsCommand
{
	
	@Override
	public void perform() throws MassiveException
	{
		if (!MPerm.getPermBan().has(msender, msenderFaction, true)) return;
		
		ArrayList<String> bans = new ArrayList<>();
		
		for (String uuid : msenderFaction.getBannedPlayerUuids())
		{
			bans.add(MPlayer.get(uuid).getName());
		}
		
		msender.msg(Txt.parse("<a>Showing bans for %s:", msenderFaction.describeTo(msender, true)));
		
		StringBuilder sb = new StringBuilder();
		
		if (!bans.isEmpty())
		{
			sb.append(ChatColor.YELLOW).append(String.join(ChatColor.GOLD + ", " + ChatColor.YELLOW, bans));
		}
		
		msender.message(sb.toString());
	}
	
}