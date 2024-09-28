package com.massivecraft.factions.entity.objects;

import com.massivecraft.factions.entity.MPlayer;
import gg.halcyon.stats.EngineStats;

public class PlayerStats
{
	
	private String playerId;
	private Long blocksBroken;
	private Long blocksPlaced;
	private Long playersKilled;
	private Long mobsKilled;
	private Long deaths;
	private Long fishCaught;
	private Long timePlayed;
	
	public PlayerStats(String playerId)
	{
		this.playerId = playerId;
		this.blocksBroken = 0L;
		this.blocksPlaced = 0L;
		this.playersKilled = 0L;
		this.mobsKilled = 0L;
		this.deaths = 0L;
		this.fishCaught = 0L;
		this.timePlayed = 0L;
	}
	
	public Long getBlocksBroken()
	{
		return blocksBroken;
	}
	
	public void incrementBlocksBroken()
	{
		this.blocksBroken = this.blocksBroken + 1;
	}
	
	public Long getBlocksPlaced()
	{
		return blocksPlaced;
	}
	
	public void incrementBlocksPlaced()
	{
		this.blocksPlaced = this.blocksPlaced + 1;
	}
	
	public Long getPlayersKilled()
	{
		return playersKilled;
	}
	
	public void incrementPlayersKilled()
	{
		this.playersKilled = this.playersKilled + 1;
	}
	
	public Long getMobsKilled()
	{
		return mobsKilled;
	}
	
	public void incrementMobsKilled()
	{
		this.mobsKilled = this.mobsKilled + 1;
	}
	
	public Long getDeaths()
	{
		return deaths;
	}
	
	public void incrementDeaths()
	{
		this.deaths = this.deaths + 1;
	}
	
	public Long getFishCaught()
	{
		return fishCaught;
	}
	
	public void incrementFishCaught()
	{
		this.fishCaught = this.fishCaught + 1;
	}
	
	public String getPlayerId()
	{
		return playerId;
	}
	
	public Long getTimePlayed()
	{
		return timePlayed + EngineStats.get().getPlaytimeSinceLogin(MPlayer.get(playerId));
	}
	
	public void setTimePlayed(Long timePlayed)
	{
		this.timePlayed = timePlayed;
	}
}
