package gg.halcyon.upgrades;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public abstract class Upgrade implements Listener
{
	
	public Upgrade()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
	}
	
	public abstract int getMaxLevel();
	
	public abstract String getUpgradeName();
	
	public abstract String[] getCurrentUpgradeDescription();
	
	public abstract String[] getNextUpgradeDescription();
	
	public abstract Material getUpgradeItem();
	
	public abstract Integer[] getCost();
	
	public abstract void onUpgrade(Faction faction);

	public abstract int getInventorySlot();
}
