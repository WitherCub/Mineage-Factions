package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.factions.engine.actions.ActionFillTnt;
import com.massivecraft.factions.engine.actions.ActionStickBuy;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.UpgradesManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.text.NumberFormat;

public class CmdFactionsTnt extends FactionsCommand
{
	private static CmdFactionsTnt i = new CmdFactionsTnt();
	public static CmdFactionsTnt get() { return i; }
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsTntDeposit cmdFactionsTntDeposit = new CmdFactionsTntDeposit();
	public CmdFactionsTntWithdraw cmdFactionsTntWithdraw = new CmdFactionsTntWithdraw();
	public CmdFactionsTntFill cmdFactionsTntFill = new CmdFactionsTntFill();
	public CmdFactionsTntClear cmdFactionsTntClear = new CmdFactionsTntClear();
	public CmdFactionsTntSet cmdFactionsTntSet = new CmdFactionsTntSet();
	public CmdFactionsTntBalance cmdFactionsTntBalance = new CmdFactionsTntBalance();
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		this.getHelpCommand().execute(this.sender, this.getArgs());
		
		if (!(msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<n>In order to use your virtual tnt storage, you must unlock it using /f upgrade.");
		}
		else
		{
			me.openInventory(getFactionTntGui().getInventory());
			
			msender.msg("<n>Maximum TNT your Faction can store: <a>%s", UpgradesManager.get().getUpgradeByName(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()).getCurrentUpgradeDescription()[msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) - 1].split(" ")[2]);
			msender.msg("<n>Current TNT in storage: <a>%s", numberFormat.format(msenderFaction.getTnt()));
		}
	}
	
	
	private ChestGui getFactionTntGui()
	{
		Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Txt.parse("&c&lFaction TNT"));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(false);
		chestGui.setAutoremoving(false);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		chestGui.getInventory().setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(0).name(""));
		chestGui.getInventory().setItem(4, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(0).name(""));
		
		chestGui.getInventory().setItem(1, new ItemBuilder(Material.DISPENSER).durability(0).name(Txt.parse("&eFill nearby dispensers")).lore(Txt.parse("&7Click to fill dispensers within &e30")).lore(Txt.parse("&7blocks with 1 piece of TNT")));
		chestGui.setAction(1, new ActionFillTnt());
		
		chestGui.getInventory().setItem(2, new ItemBuilder(Material.TNT).durability(0).name(Txt.parse("&7Stored TNT: &e%amount%".replace("%amount%", String.valueOf(msenderFaction.getTnt())))).lore(Txt.parse("&aYour faction &7can store a maximum")).lore(Txt.parse("&7of &e%max% &7TNT".replace("%max%", String.valueOf(UpgradesManager.get().getUpgradeByName(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()).getCurrentUpgradeDescription()[msenderFaction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()) - 1].split(" ")[2])))).lore("").lore(Txt.parse("&7Purchase upgrades to store more")));
		
		chestGui.getInventory().setItem(3, new ItemBuilder(Material.STICK).durability(0).name(Txt.parse(MConf.get().tntStickName)).lore(Txt.parse("&7Click to claim a tnt fill tool for &e%points%".replace("%points%", String.valueOf(MConf.get().tntStickPoints)))).lore(Txt.parse("&7points.")).lore("").lore(Txt.parse("&7Right-click a container with this tool")).lore(Txt.parse("&7to put all your TNT into your TNT storage.")));
		chestGui.setAction(3, new ActionStickBuy());
		
		return chestGui;
	}
}
