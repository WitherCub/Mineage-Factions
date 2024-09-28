package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.util.TNTUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;

public class CmdFactionsTntWithdraw extends FactionsCommand
{
	public CmdFactionsTntWithdraw()
	{
		this.addParameter(TypeInteger.get(), "amount", "2304");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (!(msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<b>In order to use your virtual tnt storage, you must unlock it using /f upgrade.");
			return;
		}
		
		int tntInInventory = 0;
		int otherInInventory = 0;
		if (me.getInventory().contains(Material.TNT))
		{
			tntInInventory = TNTUtil.getAmountOfTNT(me.getInventory());
			otherInInventory = TNTUtil.getAmountOfNotTNT(me.getInventory());
		}
		
		int spaceLeft = 2304 - (tntInInventory + otherInInventory);
		
		Integer amount = this.readArg(spaceLeft);

		if (amount < 1) return;
		
		if (MPerm.getPermTntWithdraw().has(msender, msenderFaction, true))
		{
			int tntBal = msenderFaction.getTnt();
			
			if (tntBal <= 0)
			{
				msender.msg("<b>Your faction does not have any tnt to withdraw.");
				return;
			}
			
			if (amount > spaceLeft)
			{
				msender.msg("<b>You do not have enough space in your inventory to withdraw %s tnt.", amount);
				return;
			}
			
			if (amount > tntBal)
			{
				msender.msg("<b>Your faction does not have enough tnt to withdraw %s tnt.", amount);
				return;
			}
			
			if (spaceLeft >= amount)
			{
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setGroupingUsed(true);
				
				msender.msg("%s<i> withdrew <a>%s <i>tnt from %s<i>.", msender.describeTo(msender, true), numberFormat.format(amount), msenderFaction.describeTo(msender));
				msenderFaction.takeTnt(amount);
				me.getInventory().addItem(new ItemStack(Material.TNT, amount));
				me.updateInventory();
			}
		}
	}
	
}