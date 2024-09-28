package com.massivecraft.factions.integration.factionkore;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.feature.OutpostFeature;
import com.golfing8.kore.feature.RaidingOutpostFeature;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;

public class EngineFKore extends Engine {
    private static EngineFKore i = new EngineFKore();
    public static EngineFKore get() { return i; }

    RaidingOutpostFeature raidingOutpostFeature = null;
    OutpostFeature outpostFeature = null;

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if(active) {
            raidingOutpostFeature = (RaidingOutpostFeature) FactionsKore.get().getFeature("raiding-outpost");
            outpostFeature = (OutpostFeature) FactionsKore.get().getFeature("outpost");
        }
    }

    public String getROOwner() {
        try {
            return raidingOutpostFeature.getOutpost().getOwner();
        } catch (NullPointerException ignore) {
            return null;
        }
    }

    public boolean isRaidOutpost(PS ps) {
        try {
            return raidingOutpostFeature.getOutpost().isInRaidArea(ps.asBukkitLocation());
        } catch (NullPointerException | IllegalStateException ignore) {}
        try {
            return raidingOutpostFeature.getOutpost().isInRaidArea(ps.asBukkitBlock().getLocation());
        } catch (NullPointerException | IllegalStateException ignore) {}

        return false;
    }
}
