package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CmdFactionsListwarps extends FactionsCommand
{
	
	public CmdFactionsListwarps()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Faction fac = readArg(msenderFaction);
		
		if (!MPerm.getPermWarp().has(msender, fac, true)) return;
		
		ArrayList<String> warps = fac.getAllWarps();
		
		msender.msg(Txt.parse("<a>Showing warps for %s <a>(%s/%s):", fac.describeTo(msender, true), fac.getAllWarps().size(), (MConf.get().amountOfWarps + (fac.getLevel(MissionUpgradeConf.get().warpUpgrade.getUpgradeName()) * 2))));
		
		StringBuilder sb = new StringBuilder();
		
		if (!warps.isEmpty())
		{
			sb.append(ChatColor.YELLOW).append(String.join(ChatColor.GOLD + ", " + ChatColor.YELLOW, warps));
		}
		
		msender.message(sb.toString());
	}
	
}
