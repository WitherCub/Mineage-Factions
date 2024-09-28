package com.massivecraft.factions.engine.actions;

import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionCloseInventory extends ChestActionAbstract
{
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		event.getWhoClicked().closeInventory();
		return true;
	}
	
}
