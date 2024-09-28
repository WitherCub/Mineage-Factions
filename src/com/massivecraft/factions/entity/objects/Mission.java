package com.massivecraft.factions.entity.objects;

import com.massivecraft.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public abstract class Mission implements Listener
{
	
	public Mission()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
	}
	
	public abstract String getMissionName();
	
	public abstract String getMissionDisplayname();
	
	public abstract String getDescription();
	
	public abstract Double getRequirement();
	
	public abstract Integer getCreditsReward();
	
	public abstract Material getMissionGuiIconMaterial();
	
	public abstract Integer getMissionGuiIconDurability();
	
}