package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.money.MoneyMixinVault;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsSetwarp extends FactionsCommand
{
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetwarp()
	{
		// Parameters
		this.addParameter(TypeString.get(), "warp");
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(null, TypeString.get(), "password");
		
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@SuppressWarnings("deprecation")
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction faction = readArgAt(1, msenderFaction);
		
		String password = readArgAt(2, null);
		
		PS newHome = PS.valueOf(me.getLocation());
		
		// MPerm
		if (!MPerm.getPermSetwarp().has(msender, faction, true))
			return;
		
		// Verify
		if (!msender.isOverriding() && !faction.isValidWarp(newHome))
		{
			msender.msg(Txt.parse("<b>Sorry, your faction warps can only be set inside your own claimed territory."));
			return;
		}
		
		if (faction.getAllWarps().size() >= (MConf.get().amountOfWarps + (faction.getLevel(MissionUpgradeConf.get().warpUpgrade.getUpgradeName()) * MissionUpgradeConf.get().warpsPerUpgrade)))
		{
			msender.message(Txt.parse("<b>You cannot set anymore warps for this faction!"));
			return;
		}
		
		if (!new MoneyMixinVault().getEconomy().has(me.getName(), MConf.get().costPerWarp))
		{
			msender.msg(Txt.parse("<b>You need $%s to set a warp.", MConf.get().costPerWarp));
			return;
		}
		
		new MoneyMixinVault().getEconomy().withdrawPlayer(me.getName(), MConf.get().costPerWarp);
		faction.setWarp(newHome, readArgAt(0), password, null);
		
		// Inform
		faction.msg(Txt.parse("%s<i> has created a faction warp named <a>%s<i>.", msender.describeTo(msenderFaction, true), readArgAt(0)));
		if (faction != msenderFaction)
		{
			msender.msg(Txt.parse("%s<i> set a faction warp named <a>%s <i>for %s<i>.", msender.describeTo(msender, true), readArgAt(0), faction.describeTo(msender, true)));
		}
	}
}
