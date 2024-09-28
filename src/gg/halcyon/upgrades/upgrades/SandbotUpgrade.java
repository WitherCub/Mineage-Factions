package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsSandbots;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.entity.Sandbot;
import gg.halcyon.upgrades.Upgrade;
import org.bukkit.Material;

public class SandbotUpgrade extends Upgrade {
    @Override
    public int getMaxLevel() {
        return MissionUpgradeConf.get().sandbotUpgrade.getMaxLevel();
    }

    @Override
    public String getUpgradeName() {
        return MissionUpgradeConf.get().sandbotUpgrade.getUpgradeName();
    }

    @Override
    public String[] getCurrentUpgradeDescription() {
        return MissionUpgradeConf.get().sandbotUpgrade.getCurrentUpgradeDescription();
    }

    @Override
    public String[] getNextUpgradeDescription() {
        return MissionUpgradeConf.get().sandbotUpgrade.getNextUpgradeDescription();
    }

    @Override
    public Material getUpgradeItem() {
        return MissionUpgradeConf.get().sandbotUpgrade.getUpgradeItem();
    }

    @Override
    public Integer[] getCost() {
        return MissionUpgradeConf.get().sandbotUpgrade.getCost();
    }

    @Override
    public void onUpgrade(Faction faction) {
        for (int slot : CmdFactionsSandbots.get().sandbotCenterGuiSlots) {
            if (faction.getSandbots().get(slot) == null) {
                faction.addSandbot(slot, new Sandbot());
                break;
            }
        }
    }

    @Override
    public int getInventorySlot() {
        return MissionUpgradeConf.get().sandbotUpgrade.getInventorySlot();
    }
}