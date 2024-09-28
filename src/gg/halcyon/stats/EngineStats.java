package gg.halcyon.stats;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerStats;
import com.massivecraft.factions.entity.objects.PlayerStats;
import com.massivecraft.massivecore.Engine;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class EngineStats extends Engine
{

	private static EngineStats i = new EngineStats();
	public HashMap<String, Long> loginTimes = new HashMap<>();

	public static EngineStats get()
	{
		return i;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

		PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(event.getPlayer());

		if (mPlayerStats == null) {
			return;
		}

		mPlayerStats.incrementBlocksBroken();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

		PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(event.getPlayer());

		if (mPlayerStats == null) {
			return;
		}

		mPlayerStats.incrementBlocksPlaced();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
		if (event.getState() != State.CAUGHT_FISH) return;

		PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(event.getPlayer());

		if (mPlayerStats == null) {
			return;
		}

		mPlayerStats.incrementFishCaught();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKillMob(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player) return;
		if (event.getEntity().getKiller() == null) return;
		if (event.getEntity().getKiller().getGameMode() != GameMode.SURVIVAL) return;

		PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(event.getEntity().getKiller());

		if (mPlayerStats == null) {
			return;
		}

		mPlayerStats.incrementMobsKilled();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (!event.getEntity().isDead()) return;
		if (event.getEntity().hasMetadata("NPC")) return;

		Player deadPlayer = event.getEntity().getPlayer();

		if (deadPlayer != null) {
			PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(deadPlayer);

			if (mPlayerStats == null) {
				return;
			}

			mPlayerStats.incrementDeaths();
		}

		Player killerPlayer = event.getEntity().getKiller();

		if (event.getEntity() != null)
		{
			if (killerPlayer != null) {
				PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(killerPlayer);

				if (mPlayerStats == null) {
					return;
				}

				mPlayerStats.incrementPlayersKilled();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLogin(PlayerJoinEvent event)
	{
		loginTimes.put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		// update playtime before they leave server
		PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(event.getPlayer());

		if (mPlayerStats == null) {
			return;
		}

		mPlayerStats.setTimePlayed(mPlayerStats.getTimePlayed());

		// remove player from hashmap
		loginTimes.remove(event.getPlayer().getUniqueId().toString());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		// update playtime before they leave server
		PlayerStats mPlayerStats = MPlayerStats.get().getPlayerStats(event.getPlayer());

		if (mPlayerStats == null) {
			return;
		}

		mPlayerStats.setTimePlayed(MPlayerStats.get().getPlayerStats(event.getPlayer()).getTimePlayed());

		// remove player from hashmap
		loginTimes.remove(event.getPlayer().getUniqueId().toString());
	}

	public long getPlaytimeSinceLogin(MPlayer mPlayer)
	{
		if (loginTimes.containsKey(mPlayer.getUuid().toString()))
		{
			return System.currentTimeMillis() - loginTimes.get(mPlayer.getUuid().toString());
		}

		return 0;
	}

}