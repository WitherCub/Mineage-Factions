package com.massivecraft.factions.coll;

import com.massivecraft.factions.entity.FactionBreachs;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class FactionBreachsColl extends Coll<FactionBreachs>
{
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static FactionBreachsColl i = new FactionBreachsColl();

    public static FactionBreachsColl get()
    {
        return i;
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
        FactionBreachs.set(this.get(MassiveCore.INSTANCE, true));
    }

}