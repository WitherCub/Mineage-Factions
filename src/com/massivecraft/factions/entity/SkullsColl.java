package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class SkullsColl extends Coll<Skulls> {

    private static SkullsColl i = new SkullsColl();

    public static SkullsColl get() {
        return i;
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (!active) return;
        Skulls.set(this.get(MassiveCore.INSTANCE, true));
    }

}