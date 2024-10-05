package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.mixin.MixinMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionSetShieldedHour extends ChestActionAbstract
{
	
	private Faction faction;
	private int startHour;
	private String playerStr;
	private String factionStr;
	private String startTime;
	private String endTime;
	
	public ActionSetShieldedHour(Faction faction, int startHour, String playerStr, String factionStr, String startTime, String endTime)
	{
		this.faction = faction;
		this.startHour = startHour;
		this.playerStr = playerStr;
		this.factionStr = factionStr;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event, Player player)
	{
		if (!MConf.get().isShieldTimesChangeable())
		{
			MixinMessage.get().msgOne(player, LangConf.get().changingShieldHoursOffMsg);
			return false;
		}
		
		if (!MPerm.getPermShield().has(MPlayer.get(player), faction, true)) return false;
		
		if (faction.getShieldedHoursCooldownFromDisable() != null && System.currentTimeMillis() - faction.getShieldedHoursCooldownFromDisable() < MConf.get().shieldHoursChangeTimeBeforeUpdate)
		{
			MixinMessage.get().msgOne(player, LangConf.get().mustWaitBeforeChangingShieldedHoursFromDisableMsg.replace("%time%", TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursCooldownFromDisable())).trim()));
			return false;
		}
		
		if (faction.getShieldedHoursStartTime() == null)
		{
			faction.setShieldedHoursStartTime(startHour);
			faction.msg(LangConf.get().shieldHoursChangedMsg.replace("%player%", playerStr).replace("%faction%", factionStr).replace("%startHour%", startTime).replace("%endHour%", endTime));
			return true;
		}
		
		faction.requestChangeShieldedHours(startHour, System.currentTimeMillis());
		faction.msg(LangConf.get().shieldHoursRequestedChangeMsg.replace("%player%", playerStr).replace("%faction%", factionStr).replace("%startHour%", startTime).replace("%endHour%", endTime));
		return true;
	}
	
}