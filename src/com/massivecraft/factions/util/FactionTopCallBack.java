package com.massivecraft.factions.util;

import org.bukkit.Chunk;
import org.bukkit.World;

public abstract class FactionTopCallBack implements World.ChunkLoadCallback {
	
	private ChunkPos chunk;
	
	public FactionTopCallBack(ChunkPos chunk) {
		this.chunk = chunk;
	}
	
	public abstract void onLoad(Chunk chunk);
	
}
