package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;

public class CmdFactionsDeletestrike extends FactionsCommand
{

    public CmdFactionsDeletestrike()
    {
        this.addParameter(TypeFaction.get(), "faction", "you");
        this.addParameter(TypeInteger.get(), "index", "");
    }

    @Override
    public void perform() throws MassiveException
    {
        Faction faction = this.readArg(msenderFaction);
        int reasonIndex = this.readArg(0) - 1;

        if (reasonIndex < 0) {
            msender.msg("&cPlease specify a strike index to remove the reason from on /f strikes.");
            return;
        }

        faction.removeWarning(faction.getFactionWarnings().get(reasonIndex));
        msender.msg(MConf.get().removeStrikeMsg.replace("%faction%", faction.getName()).replace("%index%", String.valueOf(reasonIndex + 1)));
    }

}