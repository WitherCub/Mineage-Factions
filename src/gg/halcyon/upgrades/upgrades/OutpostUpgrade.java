package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import gg.halcyon.upgrades.Upgrade;
import org.bukkit.Material;

public class OutpostUpgrade extends Upgrade {

    @Override
    public int getMaxLevel() {
        return MissionUpgradeConf.get().outpostCapSpeed.getMaxLevel();
    }

    @Override
    public String getUpgradeName() {
        return MissionUpgradeConf.get().outpostCapSpeed.getUpgradeName();
    }

    @Override
    public String[] getCurrentUpgradeDescription() {
        return MissionUpgradeConf.get().outpostCapSpeed.getCurrentUpgradeDescription();
    }

    @Override
    public String[] getNextUpgradeDescription() {
        return MissionUpgradeConf.get().outpostCapSpeed.getNextUpgradeDescription();
    }

    @Override
    public Material getUpgradeItem() {
        return MissionUpgradeConf.get().outpostCapSpeed.getUpgradeItem();
    }

    @Override
    public Integer[] getCost() {
        return MissionUpgradeConf.get().outpostCapSpeed.getCost();
    }

    @Override
    public void onUpgrade(Faction faction) {

    }

    @Override
    public int getInventorySlot() {
        return MissionUpgradeConf.get().outpostCapSpeed.getInventorySlot();
    }

}