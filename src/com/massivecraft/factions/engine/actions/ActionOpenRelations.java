package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.perm.CmdFactionsPerm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
public class ActionOpenRelations extends ChestActionAbstract {
	
	private String mPerm;
	private MPlayer mPlayer;
	
	public ActionOpenRelations(String mPerm, MPlayer mPlayer) {
		this.mPerm = mPerm;
		this.mPlayer = mPlayer;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event) {
		MPerm mPerm = MPerm.get(this.mPerm.toLowerCase());
		Faction faction = mPlayer.getFaction();
		
		if (faction.isSystemFaction()) return false;
		
		mPlayer.getPlayer().closeInventory();
		
		ChestGui gui = ChestGui.getCreative(Bukkit.createInventory(null, 9, ChatColor.RED + "Editing " + mPerm.getName() + "..."));
		gui.setAutoclosing(true);
		gui.setAutoremoving(true);
		gui.setSoundOpen(null);
		gui.setSoundClose(null);

		CmdFactionsPerm.get().cmdFactionsPermGui.populateGlass(gui.getInventory());
		
		int slot = 0;
		
		for (Rel rel : Rel.values()) {
			
			boolean status = faction.isPermitted(mPerm.getName(), rel, null);
			
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1);
			ItemMeta im = item.getItemMeta();
			
			im.setDisplayName((status ? ChatColor.GREEN : ChatColor.RED) + rel.getName());
			im.setLore(Arrays.asList(ChatColor.GRAY + "Click to Toggle."));
			
			item.setItemMeta(im);
			item.setDurability((short) (status ? 5: 14));
			
			gui.getInventory().setItem(slot, item);
			gui.setAction(slot, new ActionRelationModify(mPlayer.getFaction(), mPlayer, !status, this.mPerm, rel.getName()));
			
			slot++;
		}
		
		mPlayer.getPlayer().openInventory(gui.getInventory());
		
		return true;
	}
}