package com.massivecraft.factions.cmd.pperm;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.engine.actions.ActionOpenPperm;
import com.massivecraft.factions.entity.objects.FactionPermission;
import com.massivecraft.factions.entity.FactionPermissions;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPermColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CmdFactionsPpermGui extends FactionsCommand
{
	private static CmdFactionsPpermGui i = new CmdFactionsPpermGui();
	public static CmdFactionsPpermGui get() { return i; }
	
	public CmdFactionsPpermGui()
	{
		this.addRequirements(RequirementIsPlayer.get());
		this.addParameter(TypeMPlayer.get(), "player");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MPlayer targetMplayer = this.readArg();
		
		if (targetMplayer.isAlt())
		{
			msg("<b>Pperms are not editable for <g>alts<b>.");
			return;
		}
		
		if (targetMplayer.getRelationTo(msenderFaction) == Rel.LEADER)
		{
			msg("<b>Pperms are not editable for <g>your faction leader<b>.");
			return;
		}
		
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
		
		ChestGui chestGui = ChestGui.getCreative(Bukkit.createInventory(null, getInventorySize(FactionPermissions.get().factionPermissions.size() - 4), ChatColor.translateAlternateColorCodes('&', FactionPermissions.get().ppermissionGuiName.replace("%player%", targetMplayer.getName()))));
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
			im.setLore(MUtil.list(ChatColor.GRAY + "Click to Modify", " ", ChatColor.GRAY + "Player permissions always override", ChatColor.GRAY + "default faction permissions"));
			
			item.setItemMeta(im);
			
			chestGui.getInventory().setItem(slot, item);
			chestGui.setAction(slot, new ActionOpenPperm(factionPermission.getmPermType(), targetMplayer));
			
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
	
}