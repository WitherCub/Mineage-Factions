package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.EngineExtras;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsCornerlist extends FactionsCommand
{

    public CmdFactionsCornerlist()
    {
        this.addAliases("corners");
        this.addParameter(Parameter.getPage());
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void perform() throws MassiveException
    {
        // Args
        int page = this.readArg();

        final List<Mson> cornerList = new ArrayList<Mson>();

        for (PS corner : EngineExtras.corners)
        {
            if (MConf.get().worldsToShowInCornerlist.stream().noneMatch(corner.getWorld(true)::equalsIgnoreCase)) {
                continue;
            }

            Faction factionAtCorner = BoardColl.get().getFactionAt(corner);

            Mson mson;

            if (factionAtCorner.isNone())
            {
                String lineToSet = "<k>" + corner.getChunkCoords().getChunkX() + ", " + corner.getChunkCoords().getChunkZ() + " <i>in <k>" + corner.getWorld();

                if (MConf.get().showFactionOwnerInCornerlist) {
                    lineToSet = lineToSet + " &7- &aavailable";
                }

                mson = mson(Txt.parse(lineToSet));
                mson = mson.tooltip(Txt.parse("<i>Block coordinates: <h>x:" + corner.getChunkCoords().getChunkX() * 16 + ", z:" + corner.getChunkCoords().getChunkZ() * 16));
            }
            else
            {
                String lineToSet = "<k>" + corner.getChunkCoords().getChunkX() + ", " + corner.getChunkCoords().getChunkZ() + " in " + corner.getWorld();

                if (MConf.get().showFactionOwnerInCornerlist) {
                    lineToSet = lineToSet + " &7- &cclaimed";
                }

                mson = mson(Txt.parse(lineToSet));
                mson = mson.tooltip(Txt.parse("<i>Block coordinates: <h>x:" + corner.getChunkCoords().getChunkX() * 16 + ", z:" + corner.getChunkCoords().getChunkZ() * 16), MConf.get().showFactionOwnerInCornerlist ? Txt.parse("<i>Owned by: <h>" + factionAtCorner.describeTo(msender, true)) : "");
            }

            cornerList.add(mson);
        }

        final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Corner Claims"), page, cornerList, new Msonifier<Mson>()
        {
            @Override
            public Mson toMson(Mson mson, int i)
            {
                return cornerList.get(i);
            }
        });

        // Pager Message
        pager.message();
    }

}