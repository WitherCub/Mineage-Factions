package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.mixin.MixinMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionDisableShield extends ChestActionAbstract
{
	
	private Faction faction;
	private String playerStr;
	private String factionStr;
	
	public ActionDisableShield(Faction faction, String playerStr, String factionStr)
	{
		this.faction = faction;
		this.playerStr = playerStr;
		this.factionStr = factionStr;
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
		
		if (faction.getShieldedHoursStartTime() == null)
		{
			MixinMessage.get().msgOne(player, LangConf.get().shieldNotEnabledMsg);
			return false;
		}
		
		if (faction.getShieldedHoursCooldownFromDisable() != null)
		{
			MixinMessage.get().msgOne(player, LangConf.get().shieldAlreadyDisabledMsg.replace("%time%", TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursCooldownFromDisable())).trim()));
			return false;
		}
		
		faction.setShieldedHoursStartTime(null);
		faction.setShieldedHoursCooldownFromDisable(System.currentTimeMillis());
		faction.requestChangeShieldedHours(null, null);
		faction.msg(LangConf.get().shieldedHoursDisabledMsg.replace("%player%", playerStr).replace("%faction%", factionStr));
		return true;
	}
	
}