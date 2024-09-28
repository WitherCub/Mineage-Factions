package gg.halcyon.upgrades;

import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.util.Glow;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.upgrades.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.massivecraft.massivecore.mson.Mson.mson;

public class UpgradesManager
{
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static UpgradesManager i = new UpgradesManager();
	private ArrayList<Upgrade> upgrades = new ArrayList<>();
	
	public static UpgradesManager get()
	{
		return i;
	}

	public Inventory getFactionUpgrades(Faction faction)
	{
		Inventory inventory = Bukkit.createInventory(null, MissionUpgradeConf.get().factionUpgradesInventorySize, Txt.parse(MissionUpgradeConf.get().factionUpgradesInventoryName));

		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);

		if (MissionUpgradeConf.get().factionUpgradesBorderGlassEnabled)
		{
			if (MissionUpgradeConf.get().factionUpgradesBorderGlassGlowEnabled)
			{
				IntStream.range(0, chestGui.getInventory().getSize()).forEach(i -> chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(MissionUpgradeConf.get().factionUpgradesBorderGlassDurabilityId).enchantment(Glow.getGlow())));
			}
			else
			{
				IntStream.range(0, chestGui.getInventory().getSize()).forEach(i -> chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(MissionUpgradeConf.get().factionUpgradesBorderGlassDurabilityId)));
			}
		}

		NumberFormat priceFormat = NumberFormat.getInstance();
		priceFormat.setGroupingUsed(true);

		for (Upgrade upgrade : upgrades)
		{
			List<String> lore = new ArrayList<>();

			int upgradeLevel = faction.getLevel(upgrade.getUpgradeName());
			int upgradePrice;

			if (upgrade.getCost().length <= upgradeLevel)
			{
				upgradePrice = 0;
				lore.add(Txt.parse(""));
				lore.add(Txt.parse("<n>Current Level: <g><bold>MAX LEVEL"));
				lore.add(Txt.parse(""));
				lore.add(Txt.parse("<i>Current Benefits:"));

				if (upgradeLevel != 0)
				{
					for (String line : upgrade.getCurrentUpgradeDescription()[upgradeLevel - 1].split("/n"))
					{
						lore.add(Txt.parse("<n>%s", line));
					}
				}
			}
			else
			{
				upgradePrice = upgrade.getCost()[upgradeLevel];

				if (upgradeLevel == 0)
				{
					lore.add(Txt.parse(""));
					lore.add(Txt.parse("<n>Current Level: <b><bold>LOCKED"));
					lore.add(Txt.parse("<n>Maximum Level: <a>%s", upgrade.getMaxLevel()));
					lore.add(Txt.parse(""));
					lore.add(Txt.parse("<i>Next Upgrade:"));

					for (String line : upgrade.getNextUpgradeDescription()[upgradeLevel].split("/n"))
					{
						lore.add(Txt.parse("<n>%s", line));
					}

					lore.add(Txt.parse(""));
				}
				else
				{
					lore.add(Txt.parse(""));
					lore.add(Txt.parse("<n>Current Level: <a>%s", upgradeLevel));
					lore.add(Txt.parse("<n>Maximum Level: <a>%s", upgrade.getMaxLevel()));
					lore.add(Txt.parse(""));
					if (upgrade.getCurrentUpgradeDescription()[upgradeLevel - 1] != null)
					{
						lore.add(Txt.parse("<i>Current Benefits:"));

						for (String line : upgrade.getCurrentUpgradeDescription()[upgradeLevel - 1].split("/n"))
						{
							lore.add(Txt.parse("<n>%s", line));
						}

						lore.add(Txt.parse(""));
					}

					if (upgrade.getNextUpgradeDescription()[upgradeLevel] != null)
					{
						lore.add(Txt.parse("<i>Next Upgrade:"));

						for (String line : upgrade.getNextUpgradeDescription()[upgradeLevel].split("/n"))
						{
							lore.add(Txt.parse("<n>%s", line));
						}

						lore.add(Txt.parse(""));
					}
				}

				if (!(upgradeLevel >= upgrade.getMaxLevel()))
				{
					lore.add(Txt.parse("<n>Cost: <i>$%s", priceFormat.format(upgradePrice)));
				}
			}

			chestGui.getInventory().setItem(upgrade.getInventorySlot(), new ItemBuilder(upgrade.getUpgradeItem()).amount(1).name(upgrade.getUpgradeName()).setLore(lore));
			chestGui.setAction(upgrade.getInventorySlot(), new ActionUpgrade(faction, upgradePrice, upgrade.getUpgradeName(), upgradeLevel));
		}

		return chestGui.getInventory();
	}
	
	public ArrayList<Upgrade> getUpgrades()
	{
		return upgrades;
	}
	
	public void load()
	{
		upgrades.add(new SpawnerRateUpgrade());
		upgrades.add(new CropGrowthUpgrade());
		upgrades.add(new FactionChestUpgrade());
		upgrades.add(new TNTStorageUpgrade());
		upgrades.add(new WarpUpgrade());
		//upgrades.add(new OutpostUpgrade());
		upgrades.add(new SandbotUpgrade());
	}
	
	public Upgrade getUpgradeByName(String string)
	{
		for (Upgrade upgrade : upgrades)
		{
			if (upgrade.getUpgradeName().equalsIgnoreCase(string))
			{
				return upgrade;
			}
		}
		return null;
	}
	
	public void increaseUpgrade(Faction faction, Upgrade upgrade)
	{
		if (faction.isSystemFaction()) return;
		if (faction.getLevel(upgrade.getUpgradeName()) >= upgrade.getMaxLevel()) return;
		
		faction.increaseLevel(upgrade.getUpgradeName());
		upgrade.onUpgrade(faction);
	}
	
	// -------------------------------------------- //
	// CHEST ACTION
	// -------------------------------------------- //

	private class ActionUpgrade extends ChestActionAbstract
	{
		Faction faction;
		Integer price;
		String upgradeName;
		Integer level;

		public ActionUpgrade(Faction faction, Integer price, String upgradeName, Integer level)
		{
			this.faction = faction;
			this.price = price;
			this.upgradeName = upgradeName;
			this.level = level;
		}

		@Override
		public boolean onClick(InventoryClickEvent event)
		{
			Player player = (Player) event.getWhoClicked();
			MPlayer mPlayer = MPlayer.get(player);

			if (price == 0)
			{
				mPlayer.msg("<b>You already have the maximum level in this upgrade.");
				return true;
			}

			if (!MPerm.getPermUpgrades().has(mPlayer, faction, true)) return true;

			if (!Money.despawn(faction, null, price))
			{
				mPlayer.msg("<b>You do not have enough money in the faction bank to complete this purchase.");
				return true;
			}

			Mson mson = mson(Txt.parse("%s<i> has upgraded <a>%s <i>to level %s<i>.", mPlayer.describeTo(faction, true), ChatColor.stripColor(upgradeName), level + 1));

			increaseUpgrade(faction, getUpgradeByName(upgradeName));
			faction.sendMessage(mson);
			return true;
		}
	}
	
}
