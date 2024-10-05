package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.factions.engine.actions.ActionChangeNotificationTime;
import com.massivecraft.factions.engine.actions.ActionOpenRelations;
import com.massivecraft.factions.engine.actions.ActionToggleAlarm;
import com.massivecraft.factions.entity.GuiConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsCheck extends FactionsCommand
{
	private static CmdFactionsCheck i = new CmdFactionsCheck();
	public static CmdFactionsCheck get() { return i; }
	
	public CmdFactionsCheck()
	{
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MixinMessage.get().msgOne(me, "&6_____.[ &2Help for command \"check\" &7[<] &61/1 &7[>] &6]________");
		MixinMessage.get().msgOne(me, "&b/f check &echange check settings");
		MixinMessage.get().msgOne(me, "&b/f clear &emark walls as clear");
		MixinMessage.get().msgOne(me, "&b/f alarm &etoggle the alarm");
		
		me.openInventory(getCheckSettings(msender));
	}
	
	public Inventory getCheckSettings(MPlayer mPlayer)
	{
		Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Txt.parse("<b>Check Settings"));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		List<String> lore = new ArrayList<>();
		
		for (String string : GuiConf.get().notificationTimeLore)
		{
			if (string.contains("%currentNotificationTime%"))
			{
				if (mPlayer.getFaction().getNotificationTimeMinutes() == 0)
				{
					lore.add(Txt.parse(string).replace("%currentNotificationTime%", ChatColor.RED + "Disabled"));
				}
				else
				{
					lore.add(Txt.parse(string).replace("%currentNotificationTime%", ChatColor.GOLD + String.valueOf(mPlayer.getFaction().getNotificationTimeMinutes()) + " minutes"));
				}
			}
			else
			{
				lore.add(Txt.parse(string));
			}
		}
		
		chestGui.getInventory().setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(Txt.parse("")));
		chestGui.getInventory().setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(Txt.parse("")));
		
		chestGui.getInventory().setItem(0, new ItemBuilder(GuiConf.get().notificationTimeMaterial).name(Txt.parse(GuiConf.get().notificationTimeName)).setLore(lore));
		chestGui.setAction(0, new ActionChangeNotificationTime(mPlayer));
		
		chestGui.getInventory().setItem(2, new ItemBuilder(GuiConf.get().permsFclearMaterial).name(Txt.parse(GuiConf.get().permsFclearName)));
		chestGui.setAction(2, new ActionOpenRelations(MPerm.get(MPerm.ID_CHECK).getName().toLowerCase(), mPlayer));
		
		lore.clear();
		
		for (String string : GuiConf.get().alarmSoundsLore)
		{
			if (string.contains("%currentAlarmSounds%"))
			{
				if (mPlayer.isAlertSoundEnabled())
				{
					lore.add(Txt.parse(string).replace("%currentAlarmSounds%", ChatColor.GREEN + "Enabled"));
				}
				else
				{
					lore.add(Txt.parse(string).replace("%currentAlarmSounds%", ChatColor.RED + "Disabled"));
				}
			}
			else
			{
				lore.add(Txt.parse(string));
			}
		}
		
		if (mPlayer.isAlertSoundEnabled())
		{
			chestGui.getInventory().setItem(4, new ItemBuilder(GuiConf.get().alarmSoundsOnMaterial).durability(GuiConf.get().alarmSoundsOnData).name(Txt.parse(GuiConf.get().alarmSoundsName)).setLore(lore));
		}
		else
		{
			chestGui.getInventory().setItem(4, new ItemBuilder(GuiConf.get().alarmSoundsOffMaterial).durability(GuiConf.get().alarmSoundsOffData).name(Txt.parse(GuiConf.get().alarmSoundsName)).setLore(lore));
		}
		
		chestGui.setAction(4, new ActionToggleAlarm(mPlayer));
		
		return chestGui.getInventory();
	}
	
}