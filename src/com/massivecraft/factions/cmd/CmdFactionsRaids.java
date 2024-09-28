package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.factions.entity.RaidDataStorage;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsRaids extends FactionsCommand {

    public CmdFactionsRaids() {
        this.addParameter(Parameter.getPage());
        this.addRequirements(RequirementHasPerm.get(Perm.RAIDS));
        this.setVisibility(Visibility.INVISIBLE);
    }

    @Override
    public void perform() throws MassiveException {
        int page = this.readArg();

        List<Mson> raidList = new ArrayList<>();

        for (RaidData raidData : RaidDataStorage.get().getRaidData()) {
            Faction factionRaided = Faction.get(raidData.getFactionRaidedId());
            Faction factionRaiding = Faction.get(raidData.getFactionRaidingId());
            Mson mson = mson(Txt.parse("&2%s is being raided by %s &7- &e%s total time left &7(Phase %s)", factionRaided.getName(), factionRaiding.getName(), TimeUtil.formatTimeFormat(raidData.getTotalMillisRemaining()).trim(), raidData.getPhase()));
            raidList.add(mson);
        }

        if (raidList.isEmpty()) {
            raidList.add(mson(Txt.parse("&eThere is currently nobody raiding.")));
        }

        final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Factions being raided"), page, raidList, new Msonifier<Mson>() {
            @Override
            public Mson toMson(Mson mson, int i) {
                return raidList.get(i);
            }
        });

        pager.message();
    }

}