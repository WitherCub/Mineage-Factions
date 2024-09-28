package com.massivecraft.factions.entity;

import com.massivecraft.factions.entity.objects.PlayerStats;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import org.bukkit.entity.Player;

@EditorName("config")
public class MPlayerStats extends Entity<MPlayerStats>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	private static MPlayerStats i;
	public static void set(MPlayerStats newI) { i = newI; }
	public MassiveMap<String, PlayerStats> playerStats = new MassiveMap<>();

	// -------------------------------------------- //
	// PLAYER STATS COLL
	// -------------------------------------------- //

	public static MPlayerStats get()
	{
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //

	@Override
	public MPlayerStats load(MPlayerStats that)
	{
		super.load(that);
		this.setPlayerStats(that.playerStats);
		return this;
	}

	public void setPlayerStats(MassiveMap<String, PlayerStats> playerStats)
	{
		this.playerStats = playerStats;
		this.changed();
	}

	private void createPlayerStats(MPlayer mPlayer)
	{
		playerStats.put(mPlayer.getUuid().toString(), new PlayerStats(mPlayer.getUuid().toString()));
	}

	public PlayerStats getPlayerStats(MPlayer mPlayer)
	{
		if (mPlayer == null || mPlayer.getUuid() == null) return null;
		if (!playerStats.containsKey(mPlayer.getUuid().toString())) createPlayerStats(mPlayer);
		return playerStats.get(mPlayer.getUuid().toString());
	}

	public PlayerStats getPlayerStats(Player player)
	{
		if (player == null) {
			return null;
		}

		return getPlayerStats(MPlayer.get(player));
	}

}