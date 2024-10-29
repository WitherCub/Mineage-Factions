package com.massivecraft.factions.coll;

import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;

public class LangColl extends Coll<LangConf>
{
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static LangColl i = new LangColl();
    public static LangColl get() { return i; }

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
    public LangColl() {
        super("factions_lang");
    }

    @Override
    public void setActive(boolean active)
    {
        super.setActive(active);
        if (!active) return;
        LangConf.set(this.get(MassiveCore.INSTANCE, true));
    }

}
