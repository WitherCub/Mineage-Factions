package com.massivecraft.factions.task;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsShield;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskUpdateShieldGui extends ModuloRepeatTask
{
	
	private static TaskUpdateShieldGui i = new TaskUpdateShieldGui();
	
	public static TaskUpdateShieldGui get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return 15000;
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
			
			if (!mplayer.getPlayer().getOpenInventory().getTitle().equals(Txt.parse(MConf.get().shieldMangerGuiTitle))) continue;
			
			Faction faction = mplayer.getFaction();
			Inventory inventory = mplayer.getPlayer().getOpenInventory().getTopInventory();
			
			int i = 0;
			
			for (int startHour : MConf.get().shieldStartEndHours.keySet())
			{
				int endHour = MConf.get().shieldStartEndHours.get(startHour);
				
				String currentTime = CmdFactionsShield.get().getTimeFormatted(date.getHours(), date.getMinutes());
				String startHourFormatted = CmdFactionsShield.get().getTimeFormatted(startHour, 0);
				String endHourFormatted = CmdFactionsShield.get().getTimeFormatted(endHour, 0);
				
				List<String> lore = new ArrayList<>();
				
				if (faction.getShieldedHoursStartTime() != null && faction.getShieldedHoursStartTime() == startHour)
				{
					MConf.get().shieldManagerGuiLoreCurrently.forEach(s -> lore.add(Txt.parse(s.replace("%startHour%", startHourFormatted).replace("%endHour%", endHourFormatted).replace("%currentTime%", currentTime))));
					inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(13).setLore(lore));
				}
				else if (faction.getShieldedHoursChangeRequestNewStartTime() != null && faction.getShieldedHoursChangeRequestNewStartTime() == startHour)
				{
					MConf.get().shieldManagerGuiLoreChangeTo.forEach(s -> lore.add(Txt.parse(s.replace("%startHour%", startHourFormatted).replace("%endHour%", endHourFormatted).replace("%currentTime%", currentTime))));
					inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(4).setLore(lore));
				}
				else
				{
					MConf.get().shieldManagerGuiLoreChange.forEach(s -> lore.add(Txt.parse(s.replace("%startHour%", startHourFormatted).replace("%endHour%", endHourFormatted).replace("%currentTime%", currentTime))));
					inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(14).setLore(lore));
				}
				
				i++;
			}
		}
	}
	
}