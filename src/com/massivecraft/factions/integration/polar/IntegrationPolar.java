package com.massivecraft.factions.integration.polar;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationPolar extends Integration {
    private static final IntegrationPolar i = new IntegrationPolar();
    public static IntegrationPolar get() { return i; }

    public IntegrationPolar() {
        this.setPluginName("PolarLoader");
    }

    @Override
    public Engine getEngine() {
        return EnginePolar.get();
    }
}
