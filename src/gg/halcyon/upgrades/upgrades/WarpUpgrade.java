package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.Upgrade;
import org.bukkit.Material;

public class WarpUpgrade extends Upgrade
{
	@Override
	public int getMaxLevel()
	{
		return MissionUpgradeConf.get().warpUpgrade.getMaxLevel();
	}
	
	@Override
	public String getUpgradeName()
	{
		return MissionUpgradeConf.get().warpUpgrade.getUpgradeName();
	}
	
	@Override
	public String[] getCurrentUpgradeDescription()
	{
		return MissionUpgradeConf.get().warpUpgrade.getCurrentUpgradeDescription();
	}
	
	@Override
	public String[] getNextUpgradeDescription()
	{
		return MissionUpgradeConf.get().warpUpgrade.getNextUpgradeDescription();
	}
	
	@Override
	public Material getUpgradeItem()
	{
		return MissionUpgradeConf.get().warpUpgrade.getUpgradeItem();
	}
	
	@Override
	public Integer[] getCost()
	{
		return MissionUpgradeConf.get().warpUpgrade.getCost();
	}
	
	@Override
	public void onUpgrade(Faction faction)
	{
	
	}

	@Override
	public int getInventorySlot() {
		return MissionUpgradeConf.get().warpUpgrade.getInventorySlot();
	}
}