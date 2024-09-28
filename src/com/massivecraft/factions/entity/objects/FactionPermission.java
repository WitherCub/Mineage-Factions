package com.massivecraft.factions.entity.objects;

import com.massivecraft.factions.entity.MPerm;
import org.bukkit.Material;
public class FactionPermission
{
	
	private String mPermType;
	private Material guiDisplayItem;
	
	public FactionPermission(String mPermType, Material guiDisplayItem)
	{
		this.guiDisplayItem = guiDisplayItem;
		this.mPermType = mPermType;
	}
	
	public MPerm getMPerm()
	{
		return MPerm.get(mPermType);
	}
	
	public String getmPermType()
	{
		return mPermType;
	}
	
	public Material getGuiDisplayItem()
	{
		return guiDisplayItem;
	}
}