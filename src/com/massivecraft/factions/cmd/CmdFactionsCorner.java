package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.ps.PS;
import gg.halcyon.EngineExtras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CmdFactionsCorner extends FactionsCommand {

    public CmdFactionsCorner() {
        this.addRequirements(ReqHasFaction.get());
    }

    // bit-shifting is used because it's much faster than standard division and multiplication
    public static int blockToChunk(int blockVal) {    // 1 chunk is 16x16 blocks
        return blockVal >> 4;   // ">> 4" == "/ 16"
    }

    @Override
    public void perform() {
        if (!MPerm.getPermCorner().has(msender, msenderFaction, true)) return;

        PS to = PS.valueOf(me.getWorld().getName(), blockToChunk(me.getLocation().getBlockX()), blockToChunk(me.getLocation().getBlockZ()));

        if (EngineExtras.corners.contains(to)) {
            Faction cornerAt = BoardColl.get().getFactionAt(to);
            if (cornerAt != null && cornerAt.isNormal() && !cornerAt.equals(msenderFaction)) {
                msender.msg(LangConf.get().cannotClaimCornerMsg);
            } else {
                msenderFaction.msg(LangConf.get().attemptingClaimCornerMsg);
                List<PS> surrounding = new ArrayList<>(400);
                for (int x = 0; x < MConf.get().factionBufferSize; ++x) {
                    for (int z = 0; z < MConf.get().factionBufferSize; ++z) {
                        int newX = (to.getChunkX(true) > 0L) ? (to.getChunkX(true) - x) : (to.getChunkX(true) + x);
                        int newZ = (to.getChunkZ(true) > 0L) ? (to.getChunkZ(true) - z) : (to.getChunkZ(true) + z);
                        PS location = PS.valueOf(me.getWorld().getName(), newX, newZ);
                        Faction at = BoardColl.get().getFactionAt(location);
                        if (at == null || !at.isNormal()) {
                            surrounding.add(location);
                        }
                    }
                }
                surrounding.sort(Comparator.comparingInt(pS -> (int) getDistanceTo(pS, to)));
                if (surrounding.isEmpty()) {
                    msender.msg(LangConf.get().cannotClaimCornerMsg);
                } else {
                    int amount = 0;

                    for (PS ps : surrounding) {
                        if (msender.tryClaim(msenderFaction, Collections.singleton(ps))) {
                            amount++;
                        }
                    }

                    if (surrounding.size() == amount) {
                        msenderFaction.msg(LangConf.get().claimedCornerMsg.replace("%claims%", String.valueOf(amount)));
                    } else {
                        msenderFaction.msg(LangConf.get().couldNotClaimCornerMsg.replace("%claims%", String.valueOf(amount)));
                    }
                }
            }
        } else {
            msender.msg(LangConf.get().mustBeInCornerMsg);
        }
    }

    private double getDistanceTo(PS ps, PS that) {
        double dx = that.getBlockX(true) - ps.getBlockX(true);
        double dz = that.getBlockZ(true) - ps.getBlockZ(true);
        return Math.sqrt(dx * dx + dz * dz);
    }

}