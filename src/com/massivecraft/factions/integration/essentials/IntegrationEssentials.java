package com.massivecraft.factions.integration.essentials;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationEssentials extends Integration {
    private static IntegrationEssentials i = new IntegrationEssentials();
    public static IntegrationEssentials get() { return i; }

    public IntegrationEssentials() {
        setPluginName("Essentials");
    }

    @Override
    public Engine getEngine() {
        return EngineEssentials.get();
    }
}
