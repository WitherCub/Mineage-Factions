package gg.halcyon.upgrades;

import com.massivecraft.massivecore.store.EntityInternal;
import org.bukkit.Material;

public class UpgradeInfo extends EntityInternal<UpgradeInfo>
{
	
	public String getUpgradeName()
	{
		return upgradeName;
	}
	
	public String[] getCurrentUpgradeDescription()
	{
		return currentUpgradeDescription;
	}
	
	public String[] getNextUpgradeDescription()
	{
		return nextUpgradeDescription;
	}
	
	public Material getUpgradeItem()
	{
		return upgradeItem;
	}
	
	public Integer[] getCost()
	{
		return cost;
	}
	
	private int maxLevel;
	private String upgradeName;
	private String[] currentUpgradeDescription;
	private String[] nextUpgradeDescription;
	private Material upgradeItem;
	private Integer[] cost;
	private Double[] upgradeValue;
	private int inventorySlot;
	
	public UpgradeInfo(String upgradeName, Material upgradeItem, int maxLevel, String[] currentUpgradeDescription, String[] nextUpgradeDescription, Integer[] cost, Double[] upgradeValue, int inventorySlot)
	{
		this.maxLevel = maxLevel;
		this.upgradeName = upgradeName;
		this.currentUpgradeDescription = currentUpgradeDescription;
		this.nextUpgradeDescription = nextUpgradeDescription;
		this.upgradeItem = upgradeItem;
		this.cost = cost;
		this.upgradeValue = upgradeValue;
		this.inventorySlot = inventorySlot;
	}
	
	public int getMaxLevel()
	{
		return maxLevel;
	}

	public int getInventorySlot() {
		return inventorySlot;
	}

	public Double[] getUpgradeValue() {
		return upgradeValue;
	}
}