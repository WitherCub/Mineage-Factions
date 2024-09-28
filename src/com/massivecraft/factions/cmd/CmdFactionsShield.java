package com.massivecraft.factions.cmd;

import com.massivecraft.factions.engine.actions.ActionDisableShield;
import com.massivecraft.factions.engine.actions.ActionShieldOpenConfirm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdFactionsShield extends FactionsCommand
{
	private static CmdFactionsShield i = new CmdFactionsShield();
	public static CmdFactionsShield get() { return i; }

	public MassiveSet<UUID> playersWithShieldGuiOpen = new MassiveSet<>();
	
	@Override
	public List<String> getAliases()
	{
		return MUtil.list("shield", "forcefield", "armistice");
	}
	
	@Override
	public void perform()
	{
		if (!MPerm.getPermShield().has(msender, msenderFaction, true)) return;
		
		me.openInventory(getShieldGui(msenderFaction).getInventory());
		playersWithShieldGuiOpen.add(msender.getUuid());
	}
	
	private ChestGui getShieldGui(Faction faction)
	{
		Inventory inventory = Bukkit.createInventory(null, 45, Txt.parse(MConf.get().shieldMangerGuiTitle));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(true);
		chestGui.setAutoremoving(true);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		Date date = new Date();
		date.setHours(TimeUtil.getTimeHours());
		date.setMinutes(TimeUtil.getTimeMinutes());
		date.setSeconds(TimeUtil.getTimeSeconds());
		
		int i = 0;
		
		for (int startHour : MConf.get().shieldStartEndHours.keySet())
		{
			int endHour = MConf.get().shieldStartEndHours.get(startHour);
			
			String currentTime = getTimeFormatted(date.getHours(), date.getMinutes());
			String startHourFormatted = getTimeFormatted(startHour, 0);
			String endHourFormatted = getTimeFormatted(endHour, 0);
			
			List<String> lore = new ArrayList<>();
			
			if (faction.getShieldedHoursStartTime() != null && faction.getShieldedHoursStartTime() == startHour)
			{
				MConf.get().shieldManagerGuiLoreCurrently.forEach(s -> lore.add(Txt.parse(s.replace("%startHour%", startHourFormatted).replace("%endHour%", endHourFormatted).replace("%currentTime%", currentTime))));
				chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(13).setLore(lore));
			}
			else if (faction.getShieldedHoursChangeRequestNewStartTime() != null && faction.getShieldedHoursChangeRequestNewStartTime() == startHour)
			{
				MConf.get().shieldManagerGuiLoreChangeTo.forEach(s -> lore.add(Txt.parse(s.replace("%startHour%", startHourFormatted).replace("%endHour%", endHourFormatted).replace("%currentTime%", currentTime))));
				chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(4).setLore(lore));
			}
			else
			{
				MConf.get().shieldManagerGuiLoreChange.forEach(s -> lore.add(Txt.parse(s.replace("%startHour%", startHourFormatted).replace("%endHour%", endHourFormatted).replace("%currentTime%", currentTime))));
				chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(14).setLore(lore));
				chestGui.setAction(i, new ActionShieldOpenConfirm("Set", faction, startHour, msender.describeTo(msenderFaction, true), msenderFaction.describeTo(msender), startHourFormatted, endHourFormatted));
			}
			
			i++;
		}
		
		chestGui.getInventory().setItem(24, new ItemBuilder(Material.BARRIER).name(" "));
		chestGui.getInventory().setItem(25, new ItemBuilder(Material.BARRIER).name(" "));
		chestGui.getInventory().setItem(26, new ItemBuilder(Material.BARRIER).name(" "));
		
		if (faction.getShieldedHoursCooldownFromDisable() != null)
		{
			String timeRemaining = TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursCooldownFromDisable()));
			chestGui.getInventory().setItem(39, new ItemBuilder(Material.WATCH).name(Txt.parse(MConf.get().pendingChangeGuiName)).setLore(MConf.get().pendingChangeGuiLoreChangeAfterDisable.stream().map(s -> Txt.parse(s.replace("%time%", timeRemaining))).collect(Collectors.toList())));
		}
		else if (faction.getShieldedHoursChangeRequestNewStartTime() != null)
		{
			String newStartHourFormatted = getTimeFormatted(faction.getShieldedHoursChangeRequestNewStartTime(), 0);
			String newEndHourFormatted = getTimeFormatted(MConf.get().shieldStartEndHours.get(faction.getShieldedHoursChangeRequestNewStartTime()), 0);
			String timeRemaining = TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursChangeRequestMillis()));
			chestGui.getInventory().setItem(39, new ItemBuilder(Material.WATCH).name(Txt.parse(MConf.get().pendingChangeGuiName)).setLore(MConf.get().pendingChangeGuiLoreChangeActive.stream().map(s -> Txt.parse(s.replace("%startHour%", newStartHourFormatted).replace("%endHour%", newEndHourFormatted).replace("%time%", timeRemaining))).collect(Collectors.toList())));
		}
		else
		{
			chestGui.getInventory().setItem(39, new ItemBuilder(Material.WATCH).name(Txt.parse(MConf.get().pendingChangeGuiName)).setLore(MConf.get().pendingChangeGuiLoreNoChange.stream().map(Txt::parse).collect(Collectors.toList())));
		}
		
		chestGui.getInventory().setItem(40, new ItemBuilder(Material.PAPER).name(Txt.parse(MConf.get().shieldInformationGuiName)).setLore(MConf.get().shieldInformationGuiLore.stream().map(Txt::parse).collect(Collectors.toList())));
		chestGui.getInventory().setItem(41, new ItemBuilder(Material.REDSTONE_BLOCK).name(Txt.parse(MConf.get().disableShieldGuiItemName)).setLore(MConf.get().disableShieldGuiItemLore.stream().map(Txt::parse).collect(Collectors.toList())));
		chestGui.setAction(41, new ActionDisableShield(faction, msender.describeTo(msenderFaction, true), msenderFaction.describeTo(msender)));
		chestGui.setAction(41, new ActionShieldOpenConfirm("Disable", faction, 0, msender.describeTo(msenderFaction, true), msenderFaction.describeTo(msender), null, null));
		
		return chestGui;
	}
	
	public String getTimeFormatted(int hourOfDay, int minute)
	{
		String s = ((hourOfDay > 12) ? hourOfDay % 12 : hourOfDay) + ":" + (minute < 10 ? ("0" + minute) : minute) + " " + ((hourOfDay >= 12) ? "PM" : "AM");
		
		if (s.equals("0:00 AM"))
		{
			s = "Midnight";
		}
		else if (s.equals("0:00 PM"))
		{
			s = "Midday";
		}
		
		return s;
	}
	
}