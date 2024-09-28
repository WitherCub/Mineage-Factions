package com.massivecraft.factions.cmd;

import com.massivecraft.factions.engine.EngineRoster;
import com.massivecraft.factions.entity.MConf;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

public class CmdFactionsRoster extends FactionsCommand {

    private static CmdFactionsRoster i = new CmdFactionsRoster();
    public static CmdFactionsRoster get() { return i; }

    public CmdFactionsRosterAdd cmdFactionsRosterAdd = new CmdFactionsRosterAdd();
    public CmdFactionsRosterSetrank cmdFactionsRosterSetrank = new CmdFactionsRosterSetrank();
    public CmdFactionsRosterToggle cmdFactionsRosterToggle = new CmdFactionsRosterToggle();
    public CmdFactionsRosterSetkicks cmdFactionsRosterSetkicks = new CmdFactionsRosterSetkicks();

    @Override
    public void perform() {
        if (!MConf.get().rosterEnabled) {
            this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFaction rosters are disabled."));
            return;
        }

        i.getHelpCommand().execute(this.sender, this.getArgs());

        if (msender.isConsole()) {
            return;
        }

        Inventory inv = EngineRoster.get().getRosterGui(msenderFaction, me);
        if (inv != null) {
            me.openInventory(inv);
        }
    }

}