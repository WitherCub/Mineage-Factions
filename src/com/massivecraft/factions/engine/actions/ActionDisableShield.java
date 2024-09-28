package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
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
			MixinMessage.get().msgOne(player, MConf.get().changingShieldHoursOffMsg);
			return false;
		}
		
		if (!MPerm.getPermShield().has(MPlayer.get(player), faction, true)) return false;
		
		if (faction.getShieldedHoursStartTime() == null)
		{
			MixinMessage.get().msgOne(player, MConf.get().shieldNotEnabledMsg);
			return false;
		}
		
		if (faction.getShieldedHoursCooldownFromDisable() != null)
		{
			MixinMessage.get().msgOne(player, MConf.get().shieldAlreadyDisabledMsg.replace("%time%", TimeUtil.formatTime(MConf.get().shieldHoursChangeTimeBeforeUpdate - (System.currentTimeMillis() - faction.getShieldedHoursCooldownFromDisable())).trim()));
			return false;
		}
		
		faction.setShieldedHoursStartTime(null);
		faction.setShieldedHoursCooldownFromDisable(System.currentTimeMillis());
		faction.requestChangeShieldedHours(null, null);
		faction.msg(MConf.get().shieldedHoursDisabledMsg.replace("%player%", playerStr).replace("%faction%", factionStr));
		return true;
	}
	
}