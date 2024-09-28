package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.util.ScrollerInventory;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionSwitchPage extends ChestActionAbstract
{
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		if (!(event.getWhoClicked() instanceof Player)) return false;
		
		Player p = (Player) event.getWhoClicked();
		
		if (!ScrollerInventory.users.containsKey(p.getUniqueId())) return false;
		
		ScrollerInventory inv = ScrollerInventory.users.get(p.getUniqueId());
		
		if (event.getCurrentItem() == null) return false;
		if (event.getCurrentItem().getItemMeta() == null) return false;
		if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return false;
		if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ScrollerInventory.nextPageName))
		{
			if (inv.currpage < inv.pages.size() - 1)
			{
				inv.currpage += 1;
				p.openInventory(inv.pages.get(inv.currpage).getInventory());
			}
			return true;
		}
		else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ScrollerInventory.previousPageName))
		{
			if (inv.currpage > 0)
			{
				inv.currpage -= 1;
				p.openInventory(inv.pages.get(inv.currpage).getInventory());
			}
			return true;
		}
		
		return false;
	}
	
}