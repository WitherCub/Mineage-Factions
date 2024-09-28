package com.massivecraft.factions.util;

import com.massivecraft.factions.engine.actions.ActionPpermChoosePlayer;
import com.massivecraft.factions.engine.actions.ActionSwitchPage;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ScrollerInventory
{
	
	public static final String nextPageName = Txt.parse(MConf.get().pageableGuiNextPageIconName);
	public static final String previousPageName = Txt.parse(MConf.get().pageableGuiPreviousPageIconName);
	public static HashMap<UUID, ScrollerInventory> users = new HashMap<>();
	public ArrayList<ChestGui> pages = new ArrayList<>();
	public UUID id;
	public int currpage = 0;
	
	public ScrollerInventory(String name, MPlayer mPlayer, Faction faction)
	{
		this.id = UUID.randomUUID();
		
		ChestGui page = getBlankPage(name, faction);
		
		int slot = 0;
		
		for (MPlayer playerInFaction : faction.getMPlayers())
		{
			if (playerInFaction.isAlt()) continue;
			
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(playerInFaction.getName());
			meta.setDisplayName(ChatColor.YELLOW + playerInFaction.getName());
			meta.setLore(MUtil.list(ChatColor.GRAY + "Rank: " + ChatColor.GREEN + playerInFaction.getRelationTo(faction).getName()));
			skull.setItemMeta(meta);
			
			if (page.getInventory().firstEmpty() == 46)
			{
				pages.add(page);
				page = getBlankPage(name, faction);
				slot = 0;
				page.getInventory().addItem(skull);
			}
			else
			{
				page.getInventory().addItem(skull);
			}
			
			page.setAction(slot, new ActionPpermChoosePlayer(playerInFaction));
			
			slot++;
		}
		
		pages.add(page);
		
		mPlayer.getPlayer().openInventory(pages.get(currpage).getInventory());
		
		users.put(mPlayer.getPlayer().getUniqueId(), this);
	}
	
	private ChestGui getBlankPage(String name, Faction faction)
	{
		Inventory page = Bukkit.createInventory(null, 54, name);
		
		ChestGui chestGui = ChestGui.getCreative(page);
		chestGui.setAutoclosing(false);
		chestGui.setAutoremoving(false);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		int players = 0;
		
		for (MPlayer mPlayer : faction.getMPlayers())
		{
			if (mPlayer.isAlt()) continue;
			players++;
		}
		
		if (players < 45) return chestGui;
		
		ItemStack nextpage = new ItemBuilder(MConf.get().pageableGuiNextPageIconMaterial).durability(MConf.get().pageableGuiNextPageIconDurability).name(nextPageName);
		ItemMeta meta = nextpage.getItemMeta();
		meta.setDisplayName(nextPageName);
		nextpage.setItemMeta(meta);
		
		ItemStack prevpage = new ItemBuilder(MConf.get().pageableGuiPreviousPageIconMaterial).durability(MConf.get().pageableGuiPreviousPageIconDurability).name(previousPageName);
		meta = prevpage.getItemMeta();
		meta.setDisplayName(previousPageName);
		prevpage.setItemMeta(meta);
		
		chestGui.getInventory().setItem(53, nextpage);
		chestGui.setAction(53, new ActionSwitchPage());
		chestGui.getInventory().setItem(45, prevpage);
		chestGui.setAction(45, new ActionSwitchPage());
		
		return chestGui;
	}
}