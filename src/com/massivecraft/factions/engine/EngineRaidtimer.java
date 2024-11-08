package com.massivecraft.factions.engine;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import gg.halcyon.EngineShield;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EngineRaidtimer extends Engine {

    private static EngineRaidtimer i = new EngineRaidtimer();

    public static EngineRaidtimer get() {
        return i;
    }

    @EventHandler
    public void onFactionDisband(EventFactionsDisband event)
    {
        if (event.getFaction().isSystemFaction()) return;

        RaidDataStorage.get().getRaidData().removeIf(raidData -> {
            if (raidData.getFactionRaidingId().equals(event.getFactionId()))
            {
                return true;
            }
            if (raidData.getFactionRaidedId().equals(event.getFactionId()))
            {
                Faction faction = Faction.get(raidData.getFactionRaidingId());
                if (faction != null) {
                    faction.msg(LangConf.get().raidHasEndedDisbandMsg.replace("%faction%", event.getFaction().getName()));
                }
                return true;
            }
            return false;
        });
    }

    //@EventHandler(priority = EventPriority.LOWEST)
    //public void onGenbucketPlace(EventGenbucketPlace event) {
    //    PS psAt = PS.valueOf(event.getPlayer().getLocation());
    //    Faction factionAt = BoardColl.get().getFactionAt(psAt);
    //
    //    RaidData raidData = RaidDataStorage.get().isBeingRaided(factionAt);
    //
    //    if (raidData != null && factionAt.getId().equals(raidData.getFactionRaidedId()) && raidData.getPhase() == 1) {
    //        int chunkX = psAt.getChunkX(true);
    //        int chunkZ = psAt.getChunkZ(true);
    //        String worldName = psAt.getWorld(true);
    //
    //        boolean isWithinCoreRadius = EngineShield.get().isPsInsideBaseRegion(factionAt, worldName, chunkX, chunkZ);
    //
    //        if (!isWithinCoreRadius) {
    //            return;
    //        }
    //
    //        event.setCancelled(true);
    //        MixinMessage.get().msgOne(event.getPlayer(), MConf.get().cantPlaceGenbucketLockdownMsg);
    //    }
    //}

    //@EventHandler(priority = EventPriority.LOWEST)
    //public void onPrinterToggle(PrinterToggleEvent event) {
    //    PS psAt = PS.valueOf(event.getPlayer().getLocation());
    //    Faction factionAt = BoardColl.get().getFactionAt(psAt);
    //
    //    RaidData raidData = RaidDataStorage.get().isBeingRaided(factionAt);
    //
    //    if (raidData != null && factionAt.getId().equals(raidData.getFactionRaidedId()) && raidData.getPhase() == 1) {
    //        int chunkX = psAt.getChunkX(true);
    //        int chunkZ = psAt.getChunkZ(true);
    //        String worldName = psAt.getWorld(true);
    //
    //        boolean isWithinCoreRadius = EngineShield.get().isPsInsideBaseRegion(factionAt, worldName, chunkX, chunkZ);
    //
    //        if (!isWithinCoreRadius) {
    //            return;
    //        }
    //
    //        event.setCancelled(true);
    //        MixinMessage.get().msgOne(event.getPlayer(), LangConf.get().cantTogglePrinterLockdownMsg);
    //    }
    //}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplosionPlace(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null || event.getItem().getType() != Material.MONSTER_EGG && event.getItem().getType() != Material.TNT) return;

        PS psAt = PS.valueOf(event.getPlayer().getLocation());
        Faction factionAt = BoardColl.get().getFactionAt(psAt);

        RaidData raidData = RaidDataStorage.get().isBeingRaided(factionAt);

        if (raidData != null && MPlayer.get(event.getPlayer()).getFaction().getId().equals(raidData.getFactionRaidedId())) {
            event.setCancelled(true);
            MixinMessage.get().msgOne(event.getPlayer(), LangConf.get().cantPlaceExplosionMsg);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent event) {
        PS psAt = PS.valueOf(event.getBlock());
        Faction factionAt = BoardColl.get().getFactionAt(psAt);

        RaidData raidData = RaidDataStorage.get().isBeingRaided(factionAt);

        if (raidData != null && factionAt.getId().equals(raidData.getFactionRaidedId())) {
            event.setCancelled(true);
            MixinMessage.get().msgOne(event.getPlayer(), LangConf.get().cantMineSpawnersDuringRaidtimerMsg);
        }
    }

}