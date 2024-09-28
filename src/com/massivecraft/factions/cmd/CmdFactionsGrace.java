package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsGrace extends FactionsCommand {

    public CmdFactionsGrace() {
        this.addRequirements(RequirementHasPerm.get(Perm.GRACE));
    }

    @Override
    public void perform() throws MassiveException {
        MConf.get().setGracePeriod(!MConf.get().gracePeriod);
        msender.msg("&eGrace period has been %s&e.", MConf.get().gracePeriod ? Txt.parse("&aENABLED") : Txt.parse("&cDISABLED"));
    }

}