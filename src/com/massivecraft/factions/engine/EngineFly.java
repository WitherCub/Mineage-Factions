package com.massivecraft.factions.engine;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class EngineFly extends Engine
{
	
	private static EngineFly i = new EngineFly();
	
	public MassiveSet<String> playersWithFlyDisabled = new MassiveSet<>();
	
	public static EngineFly get()
	{
		return i;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerToggleFly(PlayerToggleFlightEvent event)
	{
		if (!MConf.get().factionsFlyEnabled) return;
		
		Player player = event.getPlayer();
		
		if (playersWithFlyDisabled.contains(player.getUniqueId().toString())) return;
		
		if (!event.isFlying() || MUtil.isntPlayer(player) || hasFlyBypass(player)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		Faction hostFaction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
		
		if (isEnemyNear(mplayer, player, hostFaction) || hostFaction.isNone() && !player.hasPermission("factions.wildfly") || !hostFaction.isNone() && !MPerm.getPermFly().has(mplayer, hostFaction, false))
		{
			event.setCancelled(true);
			player.setAllowFlight(false);
			return;
		}
		
		mplayer.setWasFlying(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (!MConf.get().factionsFlyEnabled) return;
		
		Player player = event.getPlayer();
		
		if (MUtil.isntPlayer(player) || hasFlyBypass(player)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		Faction hostFaction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
		
		if (hostFaction == null) hostFaction = FactionColl.get().getNone();
		
		boolean hasPerm = MPerm.getPermFly().has(mplayer, hostFaction, false);
		
		if (hostFaction.isNone())
		{
			if (player.getAllowFlight() && !player.hasPermission("factions.wildfly"))
			{
				player.setAllowFlight(false);
				disableFlight(player, LangConf.get().factionsFlyDisabledNoPerms);
			}
			return;
		}
		
		if (!hasPerm)
		{
			disableFlight(player, LangConf.get().factionsFlyDisabledNoPerms);
		}
	}
	
	public void chunkChangeFlight(MPlayer mplayer, Player player, PS chunkFrom, PS chunkTo)
	{
		if (MUtil.isntPlayer(player) || hasFlyBypass(player)) return;
		
		Faction factionTo = BoardColl.get().getFactionAt(chunkTo);
		
		if (factionTo == null) factionTo = FactionColl.get().getNone();
		
		if (factionTo.isNone())
		{
			if (!player.hasPermission("factions.wildfly"))
			{
				disableFlight(player, LangConf.get().factionsFlyDisabledNoPerms);
			}
		}
		else if (!MPerm.getPermFly().has(mplayer, factionTo, false))
		{
			disableFlight(player, LangConf.get().factionsFlyDisabledNoPerms);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPearl(PlayerInteractEvent event)
	{
		if (!MConf.get().factionsFlyEnabled || !MConf.get().factionsFlyCanUsePearls || event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		Player player = event.getPlayer();
		
		if (MUtil.isntPlayer(player)) return;
		
		if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL && player.isFlying())
		{
			event.setCancelled(true);
			MixinMessage.get().msgOne(player, LangConf.get().factionsFlyNoPearlsMsg);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void negateNextFall(EntityDamageEvent event)
	{
		if (!MConf.get().factionsFlyEnabled || !(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		
		if (MUtil.isntPlayer(player) || event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
		
		MPlayer mplayer = MPlayer.get(player);
		
		if (mplayer.wasFlying())
		{
			mplayer.setWasFlying(false);
			event.setCancelled(true);
		}
	}
	
	public void disableFlight(Player player, String message)
	{
		if (MUtil.isntPlayer(player) || hasFlyBypass(player)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		
		if (player.isFlying())
		{
			player.setFlying(false);
			player.setVelocity(new Vector(0, 0, 0));
			player.setFallDistance(0.0f);
			mplayer.setWasFlying(true);
			
			if (message != null)
			{
				MixinMessage.get().msgOne(player, message);
			}
		}
		
		player.setAllowFlight(false);
	}
	
	public void enableFlight(Player player, String message)
	{
		player.setAllowFlight(true);
		
		if (message != null)
		{
			MixinMessage.get().msgOne(player, message);
		}
	}
	
	public boolean hasFlyBypass(Player player)
	{
		return player.getGameMode() != GameMode.SURVIVAL || player.hasPermission(LangConf.get().factionsFlyOverridePerm);
	}
	
	public boolean isEnemyNear(MPlayer mplayer, Player player, Faction hostFaction)
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if (p.getWorld() != player.getWorld()) continue;
			
			Location playerLocation = player.getLocation();
			Location pLocation = p.getLocation();
			
			double distance = Math.pow(playerLocation.getX() - pLocation.getX(), 2.0) + Math.pow(playerLocation.getZ() - pLocation.getZ(), 2.0);
			
			if (distance > MConf.get().factionsFlyXZDistanceCheck * MConf.get().factionsFlyXZDistanceCheck || Math.abs(playerLocation.getY() - pLocation.getY()) > MConf.get().factionsFlyYDistanceCheck)
				continue;
			
			if (p == player || hasFlyBypass(p) || !player.canSee(p) || p.isDead()) continue;
			
			MPlayer pMplayer = MPlayer.get(p);
			
			if (pMplayer == null || pMplayer.getFaction().getId().equals(mplayer.getFaction().getId()) || pMplayer.isStealth())
				continue;
			
			if (pMplayer.getRelationTo(hostFaction) == Rel.ENEMY || pMplayer.getRelationTo(mplayer) == Rel.ENEMY)
				return true;
		}
		
		return false;
	}
	
}