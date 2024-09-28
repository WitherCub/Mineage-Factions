package com.massivecraft.factions.entity.objects;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Set;

public class CacheLocations {

    private HashMap<Chunk, Set<Location>> toCheckAndProcess;
    private long lastCheckMillis;

    public CacheLocations(HashMap<Chunk, Set<Location>> toCheckAndProcess, long lastCheckMillis) {
        this.toCheckAndProcess = toCheckAndProcess;
        this.lastCheckMillis = lastCheckMillis;
    }

    public long getLastCheckMillis() {
        return lastCheckMillis;
    }

    public HashMap<Chunk, Set<Location>> getToCheckAndProcess() {
        return toCheckAndProcess;
    }
}