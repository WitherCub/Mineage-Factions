package com.massivecraft.factions.engine;

import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.Sandbot;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Objects;

public class EngineSandbots extends Engine {

    private static EngineSandbots i = new EngineSandbots();

    public static EngineSandbots get() {
        return i;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLandClaim(EventFactionsChunksChange event) {
        for (Faction faction : event.getOldFactionChunks().keySet()) {
            for (Sandbot sandbot : faction.getSandbots().values()) {
                if (sandbot == null || sandbot.getPs() == null) continue;

                PS chunkPs = sandbot.getPs().getChunk(true);

                for (PS ps : event.getChunks()) {
                    if (Objects.equals(ps.getChunkX(), chunkPs.getChunkX()) && Objects.equals(ps.getChunkZ(), chunkPs.getChunkZ())) {
                        faction.despawnSandbot(sandbot);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Entity[] entities;
        for (int length = (entities = event.getChunk().getEntities()).length, i = 0; i < length; ++i) {
            Faction factionAt = BoardColl.get().getFactionAt(PS.valueOf(event.getChunk()));
            if (factionAt.isSandbot(entities[i])) {
                event.setCancelled(true);
                event.getChunk().load();
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSandSpawn(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.SAND) {
            event.setCancelled(true);
        }
    }

}