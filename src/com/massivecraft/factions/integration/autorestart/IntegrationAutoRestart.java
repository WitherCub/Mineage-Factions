package com.massivecraft.factions.integration.autorestart;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.Integration;

public class IntegrationAutoRestart extends Integration {
    private static IntegrationAutoRestart i = new IntegrationAutoRestart();
    public static IntegrationAutoRestart get() { return i; }

    public IntegrationAutoRestart() {
        this.setPluginName("UltimateAutoRestart");
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if(active) {
            Factions.get().activate(TaskAutoRestart.class);
        } else {
            if(TaskAutoRestart.get().isActive()) {
                TaskAutoRestart.get().setActive(false);
            }
        }
    }
}
