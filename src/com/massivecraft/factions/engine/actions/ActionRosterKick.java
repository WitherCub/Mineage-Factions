package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsKick;
import com.massivecraft.factions.engine.EngineRoster;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionRosterKick extends ChestActionAbstract {

    private MPlayer mplayerToKick;

    public ActionRosterKick(MPlayer mplayerToKick) {
        this.mplayerToKick = mplayerToKick;
    }

    @Override
    public boolean onClick(InventoryClickEvent event, Player player) {
        MPlayer mPlayer = MPlayer.get(player);
        Faction faction = mPlayer.getFaction();

        if (!MConf.get().isRosterChangeable()) {
            mPlayer.msg(LangConf.get().modificationsToRosterNotEnabledMsg);
            return false;
        }

        if (faction.isSystemFaction()) {
            return false;
        }

        if (mPlayer.getUuid().equals(mplayerToKick.getUuid())) {
            mPlayer.msg(LangConf.get().cantKickYourselfMsg);
            return false;
        }

        Rel playerToKickRel = mplayerToKick.getRelationTo(faction);
        Rel playerKickingRel = mPlayer.getRelationTo(faction);

        if (!playerKickingRel.isAtLeast(playerToKickRel)) {
            mPlayer.msg(LangConf.get().cantKickHigherRankMsg);
            return false;
        }

        if (!MConf.get().unlimitedRosterKicks && faction.getRosterKicksRemaining() - 1 < 0) {
            mPlayer.msg(LangConf.get().noRosterKicksRemainingMsg);
            return false;
        }

        if (!MConf.get().permittedRanksToModifyRoster.contains(playerKickingRel)) {
            mPlayer.msg(LangConf.get().notPermittedToModifyRosterMsg);
            return false;
        }

        if (!faction.getRoster().containsKey(mPlayer.getUuid())) {
            mPlayer.msg(LangConf.get().playerNotInRosterMsg);
            return false;
        }

        if (mplayerToKick.getFaction().getId().equals(faction.getId())) {
            if (MPerm.getPermKick().has(mPlayer, faction, true)) {
                CmdFactionsKick.get().execute(mPlayer.getSender(), MUtil.list(mplayerToKick.getName()));
            } else {
                return false;
            }
        }

        if (!MConf.get().unlimitedRosterKicks) {
            faction.setRosterKicksRemaining(faction.getRosterKicksRemaining() - 1); // decrement roster kicks
            faction.changed();
        }

        faction.removeFromRoster(mplayerToKick.getUuid()); // remove from roster
        mPlayer.msg(LangConf.get().playerRemovedFromRosterMsg.replace("%player%", mplayerToKick.getName()));
        player.openInventory(EngineRoster.get().getRosterGui(faction, player));
        return false;
    }

}