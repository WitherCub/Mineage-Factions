package com.massivecraft.factions.integration.autorestart;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.task.TaskAlarm;
import com.massivecraft.massivecore.ModuloRepeatTask;
import dev.norska.uar.UltimateAutoRestart;
import dev.norska.uar.api.UARAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class TaskAutoRestart extends ModuloRepeatTask {
    private static TaskAutoRestart i = new TaskAutoRestart();

    public static TaskAutoRestart get()
    {
        return i;
    }

    @Override
    public long getDelayMillis()
    {
        return 50L;
    }

    public boolean isRestarting = false;

    @Override
    public void invoke(long l) {
        if(!IntegrationAutoRestart.get().isActive()) return;

        int interval = UARAPI.getInterval(UltimateAutoRestart.getInstance());

        if(!UltimateAutoRestart.getInstance().getTaskScheduler().isRunning()) {
            isRestarting = false;
            return;
        }

        if(interval < MConf.get().blockChestBeforeRestartSec && !isRestarting) {
            isRestarting = true;

            Bukkit.getOnlinePlayers().stream().filter(player -> player.getOpenInventory() != null).forEach(player -> {
                InventoryView inventory = player.getOpenInventory();
                if(inventory.getTitle().contains(" - Faction Chest")) player.closeInventory();
            });
        }

        if(isRestarting && interval > MConf.get().blockChestBeforeRestartSec) isRestarting = false;
    }
}
