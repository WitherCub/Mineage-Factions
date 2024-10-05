package com.massivecraft.factions.task;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsShield;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.GuiConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskUpdateShieldGuiSeconds extends ModuloRepeatTask
{
	
	private static TaskUpdateShieldGuiSeconds i = new TaskUpdateShieldGuiSeconds();
	
	public static TaskUpdateShieldGuiSeconds get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return 1000;
	}
	
	@Override
	public void invoke(long l)
	{
		if (CmdFactionsShield.get().playersWithShieldGuiOpen.isEmpty()) return;
		
		Date date = new Date();
		date.setHours(TimeUtil.getTimeHours());
		date.setMinutes(TimeUtil.getTimeMinutes());
		date.setSeconds(TimeUtil.getTimeSeconds());
		
		for (UUID uuid : CmdFactionsShield.get().playersWithShieldGuiOpen)
		{
			MPlayer mplayer = MPlayer.get(uuid);

			if (!mplayer.getPlayer().getOpenInventory().getTitle().equals(Txt.parse(GuiConf.get().shieldMangerGuiTitle))) continue;

			Faction faction = mplayer.getFaction();
			Inventory inventory = mplayer.getPlayer().getOpenInventory().getTopInventory();

			if (faction.getShieldedHoursCooldownFromDisable() != null)
			{
				String timeRemaining = TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursCooldownFromDisable()));
				inventory.setItem(39, new ItemBuilder(Material.WATCH).name(Txt.parse(GuiConf.get().pendingChangeGuiName)).setLore(GuiConf.get().pendingChangeGuiLoreChangeAfterDisable.stream().map(s -> Txt.parse(s.replace("%time%", timeRemaining))).collect(Collectors.toList())));
			}
			else if (faction.getShieldedHoursChangeRequestNewStartTime() == null)
			{
				inventory.setItem(39, new ItemBuilder(Material.WATCH).name(Txt.parse(GuiConf.get().pendingChangeGuiName)).setLore(GuiConf.get().pendingChangeGuiLoreNoChange.stream().map(Txt::parse).collect(Collectors.toList())));
			}
			else
			{
				String newStartHourFormatted = CmdFactionsShield.get().getTimeFormatted(faction.getShieldedHoursChangeRequestNewStartTime(), 0);
				String newEndHourFormatted = CmdFactionsShield.get().getTimeFormatted(MConf.get().shieldStartEndHours.get(faction.getShieldedHoursChangeRequestNewStartTime()), 0);
				String timeRemaining = TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursChangeRequestMillis()));
				inventory.setItem(39, new ItemBuilder(Material.WATCH).name(Txt.parse(GuiConf.get().pendingChangeGuiName)).setLore(GuiConf.get().pendingChangeGuiLoreChangeActive.stream().map(s -> Txt.parse(s.replace("%startHour%", newStartHourFormatted).replace("%endHour%", newEndHourFormatted).replace("%time%", timeRemaining))).collect(Collectors.toList())));
			}
		}
	}
	
}