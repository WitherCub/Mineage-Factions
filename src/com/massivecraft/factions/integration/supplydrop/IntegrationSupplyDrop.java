package com.massivecraft.factions.integration.supplydrop;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationSupplyDrop extends Integration {
    private static IntegrationSupplyDrop i = new IntegrationSupplyDrop();
    public static IntegrationSupplyDrop get() { return i; }

    public IntegrationSupplyDrop() {
        setPluginName("SupplyDrops");
    }

    @Override
    public Engine getEngine() {
        return EngineSupplyDrop.get();
    }
}
