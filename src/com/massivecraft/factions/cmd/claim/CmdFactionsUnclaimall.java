package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.engine.actions.ActionCloseInventory;
import com.massivecraft.factions.engine.actions.ActionUnclaimall;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class CmdFactionsUnclaimall extends FactionsCommand
{
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnclaimall()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction faction = this.readArg(msenderFaction);
		
		// MPerm
		if (!MPerm.getPermTerritory().has(msender, faction, true)) return;
		
		me.openInventory(getConfirmInventory(faction).getInventory());
	}
	
	private ChestGui getConfirmInventory(Faction faction)
	{
		Inventory inventory = Bukkit.createInventory(null, 27, Txt.parse("<g>Confirmation"));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		chestGui.getInventory().setItem(4, new ItemBuilder(Material.WATCH).name(Txt.parse("&cUnclaim all land")).lore(Txt.parse("&7Upon confirmation all land")).lore(Txt.parse("&7owned by %faction% will be unclaimed!".replace("%faction%", faction.getName()))));
		
		chestGui.getInventory().setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED + "Deny").durability(14));
		chestGui.setAction(11, new ActionCloseInventory());
		chestGui.getInventory().setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.GREEN + "Confirm").durability(5));
		chestGui.setAction(15, new ActionUnclaimall(faction, sender));
		
		return chestGui;
	}
	
}