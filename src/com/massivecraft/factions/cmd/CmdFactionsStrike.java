package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.mixin.MixinMessage;

public class CmdFactionsStrike extends FactionsCommand
{

    public CmdFactionsStrike()
    {
        this.addParameter(TypeFaction.get(), "faction", "you");
        this.addParameter(TypeString.get(), "reason", "", true);
    }

    @Override
    public void perform() throws MassiveException
    {
        Faction faction = this.readArg(msenderFaction);
        String reason = this.readArg("");

        if (reason.isEmpty() || reason.equals(" "))
        {
            msender.msg("&cPlease specify a reason for this strike.");
            return;
        }

        faction.addWarning(reason);

        msender.msg(LangConf.get().addedStrikeMsg.replace("%faction%", faction.getName()));

        if (MConf.get().broadcastStrike)
        {
            MixinMessage.get().msgAll(LangConf.get().broadcastStrikeMsg.replace("%faction%", faction.getName()).replace("%reason%", reason));
        }
        else
        {
            faction.msg(LangConf.get().broadcastStrikeMsg.replace("%faction%", faction.getName()).replace("%reason%", reason));
        }
    }

}