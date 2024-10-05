package com.massivecraft.factions.entity;

import com.massivecraft.factions.coll.MPermColl;
import com.massivecraft.factions.entity.objects.FactionPermission;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Material;

import java.util.List;
@EditorName("config")
public class FactionPermissions extends Entity<FactionPermissions>
{
	
	private static FactionPermissions i;
	public static void set(FactionPermissions newI) { i = newI; }

	public String ppermissionGuiName = "&c&lPerms (%player%)";
	public String permissionGuiName = "&c&lManage Faction Permissions";
	public Material outLineGuiMaterial = Material.STAINED_GLASS_PANE;
	public int outLineGuiData = 14;
	public List<FactionPermission> factionPermissions = MUtil.list();
	
	public static FactionPermissions get()
	{
		return i;
	}
	
	private void initList()
	{
		if (!factionPermissions.isEmpty()) return;
		MPermColl.get().getAll().forEach(mPerm -> factionPermissions.add(new FactionPermission(mPerm.getName(), Material.ENCHANTED_BOOK)));
	}
	
	@Override
	public FactionPermissions load(FactionPermissions that)
	{
		super.load(that);
		
		initList();
		
		return this;
	}
	
}