package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeFactionWarp;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.coll.MPlayerColl;
import com.massivecraft.factions.integration.essentials.EngineEssentials;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.mixin.MixinCommand;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.teleport.DestinationSimple;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.upgrades.WarpUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class CmdFactionsWarp extends FactionsCommand
{
	private static CmdFactionsWarp i = new CmdFactionsWarp();
	public static CmdFactionsWarp get() { return i; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsWarp()
	{
		// Parameters
		this.addParameter(TypeFactionWarp.get(), "warp", "gui");
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(null, TypeString.get(), "password");
		
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction faction = readArgAt(1, msenderFaction);
		
		String password = readArgAt(2, null);
		
		if (!MPerm.getPermWarp().has(msender, faction, true)) return;
		
		String warp = readArgAt(0, null);
		
		if (warp == null)
		{
			me.openInventory(getWarpsInventory(faction));
			return;
		}
		
		PS loc = faction.getWarp(warp);
		
		// MPerm
		if (!MPerm.getPermWarp().has(msender, faction, true))
			return;
		
		if (loc == null)
		{
			msender.msg(Txt.parse("<b>The specified faction warp named <a>%s <b>is not a valid warp.", warp));
			return;
		}
		
		if (faction.getWarpPassword(warp) == null)
		{
			beginWarp(me, warp, loc.asBukkitLocation(true));
			return;
		}
		
		if (faction.getWarpPassword(warp).equals(password))
		{
			beginWarp(me, warp, loc.asBukkitLocation(true));
		}
		else
		{
			msender.msg(Txt.parse("%s <b>entered an incorrect password for the faction warp <a>%s<b>."), msender.describeTo(msender, true), warp);
		}
		
	}
	
	private int getInventorySize(int max)
	{
		if (max <= 0) return 9;
		int i = (int) Math.ceil(max / 9.0);
		return i > 5 ? 54 : i * 9;
	}
	
	public Inventory getWarpsInventory(Faction faction)
	{
		Inventory inv = Bukkit.createInventory(null, getInventorySize((int) (MConf.get().amountOfWarps + WarpUpgrade.getUpgradeValue(faction.getLevel(MissionUpgradeConf.get().warpUpgrade.getUpgradeName())))), ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Warps");
		
		ChestGui chestGui = ChestGui.getCreative(inv);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		int slot = 0;
		for (String warpName : faction.getAllWarps())
		{
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
			ItemMeta im = item.getItemMeta();
			
			im.setDisplayName(Txt.parse(GuiConf.get().warpGuiPanelWarpName.replace("%warpName%", warpName)));
			
			PS warpPs = faction.getWarp(warpName);
			
			if (warpPs == null) continue;
			
			Location warpLocation = warpPs.asBukkitLocation(true);
			String warpPassword = faction.getWarpPassword(warpName);
			
			ArrayList<String> lore = new ArrayList<>();
			
			for (String line : GuiConf.get().warpGuiPanelFormat)
			{
				lore.add(Txt.parse(line.replace("%password%", (warpPassword != null ? "&bYes" : "&cNo")).replace("%world%", warpLocation.getWorld().getName()).replace("%x%", String.valueOf(warpLocation.getBlockX())).replace("%y%", String.valueOf(warpLocation.getBlockY())).replace("%z%", String.valueOf(warpLocation.getBlockZ()))));
			}
			
			im.setLore(lore);
			item.setItemMeta(im);
			
			if (warpPassword != null)
			{
				item.setDurability((short) 14);
			}
			else
			{
				item.setDurability((short) 5);
			}
			
			Material warpItem = faction.getWarpItem(warpName);
			
			if (warpItem != null && warpItem != Material.STAINED_GLASS_PANE)
			{
				item.setType(warpItem);
				item.setDurability((short) 0);
			}
			
			chestGui.getInventory().setItem(slot, item);
			chestGui.setAction(slot, new ActionWarp(warpName, warpPassword));
			
			slot++;
		}
		
		while (slot < (MConf.get().amountOfWarps + WarpUpgrade.getUpgradeValue(faction.getLevel(MissionUpgradeConf.get().warpUpgrade.getUpgradeName()))))
		{
			
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1);
			item.setDurability((short) 8);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Available Warp");
			im.setLore(Collections.singletonList(ChatColor.GRAY + "Use /f setwarp to create a faction warp."));
			item.setItemMeta(im);
			
			chestGui.getInventory().setItem(slot, item);
			slot++;
		}
		
		while (slot < chestGui.getInventory().getSize())
		{
			ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1);
			item.setDurability((short) 14);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.RED + "LOCKED");
			im.setLore(Collections.singletonList(ChatColor.GRAY + "Use /f upgrade to get access to set more faction warps!"));
			item.setItemMeta(im);
			
			chestGui.getInventory().setItem(slot, item);
			slot++;
		}
		
		
		return chestGui.getInventory();
	}
	
	private class ActionWarp extends ChestActionAbstract
	{
		
		private String warpName;
		private String warpPassword;
		
		public ActionWarp(String warpName, String warpPassword)
		{
			this.warpName = warpName;
			this.warpPassword = warpPassword;
		}
		
		@Override
		public boolean onClick(InventoryClickEvent event)
		{
			if (warpName == null)
			{
				return false;
			}
			
			if (warpPassword != null)
			{
				MPlayerColl.get().get(event.getWhoClicked()).setCurrentWarp(warpName);
				MPlayerColl.get().get(event.getWhoClicked()).msg("&eEnter warp password in chat to continue...");
			}
			else
			{
				MixinCommand.get().dispatchCommand(event.getWhoClicked(), "f warp " + warpName);
			}
			
			return true;
			
		}
	}
	
	public void beginWarp(Player player, String warpName, Location toLocation)
	{
		MPlayer mPlayer = MPlayerColl.get().get(player);
		Faction playerFaction = mPlayer.getFaction();
		Faction factionAtLocation = BoardColl.get().getFactionAt(PS.valueOf(toLocation));
		
		if (factionAtLocation == null || factionAtLocation.isSystemFaction())
		{
			mPlayer.sendMessage("&cYou can not warp here as it is no longer owned by your faction!");
			return;
		}
		
		if (!factionAtLocation.getId().equalsIgnoreCase(playerFaction.getId()))
		{
			mPlayer.sendMessage("&cYou can not warp here as it is no longer owned by your faction!");
			return;
		}
		
		try
		{
			EngineEssentials.get().teleport(player, new DestinationSimple(PS.valueOf(toLocation), warpName), MConf.get().warpWarmup);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "An error occured while trying to teleport you...");
		}
	}
	
}