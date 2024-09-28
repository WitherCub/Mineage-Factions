package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import org.bukkit.ChatColor;

public class CmdFactionsRosterSetkicks extends FactionsCommand {

    public CmdFactionsRosterSetkicks() {
        this.addParameter(TypeFaction.get(), "faction");
        this.addParameter(TypeInteger.get(), "amount");
        this.addRequirements(RequirementHasPerm.get(Perm.ROSTER_SETKICKS));
        this.setVisibility(Visibility.SECRET);
    }

    @Override
    public void perform() throws MassiveException {
        if (!MConf.get().rosterEnabled) {
            this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFaction rosters are disabled."));
            return;
        }

        Faction faction = this.readArg();
        Integer amount = this.readArg();

        if (faction.isSystemFaction()) {
            msender.msg(MConf.get().systemFactionMsg);
            return;
        }

        if (amount < 0) {
            msender.msg("&cYou can not set a faction's roster kicks negative.");
            return;
        }

        msenderFaction.setRosterKicksRemaining(amount);
        msenderFaction.changed();
        msender.msg("%s now has %s roster kicks remaining.", faction.describeTo(msender, true), amount);
    }

}