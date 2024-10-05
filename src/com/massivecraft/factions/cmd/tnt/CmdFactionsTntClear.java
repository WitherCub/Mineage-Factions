package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.ps.PS;
import gg.halcyon.upgrades.UpgradesManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

public class CmdFactionsTntClear extends FactionsCommand
{
	public CmdFactionsTntClear()
	{
		this.addParameter(TypeInteger.get(), "radius");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (!(msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<b>In order to use your virtual tnt storage, you must unlock it using /f upgrade.");
			return;
		}
		
		Integer radius = this.readArgAt(0, MConf.get().maximumFillRadius);
		
		if (radius > MConf.get().maximumFillRadius)
		{
			msg(LangConf.get().reachedMaximumFillRadius);
			return;
		}
		
		if (MPerm.getPermTntDeposit().has(msender, msenderFaction, true))
		{
			Block b = me.getWorld().getBlockAt(me.getLocation());
			Set<Dispenser> dispensers = new HashSet<>();
			Block br;
			
			for (int x = -radius; x <= radius; x++)
			{
				for (int y = -radius; y <= radius; y++)
				{
					for (int z = -radius; z <= radius; z++)
					{
						br = b.getRelative(x, y, z);
						if (!br.getType().equals(Material.DISPENSER))
						{
							continue;
						}
						dispensers.add((Dispenser) br.getState());
					}
				}
			}
			
			if (dispensers.isEmpty())
			{
				msg("<b>There are no dispensers nearby.");
				return;
			}
			
			int maxTnt = Integer.parseInt(UpgradesManager.get().getUpgradeByName(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()).getCurrentUpgradeDescription()[msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) - 1].split(" ")[2].replaceAll(",", ""));
			int tntToMove = 0;
			int dispensersChanged = 0;
			
			for (Dispenser d : dispensers)
			{
				if (tntToMove == maxTnt) break;
				Faction faction = BoardColl.get().getFactionAt(PS.valueOf(d.getLocation()));
				if (msenderFaction == faction || faction.isNone())
				{
					ItemStack[] contents = d.getInventory().getContents();
					int has = 0;
					for (ItemStack item : contents)
					{
						if ((item != null) && (item.getType().equals(Material.TNT) && (item.getAmount() > 0)))
						{
							has += item.getAmount();
						}
					}
					
					if (tntToMove + has + msenderFaction.getTnt() > maxTnt)
					{
						has = has - ((tntToMove + has + msenderFaction.getTnt()) - maxTnt);
					}
					
					d.getInventory().removeItem(new ItemStack(Material.TNT, has));
					if (has > 0) dispensersChanged++;
					tntToMove += has;
				}
			}
			
			msenderFaction.addTnt(tntToMove);
			
			if (tntToMove > 0)
			{
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setGroupingUsed(true);
				msender.msg("%s<i> cleared <a>%s <i>dispensers. %s <i>now has <a>%s <i>tnt.", msender.describeTo(msender, true), dispensersChanged, msenderFaction.describeTo(msenderFaction, true), numberFormat.format(msenderFaction.getTnt()));
			}
			else
			{
				msender.msg("<b>No dispensers nearby containing tnt.");
			}
		}
	}
}
