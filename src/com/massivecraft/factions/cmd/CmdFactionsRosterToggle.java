package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

public class CmdFactionsRosterToggle extends FactionsCommand {

    public CmdFactionsRosterToggle() {
        this.addRequirements(RequirementHasPerm.get(Perm.ROSTER_TOGGLE));
        this.setVisibility(Visibility.SECRET);
    }

    @Override
    public void perform() {
        if (!MConf.get().rosterEnabled) {
            this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFaction rosters are disabled."));
            return;
        }

        MConf.get().setRosterChangeable(!MConf.get().isRosterChangeable());
        msender.msg("&eRoster modifications have been %s&e.", MConf.get().isRosterChangeable() ? Txt.parse("&aENABLED") : Txt.parse("&cDISABLED"));
    }

}