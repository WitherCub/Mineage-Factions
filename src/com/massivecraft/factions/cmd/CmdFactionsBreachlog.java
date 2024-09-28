package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.objects.FactionBreach;
import com.massivecraft.factions.entity.FactionBreachs;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdFactionsBreachlog extends FactionsCommand
{

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public CmdFactionsBreachlog()
    {
        // Parameters
        this.addParameter(Parameter.getPage());
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void perform() throws MassiveException
    {
        // Args
        int page = this.readArg();

        // Pager Create
        final List<Mson> breachList = new ArrayList<Mson>();

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);

        for (FactionBreach factionBreach : FactionBreachs.get().factionBreaches)
        {
            String breachedAt = DateFormatUtils.format(factionBreach.timeOfBreach, "dd MMM HH:mm:ss");

            long timeRemaining = System.currentTimeMillis() - factionBreach.timeOfBreach;
            String breachedAgo = Txt.parse("<a>%s", TimeUtil.formatTime(timeRemaining));

            Faction factionBreaching = FactionColl.get().getByName(factionBreach.getFactionBreachingName());
            Faction factionBreached = FactionColl.get().getByName(factionBreach.getFactionBreachedName());

            if (factionBreaching != null && !factionBreaching.getId().equals(factionBreach.getFactionBreachingId())) factionBreaching = null;
            if (factionBreached != null && !factionBreached.getId().equals(factionBreach.getFactionBreachedId())) factionBreached = null;

            // make gray if factionid doesnt match (it isnt the same faction that breached)
            Mson mson = mson(
                    Txt.parse(
                            "<a>%s <b>%s <i>-> <b>%s <i>for <g>$%s",
                            breachedAt,
                            factionBreaching == null ? ChatColor.GRAY + factionBreach.getFactionBreachingName() : factionBreaching.describeTo(msender, true),
                            factionBreached == null ? ChatColor.GRAY + factionBreach.getFactionBreachedName() : factionBreached.describeTo(msender, true),
                            numberFormat.format(factionBreach.valueLost)
                    )
            );

            mson = mson.tooltip(Txt.parse("<a>%sago", breachedAgo));
            breachList.add(mson);
        }

        Collections.reverse(breachList);

        final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Breach Log"), page, breachList, new Msonifier<Mson>()
        {
            @Override
            public Mson toMson(Mson mson, int i)
            {
                return breachList.get(i);
            }
        });

        // Pager Message
        pager.message();
    }

}