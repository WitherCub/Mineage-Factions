package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.claim.CmdFactionsUnclaim;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionUnclaimall extends ChestActionAbstract
{
	
	private Faction faction;
	private CommandSender sender;
	
	public ActionUnclaimall(Faction faction, CommandSender sender)
	{
		this.faction = faction;
		this.sender = sender;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		CmdFactionsUnclaim.get().cmdFactionsUnclaimAll.execute(sender, MUtil.list("all", faction.getName()));
		return true;
	}
}