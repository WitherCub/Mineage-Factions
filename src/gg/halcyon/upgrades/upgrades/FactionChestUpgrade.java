package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.entity.objects.ChestTransaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.util.InventoryUtil;
import gg.halcyon.upgrades.Upgrade;
import gg.halcyon.upgrades.UpgradesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FactionChestUpgrade extends Upgrade
{
	
	private final Map<HumanEntity, ItemStack[]> containers = new HashMap<HumanEntity, ItemStack[]>();
	
	public static short rawData(ItemStack item)
	{
		return item.getType() != null ? item.getData() != null ? item.getDurability() : 0 : 0;
	}
	
	public ItemStack[] compareInventories(ItemStack[] items1, ItemStack[] items2)
	{
		final ItemStackComparator comperator = new ItemStackComparator();
		final ArrayList<ItemStack> diff = new ArrayList<ItemStack>();
		final int l1 = items1.length, l2 = items2.length;
		int c1 = 0, c2 = 0;
		while (c1 < l1 || c2 < l2)
		{
			if (c1 >= l1)
			{
				diff.add(items2[c2]);
				c2++;
				continue;
			}
			if (c2 >= l2)
			{
				items1[c1].setAmount(items1[c1].getAmount() * -1);
				diff.add(items1[c1]);
				c1++;
				continue;
			}
			final int comp = comperator.compare(items1[c1], items2[c2]);
			if (comp < 0)
			{
				items1[c1].setAmount(items1[c1].getAmount() * -1);
				diff.add(items1[c1]);
				c1++;
			}
			else if (comp > 0)
			{
				diff.add(items2[c2]);
				c2++;
			}
			else
			{
				final int amount = items2[c2].getAmount() - items1[c1].getAmount();
				if (amount != 0)
				{
					items1[c1].setAmount(amount);
					diff.add(items1[c1]);
				}
				c1++;
				c2++;
			}
		}
		return diff.toArray(new ItemStack[diff.size()]);
	}
	
	public ItemStack[] compressInventory(ItemStack[] items)
	{
		final ArrayList<ItemStack> compressed = new ArrayList<ItemStack>();
		for (final ItemStack item : items)
		{
			if (item != null)
			{
				final int type = item.getTypeId();
				final short data = rawData(item);
				boolean found = false;
				for (final ItemStack item2 : compressed)
				{
					if (type == item2.getTypeId() && data == rawData(item2))
					{
						item2.setAmount(item2.getAmount() + item.getAmount());
						found = true;
						break;
					}
				}
				if (!found)
				{
					compressed.add(new ItemStack(type, item.getAmount(), data));
				}
			}
		}
		Collections.sort(compressed, new ItemStackComparator());
		return compressed.toArray(new ItemStack[compressed.size()]);
	}
	
	@Override
	public int getMaxLevel()
	{
		return MissionUpgradeConf.get().factionChestUpgrade.getMaxLevel();
	}
	
	@Override
	public String getUpgradeName()
	{
		return MissionUpgradeConf.get().factionChestUpgrade.getUpgradeName();
	}
	
	@Override
	public String[] getCurrentUpgradeDescription()
	{
		return MissionUpgradeConf.get().factionChestUpgrade.getCurrentUpgradeDescription();
	}
	
	@Override
	public String[] getNextUpgradeDescription()
	{
		return MissionUpgradeConf.get().factionChestUpgrade.getNextUpgradeDescription();
	}
	
	@Override
	public Material getUpgradeItem()
	{
		return MissionUpgradeConf.get().factionChestUpgrade.getUpgradeItem();
	}
	
	@Override
	public Integer[] getCost()
	{
		return MissionUpgradeConf.get().factionChestUpgrade.getCost();
	}
	
	@Override
	public void onUpgrade(Faction faction)
	{
		if (faction.getInventory() != null)
		{
			int size = Integer.parseInt(UpgradesManager.get().getUpgradeByName(MissionUpgradeConf.get().factionChestUpgrade.getUpgradeName()).getCurrentUpgradeDescription()[faction.getLevel(MissionUpgradeConf.get().factionChestUpgrade.getUpgradeName()) - 1].split(" ")[2]);
			
			for (HumanEntity he : faction.getInventory().getViewers())
			{
				he.closeInventory();
			}
			
			Inventory old = faction.getInventory();
			faction.setInventory(Bukkit.createInventory(null, size, ChatColor.GREEN + faction.getName() + " - Faction Chest"));
			faction.getInventory().setContents(old.getContents());
			old.clear();
		}
	}

	@Override
	public int getInventorySlot() {
		return MissionUpgradeConf.get().factionChestUpgrade.getInventorySlot();
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if (!event.getInventory().getName().endsWith(" - Faction Chest"))
		{
			return;
		}
		
		Faction faction = InventoryUtil.getFactionFromInventory(event.getInventory());
		if (faction == null)
		{
			return;
		}
		
		containers.put(event.getPlayer(), compressInventory(event.getInventory().getContents()));
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (!event.getInventory().getName().endsWith(" - Faction Chest"))
		{
			return;
		}
		
		Faction faction = InventoryUtil.getFactionFromInventory(event.getInventory());
		if (faction == null)
		{
			return;
		}
		
		faction.saveInventory();
		
		final HumanEntity player = event.getPlayer();
		final ItemStack[] before = containers.get(player);
		if (before != null)
		{
			final ItemStack[] after = compressInventory(event.getInventory().getContents());
			final ItemStack[] diff = compareInventories(before, after);
			for (final ItemStack item : diff)
			{
				faction.addChestTransaction(new ChestTransaction(player.getUniqueId().toString(), item));
			}
			containers.remove(player);
		}
	}
	
	@EventHandler
	public void onFactionNameChange(EventFactionsNameChange event)
	{
		if (event.getFaction().getLevel(this.getUpgradeName()) > 0)
		{
			if (event.getFaction().getInventory() != null)
			{
				for (HumanEntity he : event.getFaction().getInventory().getViewers())
				{
					he.closeInventory();
				}
				
				Inventory old = event.getFaction().getInventory();
				event.getFaction().setInventory(Bukkit.createInventory(null, old.getSize(), ChatColor.GREEN + event.getNewName() + " - Faction Chest"));
				event.getFaction().getInventory().setContents(old.getContents());
				old.clear();
			}
		}
	}
	
	public class ItemStackComparator implements Comparator<ItemStack>
	{
		@Override
		public int compare(ItemStack a, ItemStack b)
		{
			final int aType = a.getTypeId(), bType = b.getTypeId();
			if (aType < bType)
			{
				return -1;
			}
			if (aType > bType)
			{
				return 1;
			}
			final short aData = rawData(a), bData = rawData(b);
			if (aData < bData)
			{
				return -1;
			}
			if (aData > bData)
			{
				return 1;
			}
			return 0;
		}
	}
}