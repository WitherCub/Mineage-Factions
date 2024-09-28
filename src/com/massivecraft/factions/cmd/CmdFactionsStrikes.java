package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsStrikes extends FactionsCommand
{

    public CmdFactionsStrikes()
    {
        this.addParameter(TypeFaction.get(), "faction", "you");
        this.addParameter(Parameter.getPage());
    }

    @Override
    public void perform() throws MassiveException
    {
        Faction faction = this.readArg(msenderFaction);
        int page = this.readArg();

        final List<Mson> strikes = new ArrayList<>();

        faction.getFactionWarnings().forEach(s -> strikes.add(mson(Txt.parse(s))));

        final Pager<Mson> pager = new Pager<>(this, Txt.parse(MConf.get().strikePagerName.replace("%faction%", faction.getName())), page, strikes, (Msonifier<Mson>) (item, index) -> mson(Txt.parse("<a>%s. ", (index + 1))).add(strikes.get(index)));

        pager.message();
    }

}