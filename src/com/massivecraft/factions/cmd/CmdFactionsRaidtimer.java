package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.factions.entity.RaidDataStorage;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mixin.MixinMessage;

public class CmdFactionsRaidtimer extends FactionsCommand {

    public CmdFactionsRaidtimer() {
        this.addRequirements(RequirementIsPlayer.get());
        this.addRequirements(ReqHasFaction.get());
    }

    @Override
    public void perform() throws MassiveException {
        if (!MConf.get().allowAltsToCheckRaidtimerStatus && msender.isAlt()) {
            msender.msg(MConf.get().altsCantCheckRaidtimerStatusMsg);
            return;
        }

        RaidData raidData = RaidDataStorage.get().isFactionRaiding(msenderFaction);

        if (raidData == null) {
            MixinMessage.get().msgOne(me, MConf.get().yourFactionHasNoActiveRaidMsg);
        } else {
            MixinMessage.get().msgOne(me, MConf.get().cmdRaidTimeRemainingMsg.replace("%phase%", String.valueOf(raidData.getPhase())).replace("%time%", TimeUtil.formatTimeFormat(raidData.getTotalMillisRemaining()).trim()).replace("%otherFaction%", Faction.get(raidData.getFactionRaidedId()).describeTo(msender, true)));
        }
    }

}