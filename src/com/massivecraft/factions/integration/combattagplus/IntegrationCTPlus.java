package com.massivecraft.factions.integration.combattagplus;

import com.massivecraft.factions.integration.autorestart.IntegrationAutoRestart;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationCTPlus extends Integration {
    private static IntegrationCTPlus i = new IntegrationCTPlus();
    public static IntegrationCTPlus get() { return i; }

    public IntegrationCTPlus() {
        this.setPluginName("CombatTagPlus");
    }

    @Override
    public Engine getEngine() {
        return EngineCTPlus.get();
    }
}
