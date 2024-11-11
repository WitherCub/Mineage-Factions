package com.massivecraft.factions.integration.combattagplus;

import com.massivecraft.massivecore.Engine;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.PlayerCache;
import net.minelink.ctplus.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EngineCTPlus extends Engine {
    private static EngineCTPlus i = new EngineCTPlus();
    public static EngineCTPlus get() { return i; }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if(active) {
            combatTagPlus = (CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus");
        }
    }

    private CombatTagPlus combatTagPlus;

    public boolean isInCombat(Player player) {
        if(!isActive()) return false;
        if(combatTagPlus == null) return false;

        TagManager tagManager = combatTagPlus.getTagManager();

        return tagManager.isTagged(player.getUniqueId());
    }
}
