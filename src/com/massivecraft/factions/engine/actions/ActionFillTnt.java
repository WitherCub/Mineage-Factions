package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.tnt.CmdFactionsTnt;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionFillTnt extends ChestActionAbstract
{
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		CmdFactionsTnt.get().cmdFactionsTntFill.execute(player, MUtil.list("1", String.valueOf(MConf.get().maximumFillRadius)));
		return false;
	}
	
}