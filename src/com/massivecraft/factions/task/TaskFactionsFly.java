package com.massivecraft.factions.task;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.engine.EngineFly;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.integration.combattagplus.EngineCTPlus;
import com.massivecraft.factions.integration.combattagplus.IntegrationCTPlus;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TaskFactionsFly extends ModuloRepeatTask
{
	private static TaskFactionsFly i = new TaskFactionsFly();
	
	public static TaskFactionsFly get()
	{
		return i;
	}
	
	@Override
	public long getDelayMillis()
	{
		return 1000L;
	}
	
	@Override
	public void invoke(long l)
	{
		if (!MConf.get().factionsFlyEnabled) return;
		
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			MPlayer mplayer = MPlayer.get(player);
			
			if (EngineFly.get().hasFlyBypass(player)) continue;

			TerritoryAccess access = BoardColl.get().getTerritoryAccessAt(PS.valueOf(player));
			
			Faction hostFaction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
			
			if (hostFaction == null) hostFaction = FactionColl.get().getNone();
			
			if (player.isFlying() || player.getAllowFlight())
			{
				if (!mplayer.isOverriding())
				{
					if (player.getLocation().getBlockY() > MConf.get().factionsFlyMaxHeight)
					{
						EngineFly.get().disableFlight(player, LangConf.get().factionsFlyDisabledMaxHeightMessage);
					}
					else if (hostFaction.isNone())
					{
						if (!player.hasPermission("factions.wildfly"))
						{
							EngineFly.get().disableFlight(player, LangConf.get().factionsFlyDisabledNoPerms);
						}
					}
					else if (!MPerm.getPermFly().has(mplayer, hostFaction, false))
					{
						EngineFly.get().disableFlight(player, LangConf.get().factionsFlyDisabledNoPerms);
					}
					else if (EngineFly.get().isEnemyNear(mplayer, player, hostFaction))
					{
						EngineFly.get().disableFlight(player, LangConf.get().factionsFlyDisabledEnemyNearbyMessage);
					}
					else if (MConf.get().flyOnlyInCoreNRaidClaims && !hostFaction.getBaseRegionPs().contains(PS.valueOf(player.getLocation().getChunk())) && !hostFaction.isInRaidClaim(player.getLocation().getChunk()))
					{
						EngineFly.get().disableFlight(player, "&cYou can only fly in base/raid claims.");
					}
					else if (MConf.get().flyOnlyInMinutesOldClaim > 0 && (access == null || System.currentTimeMillis() - access.getClaimTime() < MConf.get().flyOnlyInMinutesOldClaim * 1000L))
					{
						EngineFly.get().disableFlight(player, "&cYou can only fly in claims that are " + MConf.get().flyOnlyInMinutesOldClaim + " minutes old.");
					}
				}
			}
			else if (
					!EngineFly.get().playersWithFlyDisabled.contains(player.getUniqueId().toString()) &&
					!player.getAllowFlight() && !EngineFly.get().isEnemyNear(mplayer, player, hostFaction) &&
					(MPerm.getPermFly().has(mplayer, hostFaction, false) ||
					(hostFaction.isNone() && player.hasPermission("factions.wildfly")))
			)
			{
				if(MConf.get().flyOnlyInCoreNRaidClaims && !hostFaction.getBaseRegionPs().contains(PS.valueOf(player.getLocation().getChunk())) && !hostFaction.isInRaidClaim(player.getLocation().getChunk())) continue;
				if(IntegrationCTPlus.get().isActive() && EngineCTPlus.get().isInCombat(player)) continue;
				EngineFly.get().enableFlight(player, LangConf.get().factionsFlyEnabledMessage);
			}
		}
	}
	
}