package com.massivecraft.factions.integration.factionkore;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationFKore extends Integration {
    private static IntegrationFKore i = new IntegrationFKore();
    public static IntegrationFKore get() { return i; }

    public IntegrationFKore() {
        this.setPluginName("FactionsKore");
    }

    @Override
    public Engine getEngine() {
        return EngineFKore.get();
    }
}
