package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class GuiColl extends Coll<GuiConf>
{
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static GuiColl i = new GuiColl();
    public static GuiColl get() { return i; }

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
    public GuiColl() {
        super("factions_guis");
    }

    @Override
    public void setActive(boolean active)
    {
        super.setActive(active);
        if (!active) return;
        GuiConf.set(this.get(MassiveCore.INSTANCE, true));
    }

}
