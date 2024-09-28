package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionDespawnSandbot extends ChestActionAbstract {

    private Faction faction;
    private int slot;
    private int sandbotNumber;

    public ActionDespawnSandbot(Faction faction, int slot, int sandbotNumber) {
        this.faction = faction;
        this.slot = slot;
        this.sandbotNumber = sandbotNumber;
    }

    @Override
    public boolean onClick(InventoryClickEvent event, Player player) {
        MPlayer mPlayer = MPlayer.get(player);

        if (!MPerm.getPermSandbots().has(mPlayer, faction, true)) return false;

        Sandbot sandbot = faction.getSandbots().get(slot);

        if (sandbot == null || sandbot.isDespawned()) {
            mPlayer.msg(MConf.get().sandbotDespawnDontExistMsg);
            return false;
        }

        faction.despawnSandbot(sandbot);
        mPlayer.msg(MConf.get().sandbotDespawnDespawnedMsg.replace("%sandbotNumber%", String.valueOf(sandbotNumber)));
        return false;
    }

} 