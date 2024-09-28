package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class RaidDataStorageColl extends Coll<RaidDataStorage>
{
    private static RaidDataStorageColl i;

    static
    {
        RaidDataStorageColl.i = new RaidDataStorageColl();
    }

    public static RaidDataStorageColl get()
    {
        return RaidDataStorageColl.i;
    }

    public void onTick()
    {
        super.onTick();
    }

    public void setActive(final boolean active)
    {
        super.setActive(active);
        if (!active)
        {
            return;
        }
        RaidDataStorage.set(this.get(MassiveCore.INSTANCE, true));
    }
}