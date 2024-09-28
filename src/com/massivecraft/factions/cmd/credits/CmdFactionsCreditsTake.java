package com.massivecraft.factions.cmd.credits;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;

import java.text.NumberFormat;

public class CmdFactionsCreditsTake extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsCreditsTake()
	{
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeInteger.get(), "amount");
		this.addRequirements(RequirementHasPerm.get(Perm.CREDITS_TAKE));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		Faction faction = this.readArg();
		Integer amount = this.readArg();
		
		faction.takeCredits(amount);
		msender.msg("%s <i>took <a>%s <i>credits from %s <i>decreasing the faction total to <a>%s <i>credits.", msender.describeTo(msender, true), numberFormat.format(amount), faction.describeTo(msender), numberFormat.format(faction.getCredits()));
		faction.msg("%s <i>took <a>%s <i>credits from %s <i>decreasing the faction total to <a>%s <i>credits.", msender.describeTo(faction, true), numberFormat.format(amount), faction.describeTo(msender), numberFormat.format(faction.getCredits()));
	}
}
