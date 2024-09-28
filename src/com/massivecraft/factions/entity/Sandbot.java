package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.EntityInternal;
import org.bukkit.Location;

import java.util.UUID;

public class Sandbot extends EntityInternal<Sandbot> {
    private String uuid;
    private PS loc;
    private Boolean despawned;
    public Sandbot() {
        this.uuid = null;
        this.loc = null;
        this.despawned = true;
    }

    public Sandbot(String uuid, Location location) {
        this.uuid = uuid;
        this.loc = PS.valueOf(location);
        this.despawned = true;
    }

    public String getUuidString() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
        this.changed();
    }

    public PS getPs() {
        return loc;
    }

    public void setDespawned(Boolean despawned) {
        this.despawned = despawned;
        this.changed();
    }

    public void setLoc(PS ps) {
        this.loc = ps;
        this.changed();
    }

    public UUID getUniqueId() {
        return uuid == null ? null : UUID.fromString(uuid);
    }

    public Location getLocation() {
        return loc.asBukkitLocation(true);
    }

    public Boolean isDespawned() {
        return despawned;
    }
}