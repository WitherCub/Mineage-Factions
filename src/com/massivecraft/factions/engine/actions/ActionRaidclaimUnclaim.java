package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.cmd.CmdFactionsRaidclaim;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.coll.FactionColl;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.mixin.MixinMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionRaidclaimUnclaim extends ChestActionAbstract {

    private final int raidclaimNumber;
    private final Faction faction;

    public ActionRaidclaimUnclaim(Faction faction, int raidclaimNumber) {
        this.faction = faction;
        this.raidclaimNumber = raidclaimNumber;
    }

    @Override
    public boolean onClick(InventoryClickEvent event, Player player) {
        MPlayer mPlayer = MPlayer.get(player);

        if (faction.isRaidClaimAvailable(raidclaimNumber)) {
            return false;
        }

        mPlayer.tryClaim(FactionColl.get().getNone(), faction.getRaidClaims().get(raidclaimNumber));
        faction.getRaidClaims().put(raidclaimNumber, null);
        faction.changed();
        MixinMessage.get().msgOne(player, LangConf.get().raidClaimRemovedMsg);

        player.openInventory(CmdFactionsRaidclaim.get().getRaidclaimGui(faction));
        return false;
    }
}