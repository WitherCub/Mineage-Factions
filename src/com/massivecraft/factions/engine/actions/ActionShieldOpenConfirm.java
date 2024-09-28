package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ActionShieldOpenConfirm extends ChestActionAbstract
{
	
	private Faction faction;
	private int startHour;
	private String playerStr;
	private String factionStr;
	private String startTime;
	private String endTime;
	private String action;
	
	public ActionShieldOpenConfirm(String action, Faction faction, int startHour, String playerStr, String factionStr, String startTime, String endTime)
	{
		this.action = action;
		this.faction = faction;
		this.startHour = startHour;
		this.playerStr = playerStr;
		this.factionStr = factionStr;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event, Player player)
	{
		player.openInventory(getConfirmInventory(event.getCurrentItem()).getInventory());
		return true;
	}
	
	private ChestGui getConfirmInventory(ItemStack itemStack)
	{
		Inventory inventory = Bukkit.createInventory(null, 27, Txt.parse("<g>Confirmation"));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		if (itemStack.getType() == Material.STAINED_GLASS_PANE)
		{
			ItemStack clone = new ItemBuilder(itemStack).type(Material.WATCH).durability(0);
			chestGui.getInventory().setItem(4, clone);
		}
		else
		{
			chestGui.getInventory().setItem(4, itemStack);
		}
		
		chestGui.getInventory().setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED + "Deny").durability(14));
		chestGui.setAction(11, new ActionCloseInventory());
		chestGui.getInventory().setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.GREEN + "Confirm").durability(5));
		
		if (action.equals("Disable"))
		{
			chestGui.setAction(15, new ActionDisableShield(faction, playerStr, factionStr));
		}
		else if (action.equals("Set"))
		{
			chestGui.setAction(15, new ActionSetShieldedHour(faction, startHour, playerStr, factionStr, startTime, endTime));
		}
		
		return chestGui;
	}
	
}