package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import gg.halcyon.upgrades.Upgrade;
import net.minecraft.server.v1_8_R3.BlockCrops;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockGrowEvent;

public class CropGrowthUpgrade extends Upgrade {
    @Override
    public void onUpgrade(Faction faction) {

    }

    @Override
    public int getMaxLevel() {
        return MissionUpgradeConf.get().cropGrowth.getMaxLevel();
    }

    @Override
    public String getUpgradeName() {
        return MissionUpgradeConf.get().cropGrowth.getUpgradeName();
    }

    @Override
    public String[] getCurrentUpgradeDescription() {
        return MissionUpgradeConf.get().cropGrowth.getCurrentUpgradeDescription();
    }

    @Override
    public String[] getNextUpgradeDescription() {
        return MissionUpgradeConf.get().cropGrowth.getNextUpgradeDescription();
    }

    @Override
    public Material getUpgradeItem() {
        return Material.CACTUS;
    }

    @Override
    public Integer[] getCost() {
        return MissionUpgradeConf.get().cropGrowth.getCost();
    }

    @Override
    public int getInventorySlot() {
        return MissionUpgradeConf.get().cropGrowth.getInventorySlot();
    }

}