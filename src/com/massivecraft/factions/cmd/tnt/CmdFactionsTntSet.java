package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;

import java.text.NumberFormat;

public class CmdFactionsTntSet extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsTntSet()
	{
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeInteger.get(), "amount");
		this.addRequirements(RequirementHasPerm.get(Perm.TNT_SET));
		this.setVisibility(Visibility.SECRET);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg();
		
		if (!(msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<b>In order to set tnt in storage, this faction must unlock it using /f upgrade.");
			return;
		}
		
		Integer amount = this.readArg();
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		faction.setTnt(amount);
		msender.msg("%s <i>set %s <i>to <a>%s <i>tnt.", msender.describeTo(msender, true), faction.describeTo(msender), numberFormat.format(faction.getTnt()));
	}
}