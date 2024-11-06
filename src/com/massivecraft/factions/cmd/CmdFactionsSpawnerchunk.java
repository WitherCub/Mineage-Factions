package com.massivecraft.factions.cmd;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.util.ChunkPos;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Chunk;
import org.bukkit.block.CreatureSpawner;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdFactionsSpawnerchunk extends FactionsCommand {
    private static CmdFactionsSpawnerchunk i = new CmdFactionsSpawnerchunk();
    public static CmdFactionsSpawnerchunk get() { return i; }

    public Cache<UUID, SpawnerChunkRequest> playersSettingSpawnerChunks = CacheBuilder.newBuilder().expireAfterAccess(MConf.get().spawnerChunkRequestExpireAfterXSeconds, TimeUnit.SECONDS).build();
    public Cache<UUID, Long> spawnerCacheLastUse = CacheBuilder.newBuilder().expireAfterAccess(MConf.get().spawnerChunkCommandUseCooldownXSeconds, TimeUnit.SECONDS).build();

    public CmdFactionsSpawnerchunk() {
        this.addRequirements(ReqHasFaction.get());
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws MassiveException {
        Long spawnerCacheLastUseValue = spawnerCacheLastUse.getIfPresent(msender.getUuid());
        if (spawnerCacheLastUseValue != null) {
            msender.msg(LangConf.get().pleaseWaitXSecondsToUseSpawnerchunkCmdAgainMsg.replace("%seconds%", TimeUtil.formatPlayTime((System.currentTimeMillis() + (MConf.get().spawnerChunkCommandUseCooldownXSeconds * 1000)) - spawnerCacheLastUseValue)));
            return;
        }
        spawnerCacheLastUse.put(msender.getUuid(), System.currentTimeMillis());

        Chunk chunk = me.getLocation().getChunk();
        ChunkPos chunkPos = new ChunkPos(me.getLocation().getWorld().getName(), chunk.getX(), chunk.getZ());
        Faction factionAt = BoardColl.get().getFactionAt(PS.valueOf(me.getLocation()));

        if (factionAt == null || factionAt.isSystemFaction() || factionAt != msenderFaction) {
            msender.msg(LangConf.get().onlySetSpawnerchunkOwnTerritoryMsg);
            return;
        }

        if (!MPerm.getPermSpawnerchunk().has(msender, msenderFaction, true)) return;

        if (factionAt.getSpawnerChunks().contains(chunkPos)) {
            int spawnersInChunk = (int) Arrays.stream(chunk.getTileEntities()).filter(blockState -> blockState instanceof CreatureSpawner).count();

            if (spawnersInChunk > 0) {
                msender.msg(LangConf.get().cannotUnmarkSpawnerChunkWithSpawnersInChunkMsg.replace("%spawnersInChunk%", Factions.get().getPriceFormat().format(spawnersInChunk)));
                return;
            }

            factionAt.removeSpawnerChunk(chunkPos);
            msender.msg(LangConf.get().unmarkedSpawnerchunkMsg);
            return;
        }

//        boolean nextToSpawnerChunk = true;
//
//        if (!factionAt.getSpawnerChunks().isEmpty()) {
//            nextToSpawnerChunk = false;
//
//            // Check surrounding chunks for spawner chunks
//            for (int x = (chunkPos.getX() - 1); x <= (chunkPos.getX() + 1); x++) {
//                for (int z = (chunkPos.getZ() - 1); z <= (chunkPos.getZ() + 1); z++) {
//                    PS checkPs = PS.valueOf(me.getLocation().getWorld().getName(), x, z);
//                    if (BoardColl.get().getFactionAt(checkPs).getId().equals(factionAt.getId()) && factionAt.getSpawnerChunks().contains(new ChunkPos(me.getLocation().getWorld().getName(), x, z))) {
//                        nextToSpawnerChunk = true;
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (!nextToSpawnerChunk) {
//            msender.msg(MConf.get().mustBeNextToSpawnerchunkMsg);
//            return;
//        }

        playersSettingSpawnerChunks.put(msender.getUuid(), new SpawnerChunkRequest(PS.valueOf(me.getLocation()), System.currentTimeMillis()));
        LangConf.get().placeSpawnerToMarkMsg.stream().map(s -> Txt.parse(s.replace("%spawnerChunkLockdownRadius%", String.valueOf(MConf.get().spawnerChunkLockdownRadius)))).forEach(s -> msender.msg(s));
    }

    public static class SpawnerChunkRequest {
        public PS ps;
        Long millis;

        SpawnerChunkRequest(PS ps, Long millis) {
            this.ps = ps;
            this.millis = millis;
        }
    }

}