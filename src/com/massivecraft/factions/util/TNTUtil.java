package com.massivecraft.factions.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TNTUtil
{
	
	public static int getAmountOfNotTNT(Inventory inventory)
	{
		int i = 0;
		
		for (ItemStack is : inventory.getContents())
		{
			if (is != null)
			{
				if (is.getType() == Material.TNT && (!(is.hasItemMeta() || is.getItemMeta().hasDisplayName() || is.getItemMeta().hasLore())))
					continue;
				
				i += 64;
			}
		}
		
		return i;
	}
	
	public static int getAmountOfTNT(Inventory inventory)
	{
		int i = 0;
		
		for (ItemStack is : inventory.getContents())
		{
			if (is != null && is.getType() == Material.TNT)
			{
				if (is.hasItemMeta() || is.getItemMeta().hasDisplayName() || is.getItemMeta().hasLore()) continue;
				i += is.getAmount();
			}
		}
		
		return i;
	}
	
}
