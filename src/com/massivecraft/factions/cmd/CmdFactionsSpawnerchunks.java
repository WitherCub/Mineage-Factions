package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.util.ChunkPos;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsSpawnerchunks extends FactionsCommand {

    public CmdFactionsSpawnerchunks() {
        this.addParameter(Parameter.getPage());
        this.addParameter(TypeFaction.get(), "faction", "you");
    }

    @Override
    public void perform() throws MassiveException {
        int page = this.readArg();

        Faction faction = this.readArg(msenderFaction);

        if (MPerm.getPermSpawnerchunk().has(msender, faction, true)) {
            List<Mson> claimList = new ArrayList<>();

            for (ChunkPos chunkPos : faction.getSpawnerChunks()) {
                PS claim = PS.valueOf(chunkPos.getX(), chunkPos.getZ());
                Mson mson = mson(Txt.parse("<k>" + claim.getChunkCoords().getChunkX() + ", " + claim.getChunkCoords().getChunkZ() + " in " + chunkPos.getWorld() + "<i>. <n>(Spawner Chunk)"));
                mson = mson.tooltip(Txt.parse("<i>Block coordinates: <h>x:" + claim.getChunkCoords().getChunkX() * 16 + ", z:" + claim.getChunkCoords().getChunkZ() * 16));
                mson = mson.suggest("/f unclaim one " + claim.getChunkCoords().getChunkX() + " " + claim.getChunkCoords().getChunkZ() + " " + chunkPos.getWorld());
                claimList.add(mson);
            }

            final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Spawner Chunks for %s", faction.getName(msender)), page, claimList, new Msonifier<Mson>() {
                @Override
                public Mson toMson(Mson mson, int i) {
                    return claimList.get(i);
                }
            });

            pager.message();
        }
    }

}