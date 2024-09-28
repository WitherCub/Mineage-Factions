package com.massivecraft.factions.engine;

import com.massivecraft.factions.entity.FactionTopData;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.Engine;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EngineFtop extends Engine
{
	
	private static EngineFtop i = new EngineFtop();
	
	public static EngineFtop get()
	{
		return i;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent event)
	{
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		String locationAsString = blockToString(block);
		FactionTopData.get().addPlacetime(locationAsString, System.currentTimeMillis());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(SilkSpawnersSpawnerBreakEvent event)
	{
		if (event.isCancelled()) return;
		String locationAsString = blockToString(event.getBlock());
		FactionTopData.get().removePlacetime(locationAsString);
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event)
	{
		for (Block block : event.blockList())
		{
			if (block.getType() == Material.MOB_SPAWNER)
			{
				FactionTopData.get().removePlacetime(blockToString(block));
			}
		}
	}
	
	public static String blockToString(Block block)
	{
		return block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
	}
	
	public int calculateSpawnerWorth(double price, Long placeTime)
	{
		long timeElapsed = (System.currentTimeMillis() - placeTime) / 1000;
		
		if (timeElapsed > MConf.get().gainFullValueAfterPlaceForXSeconds)
		{
			return (int) price;
		}
		else
		{
			double toValue = (timeElapsed / MConf.get().gainFullValueAfterPlaceForXSeconds) + MConf.get().spawnerInitalValue;
			if (toValue >= 1.0) return (int) price;
			return (int) (price * (toValue));
		}
	}
	
	public int calculateSpawnerWorth(BlockState blockState, double price)
	{
		if (MConf.get().gainFullValueAfterPlaceForXSeconds <= 0) return (int) price;
		
		Long placeTime = FactionTopData.get().getPlaceTimes().getOrDefault(blockToString(blockState), null);
		
		if (placeTime == null)
		{
			return (int) price;
		}
		
		return calculateSpawnerWorth(price, placeTime);
	}
	
	public static String blockToString(BlockState block)
	{
		return block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
	}
	
}