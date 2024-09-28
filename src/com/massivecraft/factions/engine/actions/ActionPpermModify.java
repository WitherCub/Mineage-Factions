package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.mixin.MixinCommand;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ActionPpermModify extends ChestActionAbstract
{
	
	private Faction modifyingFaction;
	private MPlayer player;
	private Boolean newStatus;
	private String mPerm;
	
	public ActionPpermModify(Faction modifyingFaction, MPlayer player, Boolean newStatus, String mPerm)
	{
		this.modifyingFaction = modifyingFaction;
		this.player = player;
		this.newStatus = newStatus;
		this.mPerm = mPerm;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		if (modifyingFaction.isSystemFaction()) return false;
		if (player.getFaction().isSystemFaction()) return false;
		
		if (newStatus == null)
		{
			MixinCommand.get().dispatchCommand(event.getWhoClicked(), "f pperm unset " + mPerm + " " + player.getName());
		}
		else
		{
			MixinCommand.get().dispatchCommand(event.getWhoClicked(), "f pperm set " + mPerm + " " + player.getName() + " " + newStatus);
		}
		
		event.getWhoClicked().closeInventory();
		
		ChestGui gui = ChestGui.getCreative(Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.RED + "Editing " + mPerm + "..."));
		gui.setAutoclosing(true);
		gui.setAutoremoving(true);
		gui.setSoundOpen(null);
		gui.setSoundClose(null);
		
		Faction faction = player.getFaction();
		Boolean status = player.getPlayerPermValue(mPerm);
		
		// slot 0
		ItemStack toggleYes = new ItemStack(Material.STAINED_GLASS_PANE, 1);
		ItemMeta yesMeta = toggleYes.getItemMeta();
		
		yesMeta.setDisplayName(ChatColor.GREEN + "Click to set pperm for " + player.getName());
		
		if (status == null)
		{
			yesMeta.setLore(MUtil.list(ChatColor.GRAY + "This pperm is currently not set.", ChatColor.GRAY + "Click to set this pperm to " + ChatColor.RED + "NOO", " ", ChatColor.GRAY + "Player permissions always override", ChatColor.GRAY + "default faction permissions"));
		}
		else
		{
			yesMeta.setLore(MUtil.list(ChatColor.GRAY + "This pperm is currently set to " + (status ? ChatColor.GREEN + "YES" : ChatColor.RED + "NOO"), ChatColor.GRAY + "Click to set this pperm to " + ChatColor.GREEN + "YES", " ", ChatColor.GRAY + "Player permissions always override", ChatColor.GRAY + "default faction permissions"));
		}
		
		toggleYes.setItemMeta(yesMeta);
		toggleYes.setDurability((short) 5);
		
		gui.getInventory().setItem(0, toggleYes);
		gui.setAction(0, new ActionPpermModify(faction, player, true, this.mPerm));
		
		// slot 2
		ItemStack toggleNo = new ItemStack(Material.STAINED_GLASS_PANE, 1);
		ItemMeta noMeta = toggleNo.getItemMeta();
		
		noMeta.setDisplayName(ChatColor.RED + "Click to set pperm for " + player.getName());
		
		if (status == null)
		{
			noMeta.setLore(MUtil.list(ChatColor.GRAY + "This pperm is currently not set.", ChatColor.GRAY + "Click to set this pperm to " + ChatColor.RED + "NOO", " ", ChatColor.GRAY + "Player permissions always override", ChatColor.GRAY + "default faction permissions"));
		}
		else
		{
			noMeta.setLore(MUtil.list(ChatColor.GRAY + "This pperm is currently set to " + (status ? ChatColor.GREEN + "YES" : ChatColor.RED + "NOO"), ChatColor.GRAY + "Click to set this pperm to " + ChatColor.RED + "NOO", " ", ChatColor.GRAY + "Player permissions always override", ChatColor.GRAY + "default faction permissions"));
		}
		
		toggleNo.setItemMeta(noMeta);
		toggleNo.setDurability((short) 14);
		
		gui.getInventory().setItem(2, toggleNo);
		gui.setAction(2, new ActionPpermModify(faction, player, false, this.mPerm));
		
		// slot 4
		ItemStack unset = new ItemStack(Material.STAINED_GLASS_PANE, 1);
		ItemMeta unsetMeta = unset.getItemMeta();
		
		unsetMeta.setDisplayName(ChatColor.YELLOW + "Click to unset pperm for " + player.getName());
		unsetMeta.setLore(MUtil.list(ChatColor.GRAY + "Click to unset this pperm", " ", ChatColor.GRAY + "Player permissions always override", ChatColor.GRAY + "default faction permissions"));
		
		unset.setItemMeta(unsetMeta);
		unset.setDurability((short) 4);
		
		gui.getInventory().setItem(4, unset);
		gui.setAction(4, new ActionPpermModify(faction, player, null, this.mPerm));
		// end slots
		
		// done
		event.getWhoClicked().openInventory(gui.getInventory());
		
		return true;
	}
}