package com.massivecraft.factions.integration.polar;

import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class PolarColl extends Coll<PolarConf> {
    private static final PolarColl i = new PolarColl();
    public static final PolarColl get() { return i; }

    public PolarColl() {
        super("factions_polar");
    }

    // -------------------------------------------- //
    // STACK TRACEABILITY
    // -------------------------------------------- //

    @Override
    public void onTick()
    {
        super.onTick();
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void setActive(boolean active)
    {
        super.setActive(active);
        if (!active) return;
        PolarConf.set(this.get(MassiveCore.INSTANCE, true));
    }
}
