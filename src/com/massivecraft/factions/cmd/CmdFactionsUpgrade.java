package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.UpgradesManager;

public class CmdFactionsUpgrade extends FactionsCommand
{
	
	public CmdFactionsUpgrade()
	{
		this.addAliases("shop");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You must be in a faction to use this command!"));
			return;
		}
		
		msender.getPlayer().openInventory(UpgradesManager.get().getFactionUpgrades(msenderFaction));
	}
}
