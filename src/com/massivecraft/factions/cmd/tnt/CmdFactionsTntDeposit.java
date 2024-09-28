package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.util.TNTUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import gg.halcyon.upgrades.UpgradesManager;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;

public class CmdFactionsTntDeposit extends FactionsCommand
{
	public CmdFactionsTntDeposit()
	{
		this.addParameter(TypeInteger.get(), "amount", "2304");
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
		
		if (MPerm.getPermTntDeposit().has(msender, msenderFaction, true))
		{
			Inventory inventory = me.getInventory();
			
			int tntInInventory = 0;
			
			if (inventory.contains(Material.TNT))
			{
				tntInInventory = TNTUtil.getAmountOfTNT(inventory);
			}
			
			Integer amount = this.readArg(tntInInventory);

			if (amount < 1) return;
			
			int maxTnt = Integer.parseInt(UpgradesManager.get().getUpgradeByName(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()).getCurrentUpgradeDescription()[msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) - 1].split(" ")[2].replaceAll(",", ""));
			
			if (tntInInventory > 0)
			{
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setGroupingUsed(true);
				
				if (msenderFaction.getTnt() + amount > maxTnt) {
					amount = amount - ((amount + msenderFaction.getTnt()) - maxTnt);
				}
				
				if (tntInInventory >= amount)
				{
					msender.msg("%s<i> deposited <a>%s <i>tnt to %s<i>.", msender.describeTo(msender, true), numberFormat.format(amount), msenderFaction.describeTo(msender));
					inventory.removeItem(new ItemStack(Material.TNT, amount));
					msenderFaction.addTnt(amount);
					me.updateInventory();
				}
				else
				{
					msender.msg("<b>You do not have %s tnt in your inventory to deposit.", numberFormat.format(amount));
				}
			}
			else
			{
				msender.msg("<b>You do not have any tnt in your inventory to deposit.");
			}
		}
	}
	
}
