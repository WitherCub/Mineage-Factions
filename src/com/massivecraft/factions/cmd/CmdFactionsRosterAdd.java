package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import org.bukkit.ChatColor;

public class CmdFactionsRosterAdd extends FactionsCommand {

    public CmdFactionsRosterAdd() {
        this.addParameter(TypeMPlayer.get(), "player");
        this.addParameter(TypeRel.get(), "rel", "recruit");
        this.addRequirements(ReqHasFaction.get());
    }

    @Override
    public void perform() throws MassiveException {
        if (!MConf.get().rosterEnabled) {
            this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFaction rosters are disabled."));
            return;
        }

        MPlayer mPlayer = this.readArg();
        Rel rel = this.readArg(Rel.RECRUIT);

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

        if (msenderFaction.getRoster().containsKey(mPlayer.getUuid())) {
            msender.msg(MConf.get().playerAlreadyInRosterMsg);
            return;
        }

        if (msenderFaction.getRoster().size() >= MConf.get().maxRosterSize) {
            msender.msg(MConf.get().factionRosterFullMsg);
            return;
        }

        msenderFaction.addToRoster(mPlayer.getUuid(), rel);
        msenderFaction.msg(MConf.get().playerAddedToRosterMsg.replace("%player%", mPlayer.getName()));
    }

}