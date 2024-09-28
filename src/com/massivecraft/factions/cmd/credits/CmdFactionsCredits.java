package com.massivecraft.factions.cmd.credits;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.massivecore.MassiveException;

import java.text.NumberFormat;

public class CmdFactionsCredits extends FactionsCommand
{
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsCreditsAdd cmdFactionsCreditsAdd = new CmdFactionsCreditsAdd();
	public CmdFactionsCreditsTake cmdFactionsCreditsTake = new CmdFactionsCreditsTake();
	public CmdFactionsCreditsGiveitem cmdFactionsCreditsGiveitem = new CmdFactionsCreditsGiveitem();
	public CmdFactionsCreditsBalance cmdFactionsCreditsBalance = new CmdFactionsCreditsBalance();
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		msender.msg("%s <i>has a total of <a>%s <i>factions credits.", msenderFaction.describeTo(msender, true), numberFormat.format(msenderFaction.getCredits()));
	}
}
