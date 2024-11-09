package com.massivecraft.factions.cmd.chest;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.integration.autorestart.IntegrationAutoRestart;
import com.massivecraft.factions.integration.autorestart.TaskAutoRestart;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdFactionsChestOpen extends FactionsCommand
{
	
	public CmdFactionsChestOpen()
	{
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg(msenderFaction);

		if(IntegrationAutoRestart.get().isActive() && TaskAutoRestart.get().isRestarting) {
			msender.msg("<rose>Cannot access due to server restarting.");
			return;
		}
		
		if (!(faction.getLevel(MissionUpgradeConf.get().factionChestUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<b>In order to use your virtual chest storage, you must unlock it using /f upgrade.");
			return;
		}
		
		if (MPerm.getPermChest().has(msender, faction, true))
		{
			me.openInventory(faction.getInventory());
		}
	}
	
}
