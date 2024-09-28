package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.store.Entity;

import java.util.UUID;

public class Skulls extends Entity<Skulls> {

    private static Skulls i;
    public static void set(Skulls newI) { i = newI; }

    public MassiveMap<UUID, String> skullTextureCache = new MassiveMap<>();
    private long kicksLastResetMillis = System.currentTimeMillis();

    public static Skulls get() {
        return i;
    }

    public MassiveMap<UUID, String> getSkullTextureCache() {
        return skullTextureCache;
    }

    public void setSkullTextureCache(MassiveMap<UUID, String> skullTextureCache) {
        this.skullTextureCache = skullTextureCache;
        this.changed();
    }

    @Override
    public Skulls load(Skulls that) {
        this.setSkullTextureCache(that.skullTextureCache);
        this.setKicksLastResetMillis(that.kicksLastResetMillis);
        return this;
    }

    public long getKicksLastResetMillis() {
        return kicksLastResetMillis;
    }

    public void setKicksLastResetMillis(long kicksLastResetMillis) {
        this.kicksLastResetMillis = kicksLastResetMillis;
        this.changed();
    }

}