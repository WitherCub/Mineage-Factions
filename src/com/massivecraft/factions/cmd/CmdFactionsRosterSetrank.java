package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

public class CmdFactionsRosterSetrank extends FactionsCommand {

    public CmdFactionsRosterSetrank() {
        this.addParameter(TypeMPlayer.get(), "player");
        this.addParameter(TypeRel.get(), "rel");
        this.addRequirements(ReqHasFaction.get());
    }

    @Override
    public void perform() throws MassiveException {
        if (!MConf.get().rosterEnabled) {
            this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFaction rosters are disabled."));
            return;
        }

        MPlayer mPlayer = this.readArg();
        Rel rel = this.readArg();

        if (!MConf.get().isRosterChangeable()) {
            msender.msg(MConf.get().modificationsToRosterNotEnabledMsg);
            return;
        }

        if (!MConf.get().acceptableDefaultRankFactionRelations.contains(rel)) {
            msender.msg(MConf.get().notValidDefaultRankMsg);
            return;
        }

        if (msenderFaction.isSystemFaction()) {
            msender.msg(MConf.get().systemFactionMsg);
            return;
        }

        if (!MConf.get().permittedRanksToModifyRoster.contains(msender.getRelationTo(msenderFaction))) {
            msender.msg(MConf.get().notPermittedToModifyRosterMsg);
            return;
        }

        if (!msenderFaction.getRoster().containsKey(mPlayer.getUuid())) {
            msender.msg(MConf.get().playerNotInRosterMsg);
            return;
        }

        msenderFaction.addToRoster(mPlayer.getUuid(), rel);
        msenderFaction.msg(MConf.get().playerDefaultRankSetMsg.replace("%rel%", Txt.getNicedEnum(rel)).replace("%player%", mPlayer.getName()));
    }


}