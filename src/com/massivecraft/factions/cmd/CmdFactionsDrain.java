package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.engine.actions.ActionCloseInventory;
import com.massivecraft.factions.engine.actions.ActionDrain;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.text.NumberFormat;

public class CmdFactionsDrain extends FactionsCommand
{
	
	public CmdFactionsDrain()
	{
		this.addRequirements(RequirementIsPlayer.get());
		this.addRequirements(ReqHasFaction.get());
		this.addParameter(TypeInteger.get(), "amount", "");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You must be in a faction to use this command!"));
			return;
		}
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		Integer amount = this.readArg(-1);
		
		if (amount == -1)
		{
			if (msender.isDrain())
			{
				msender.msg(LangConf.get().currentDrainStatusAllowedMsg);
			}
			else
			{
				msender.msg(LangConf.get().currentDrainStatusDisallowedMsg);
			}
			
			if (msenderFaction.getLastDrainMillis() == 0)
			{
				msender.msg(LangConf.get().lastDrainTime.replace("%factionName%", msenderFaction.getName()).replace("%timeAgo%", "Never"));
			}
			else
			{
				msender.msg(LangConf.get().lastDrainTime.replace("%factionName%", msenderFaction.getName()).replace("%timeAgo%", TimeUtil.formatTime(System.currentTimeMillis() - msenderFaction.getLastDrainMillis()) + "ago"));
			}
			
			msender.msg(LangConf.get().lastDrainAmount.replace("%amount%", numberFormat.format(msender.getLastDrainAmountTaken())));
			msender.msg(LangConf.get().initiateDrainMsg);
		}
		else if (amount < MConf.get().minimumDrainAmount)
		{
			msender.msg(LangConf.get().drainAmountLowerThanMinimumMsg.replace("%amount%", numberFormat.format(MConf.get().minimumDrainAmount)));
		}
		else if (MPerm.getPermDrain().has(this.msender, this.msenderFaction, true))
		{
			me.openInventory(getConfirmInventory(numberFormat.format(amount), amount, numberFormat).getInventory());
		}
	}
	
	private ChestGui getConfirmInventory(String amountToDrainStr, Integer drainAmount, NumberFormat numberFormat)
	{
		Inventory inventory = Bukkit.createInventory(null, 27, Txt.parse("<g>Confirmation"));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		chestGui.getInventory().setItem(4, new ItemBuilder(Material.HOPPER).name(Txt.parse("&cDrain all members %amount%?".replace("%amount%", amountToDrainStr)).replace("%faction%", msenderFaction.getName())).lore(Txt.parse("&7Upon confirmation all member")).lore(Txt.parse("&7balances will be drained!")));
		
		chestGui.getInventory().setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED + "Deny").durability(14));
		chestGui.setAction(11, new ActionCloseInventory());
		chestGui.getInventory().setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.GREEN + "Confirm").durability(5));
		chestGui.setAction(15, new ActionDrain(msender, msenderFaction, drainAmount, numberFormat));
		
		return chestGui;
	}
	
}