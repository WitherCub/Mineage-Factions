package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionSpawnSandbot extends ChestActionAbstract {

    private Faction faction;
    private int slot;
    private int sandbotNumber;

    public ActionSpawnSandbot(Faction faction, int slot, int sandbotNumber) {
        this.faction = faction;
        this.slot = slot;
        this.sandbotNumber = sandbotNumber;
    }

    @Override
    public boolean onClick(InventoryClickEvent event, Player player) {
        MPlayer mPlayer = MPlayer.get(player);

        if (!MPerm.getPermSandbots().has(mPlayer, faction, true)) return false;

        Sandbot sandbot = faction.getSandbots().get(slot);

        if (sandbot == null) {
            mPlayer.msg(LangConf.get().spawnbotSandPurchaseSandbotMsg.replace("%sandbotNumber%", String.valueOf(sandbotNumber)));
            return false;
        }

        if (!sandbot.isDespawned()) {
            mPlayer.msg(LangConf.get().sandbotSpawnAlreadySpawnedMsg);
        }

        PS psAt = PS.valueOf(player.getLocation());

        if (!faction.getId().equals(BoardColl.get().getFactionAt(psAt).getId())) {
            mPlayer.msg(LangConf.get().spawnbotMustBeInFactionLandMsg);
            return false;
        }

        faction.spawnSandbot(sandbot, psAt);
        mPlayer.msg(LangConf.get().spawnbotSpawnedMsg);
        return false;
    }
}