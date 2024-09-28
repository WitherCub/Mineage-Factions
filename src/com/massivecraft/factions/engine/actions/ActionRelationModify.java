package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.perm.CmdFactionsPermGui;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.mixin.MixinCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ActionRelationModify extends ChestActionAbstract
{
	
	private Faction modifyingFaction;
	private MPlayer player;
	private boolean newStatus;
	private String mPerm;
	private String relation;
	
	public ActionRelationModify(Faction modifyingFaction, MPlayer player, boolean newStatus, String mPerm, String relation)
	{
		this.modifyingFaction = modifyingFaction;
		this.player = player;
		this.newStatus = newStatus;
		this.mPerm = mPerm;
		this.relation = relation;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		if (modifyingFaction.isSystemFaction()) return false;
		if (player.getFaction().isSystemFaction()) return false;
		
		MixinCommand.get().dispatchCommand(event.getWhoClicked(), "f perm set " + mPerm + " " + relation + " " + newStatus);
		
		event.getWhoClicked().closeInventory();
		
		ChestGui gui = ChestGui.getCreative(Bukkit.createInventory(null, 9, ChatColor.RED + "Editing " + mPerm + "..."));
		gui.setAutoclosing(true);
		gui.setAutoremoving(true);
		gui.setSoundOpen(null);
		gui.setSoundClose(null);
		
		CmdFactionsPermGui.get().populateGlass(gui.getInventory());
		
		int slot = 0;
		
		Faction faction = player.getFaction();
		for (Rel rel : Rel.values())
		{
			
			boolean status = faction.isPermitted(mPerm, rel, null);
			
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1);
			ItemMeta im = item.getItemMeta();
			
			im.setDisplayName((status ? ChatColor.GREEN : ChatColor.RED) + rel.getName());
			im.setLore(Arrays.asList(ChatColor.GRAY + "Click to Toggle."));
			
			item.setItemMeta(im);
			item.setDurability((short) (status ? 5 : 14));
			
			gui.getInventory().setItem(slot, item);
			gui.setAction(slot, new ActionRelationModify(faction, player, !status, this.mPerm, rel.getName()));
			
			slot++;
		}
		
		event.getWhoClicked().openInventory(gui.getInventory());
		
		return true;
	}
}