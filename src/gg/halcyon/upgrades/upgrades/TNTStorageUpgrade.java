package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.Upgrade;
import org.bukkit.Material;

public class TNTStorageUpgrade extends Upgrade
{
	@Override
	public int getMaxLevel()
	{
		return MissionUpgradeConf.get().tntUpgrade.getMaxLevel();
	}
	
	@Override
	public String getUpgradeName()
	{
		return MissionUpgradeConf.get().tntUpgrade.getUpgradeName();
	}
	
	@Override
	public String[] getCurrentUpgradeDescription()
	{
		return MissionUpgradeConf.get().tntUpgrade.getCurrentUpgradeDescription();
	}
	
	@Override
	public String[] getNextUpgradeDescription()
	{
		return MissionUpgradeConf.get().tntUpgrade.getNextUpgradeDescription();
	}
	
	@Override
	public Material getUpgradeItem()
	{
		return MissionUpgradeConf.get().tntUpgrade.getUpgradeItem();
	}
	
	@Override
	public Integer[] getCost()
	{
		return MissionUpgradeConf.get().tntUpgrade.getCost();
	}
	
	@Override
	public void onUpgrade(Faction faction)
	{
	
	}

	@Override
	public int getInventorySlot() {
		return MissionUpgradeConf.get().tntUpgrade.getInventorySlot();
	}
}