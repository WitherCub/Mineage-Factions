package com.massivecraft.factions.cmd.credits;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;

import java.text.NumberFormat;

public class CmdFactionsCreditsAdd extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsCreditsAdd()
	{
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeInteger.get(), "amount");
		this.addRequirements(RequirementHasPerm.get(Perm.CREDITS_ADD));
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
		
		faction.addCredits(amount);
		msender.msg("%s <i>added <a>%s <i>credits to %s <i>increasing the faction total to <a>%s <i>credits.", msender.describeTo(msender, true), numberFormat.format(amount), faction.describeTo(msender), numberFormat.format(faction.getCredits()));
		faction.msg("%s <i>added <a>%s <i>credits to %s <i>increasing the faction total to <a>%s <i>credits.", msender.describeTo(faction, true), numberFormat.format(amount), faction.describeTo(msender), numberFormat.format(faction.getCredits()));
	}
}
