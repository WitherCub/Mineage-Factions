package com.massivecraft.factions.entity;

import com.massivecraft.factions.entity.objects.FactionBreach;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;

import java.util.ArrayList;

@EditorName("config")
public class FactionBreachs extends Entity<FactionBreachs>
{
    // -------------------------------------------- //
    // META
    // -------------------------------------------- //

    private static FactionBreachs i;
    public static void set(FactionBreachs newI) { i = newI; }
    public static FactionBreachs get()
    {
        return i;
    }

    // -------------------------------------------- //
    // PRIMARY CLAIMS COLL
    // -------------------------------------------- //

    public ArrayList<FactionBreach> factionBreaches = new ArrayList<>();

    // -------------------------------------------- //
    // OVERRIDE: ENTITY
    // -------------------------------------------- //

    @Override
    public FactionBreachs load(FactionBreachs that)
    {
        super.load(that);
        this.setFactionBreaches(that.factionBreaches);
        return this;
    }

    public void setFactionBreaches(ArrayList<FactionBreach> factionBreaches)
    {
        this.factionBreaches = factionBreaches;
        this.changed();
    }

}