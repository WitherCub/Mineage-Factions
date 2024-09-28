package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import gg.halcyon.upgrades.UpgradesManager;

import java.text.NumberFormat;

public class CmdFactionsTntBalance extends FactionsCommand
{
	
	public CmdFactionsTntBalance()
	{
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg(msenderFaction);
		
		if (faction != msenderFaction && !Perm.TNT_BALANCE_OTHERS.has(sender, true)) return;
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		if (!(faction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<b>%s <b>does not have their virtual tnt storage unlocked.", faction.describeTo(msender, true));
		}
		else
		{
			msender.msg("<i>%s <i>has <a>%s/%s <i>TNT", faction.describeTo(msender, true), UpgradesManager.get().getUpgradeByName(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()).getCurrentUpgradeDescription()[faction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) - 1].split(" ")[2], numberFormat.format(faction.getTnt()));
		}
	}
	
}