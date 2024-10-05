package gg.halcyon.upgrades.upgrades;

import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.massivecore.ps.PS;
import gg.halcyon.upgrades.Upgrade;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerRateUpgrade extends Upgrade {

    @Override
    public int getMaxLevel() {
        return MissionUpgradeConf.get().spawnerRate.getMaxLevel();
    }

    @Override
    public String getUpgradeName() {
        return MissionUpgradeConf.get().spawnerRate.getUpgradeName();
    }

    @Override
    public String[] getCurrentUpgradeDescription() {
        return MissionUpgradeConf.get().spawnerRate.getCurrentUpgradeDescription();
    }

    @Override
    public String[] getNextUpgradeDescription() {
        return MissionUpgradeConf.get().spawnerRate.getNextUpgradeDescription();
    }

    @Override
    public Material getUpgradeItem() {
        return MissionUpgradeConf.get().spawnerRate.getUpgradeItem();
    }

    @Override
    public Integer[] getCost() {
        return MissionUpgradeConf.get().spawnerRate.getCost();
    }

    @Override
    public void onUpgrade(Faction faction) {

    }

    @Override
    public int getInventorySlot() {
        return MissionUpgradeConf.get().spawnerRate.getInventorySlot();
    }

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent event) {
        if(event.getSpawner() == null) return;

        Faction factionAt = BoardColl.get().getFactionAt(PS.valueOf(event.getLocation()));

        if (factionAt == null || factionAt.isSystemFaction()) return;
        if (event.getSpawner().getType() != Material.MOB_SPAWNER) return;

        BlockState state = event.getSpawner().getBlock().getState();

        if (state == null) return;
        if (isDefaultSpawnerSettings(state)) return;

        int spawnerRateLevel = factionAt.getLevel(getUpgradeName());

        if (spawnerRateLevel == 0) {
            setSpawner(state, 225, 625);
        } else if (spawnerRateLevel == 1) {
            setSpawner(state, 210, 580);
        } else if (spawnerRateLevel == 2) {
            setSpawner(state, 200, 530);
        } else if (spawnerRateLevel == 3) {
            setSpawner(state, 188, 498);
        } else if (spawnerRateLevel == 4) {
            setSpawner(state, 172, 452);
        } else if (spawnerRateLevel == 5) {
            setSpawner(state, 161, 437);
        }
    }

    private void setSpawner(BlockState state, int min, int max) {
        TileEntityMobSpawner tile = ((CraftCreatureSpawner) state).getTileEntity();
        final NBTTagCompound spawnerTag = new NBTTagCompound();
        tile.b(spawnerTag);

        spawnerTag.setShort("MinSpawnDelay", (short) min);
        spawnerTag.setShort("MaxSpawnDelay", (short) max);

        tile.a(spawnerTag);
        state.update();
    }

    private boolean isDefaultSpawnerSettings(BlockState state) {
        TileEntityMobSpawner tile = ((CraftCreatureSpawner) state).getTileEntity();
        final NBTTagCompound spawnerTag = new NBTTagCompound();
        tile.b(spawnerTag);

        return spawnerTag.getShort("MinSpawnDelay") == 225 && spawnerTag.getShort("MaxSpawnDelay") == 625;
    }

}