package com.massivecraft.factions.cmd.credits;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;

import java.text.NumberFormat;

public class CmdFactionsCreditsBalance extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsCreditsBalance()
	{
		this.addAliases("bal");
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		Faction faction = this.readArg(msenderFaction);
		
		msender.msg("%s <i>has a total of <a>%s <i>factions credits.", faction.describeTo(msender, true), numberFormat.format(faction.getCredits()));
	}
}