package com.massivecraft.factions.integration.supplydrop;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import me.wither.supplydrop.TaskNaturalSpawnDrop;
import me.wither.supplydrop.entity.Coords;
import org.bukkit.Location;

public class EngineSupplyDrop extends Engine {
    private static EngineSupplyDrop i = new EngineSupplyDrop();
    public static EngineSupplyDrop get() { return i; }

    public boolean isSupplyDrop(PS location) {
        if(IntegrationSupplyDrop.get().isIntegrationActive()) {
            if(TaskNaturalSpawnDrop.get().activeDrop()) {
                if(location.getWorld() == null || location.getBlockX() == null || location.getBlockY() == null || location.getBlockZ() == null) return false;

                Coords coords = new Coords(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

                return TaskNaturalSpawnDrop.get().getDropLocation().equals(coords);
            } else return false;
        } else return false;
    }
}
