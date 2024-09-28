package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeFactionWarp;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsDeletewarp extends FactionsCommand
{
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsDeletewarp()
	{
		// Aliases
		this.addAliases("delwarp");
		
		// Parameters
		this.addParameter(TypeFactionWarp.get(), "warp");
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = readArgAt(1, msenderFaction);
		
		if (!MPerm.getPermSetwarp().has(msender, faction, true))
			return;
		
		String warpName = readArgAt(0);
		
		boolean deleted = faction.deleteWarp(warpName);
		
		if (deleted) {
			msenderFaction.msg(Txt.parse("%s <i>deleted the faction warp %s<i>.", msender.describeTo(msenderFaction), warpName));
		} else {
			msender.msg(Txt.parse("<b>Invalid warp name!"));
		}
	}
	
}
