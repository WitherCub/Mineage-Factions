package com.massivecraft.factions.util;

import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.Objects;

public class ChunkPos {

	private String world;
	private int x, z;

	public ChunkPos(String world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public ChunkPos(String world, PS ps) {
		this.world = world;
		this.x = ps.getChunkX();
		this.z = ps.getChunkZ();
	}

	public ChunkPos(PS ps) {
		this.world = ps.getWorld();
		this.x = ps.getChunkX();
		this.z = ps.getChunkZ();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChunkPos chunkPos = (ChunkPos) o;
		return x == chunkPos.x &&
				z == chunkPos.z &&
				world.equals(chunkPos.world);
	}

	@Override
	public int hashCode() {
		return Objects.hash(world, x, z);
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public Chunk getChunk() {
		return Bukkit.getWorld(world).getChunkAt(x, z);
	}

	public PS getPS() {
		return PS.valueOf(world, x, z);
	}
}