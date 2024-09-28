package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.util.TNTUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class CmdFactionsTntFill extends FactionsCommand
{
	public CmdFactionsTntFill()
	{
		this.addParameter(TypeInteger.get(), "amount");
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
		
		Integer tntPer = this.readArg();
		
		if (tntPer < 1)
		{
			msg(MConf.get().cantFillLessThanOne);
			return;
		}
		
		Integer radius = this.readArgAt(1, MConf.get().maximumFillRadius);
		
		if (radius > MConf.get().maximumFillRadius)
		{
			msg(MConf.get().reachedMaximumFillRadius);
			return;
		}
		
		if (MPerm.getPermTntWithdraw().has(msender, msenderFaction, true))
		{
			int tntBal = msenderFaction.getTnt();
			
			if (tntPer > 576)
			{
				msender.msg("<b>Dispensers can only hold a maximum of 576 tnt.");
				return;
			}
			
			if (tntBal < 0)
			{
				msender.msg("<b>Your faction does not have any tnt to fill.");
				return;
			}
			
			// calculate dispensers below
			
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
			
			// start filling below
			
			int tntUsed = 0;
			int dispensersFilled = 0;
			
			for (Dispenser d : dispensers)
			{
				int tntInDispenser = 0;
				int otherInDispenser = 0;
				if (d.getInventory().contains(Material.TNT))
				{
					tntInDispenser = TNTUtil.getAmountOfTNT(d.getInventory());
					
					if (tntInDispenser >= tntPer)
					{
						continue;
					}
					
					otherInDispenser = TNTUtil.getAmountOfNotTNT(d.getInventory());
				}
				
				int inDispenser = tntInDispenser + otherInDispenser;
				
				int tntToAdd = tntPer - tntInDispenser;
				
				if (inDispenser < 576)
				{
					int spaceLeft = 576 - inDispenser;
					
					if (spaceLeft > tntToAdd)
					{
						if (tntUsed + tntToAdd > tntBal)
							break;
						
						d.getInventory().addItem(new ItemStack(Material.TNT, tntToAdd));
						tntUsed = tntUsed + tntToAdd;
						dispensersFilled++;
					}
					else
					{
						if (tntUsed + tntToAdd > tntBal)
							break;
						
						d.getInventory().addItem(new ItemStack(Material.TNT, spaceLeft));
						tntUsed = tntUsed + spaceLeft;
						dispensersFilled++;
					}
				}
			}
			
			msenderFaction.takeTnt(tntUsed);
			msender.msg("%s<i> filled <a>%s <i>dispensers with <a>%s <i>tnt each, costing <a>%s <i>tnt.", msender.describeTo(msender, true), dispensersFilled, tntPer, tntUsed);
		}
	}
	
}
