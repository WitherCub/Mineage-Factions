package com.massivecraft.factions.cmd.perm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.engine.actions.ActionOpenRelations;
import com.massivecraft.factions.entity.objects.FactionPermission;
import com.massivecraft.factions.entity.FactionPermissions;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.coll.MPermColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CmdFactionsPermGui extends FactionsCommand
{
	private static CmdFactionsPermGui i = new CmdFactionsPermGui();
	public static CmdFactionsPermGui get() { return i; }
	
	public CmdFactionsPermGui()
	{
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (FactionPermissions.get().factionPermissions.isEmpty())
		{
			MPermColl.get().getAll().forEach(mPerm -> FactionPermissions.get().factionPermissions.add(new FactionPermission(mPerm.getName(), Material.ENCHANTED_BOOK)));
		}
		
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(ChatColor.RED + "You can not modify permissions for system factions.");
			return;
		}
		
		if (!MPerm.getPermPerms().has(msender, msenderFaction, true)) return;
		
		ChestGui chestGui = ChestGui.getCreative(Bukkit.createInventory(null, getInventorySize(FactionPermissions.get().factionPermissions.size() - 4), ChatColor.translateAlternateColorCodes('&', FactionPermissions.get().permissionGuiName)));
//		populateGlass(chestGui.getInventory());
		
//		int slot = 9;
		int slot = 0;
		
		for (FactionPermission factionPermission : FactionPermissions.get().factionPermissions)
		{
			if (factionPermission.getmPermType().equalsIgnoreCase("container")) continue;
			if (factionPermission.getmPermType().equalsIgnoreCase("deposit")) continue;
			if (factionPermission.getmPermType().equalsIgnoreCase("claimnear")) continue;
			if (factionPermission.getmPermType().equalsIgnoreCase("flags")) continue;
			//			if (factionPermission.getmPermType().equalsIgnoreCase("access")) continue;
			
			if (factionPermission.getGuiDisplayItem() == null)
			{
				System.out.println(factionPermission.getmPermType());
				continue;
			}
			
			ItemStack item = new ItemStack(factionPermission.getGuiDisplayItem(), 1);
			ItemMeta im = item.getItemMeta();
			
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + factionPermission.getmPermType()));
			im.setLore(Arrays.asList(ChatColor.GRAY + "Click to Modify"));
			
			item.setItemMeta(im);
			
			chestGui.getInventory().setItem(slot, item);
			chestGui.setAction(slot, new ActionOpenRelations(factionPermission.getmPermType(), msender));
			
			slot++;
		}
		
		msender.getPlayer().openInventory(chestGui.getInventory());
	}
	
	private int getInventorySize(int max)
	{
		if (max <= 0) return 9;
		int i = (int) Math.ceil(max / 9.0);
		return i > 5 ? 54 : i * 9;
	}
	
	public void populateGlass(Inventory inv)
	{
		ItemStack glass = new ItemStack(FactionPermissions.get().outLineGuiMaterial, 1);
		ItemMeta im = glass.getItemMeta();
		
		im.setDisplayName(" ");
		glass.setItemMeta(im);
		
		glass.setDurability((short) FactionPermissions.get().outLineGuiData);
		
		for (int i = 0; i < 9; i++)
		{
			inv.setItem(i, glass);
		}

		for (int i = inv.getSize() - 9; i < inv.getSize(); i++)
		{
			inv.setItem(i, glass);
		}
	}
	
}