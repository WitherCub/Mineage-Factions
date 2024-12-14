package com.massivecraft.factions.cmd;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.factions.util.ChunkPos;
import com.massivecraft.factions.util.Cuboid;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CmdFactionsSetBaseRegion extends FactionsCommand {
    //public CmdFactionsSetCoreChunk() {
    //    this.setAliases("setcorechunk", "setcore");
    //    this.setDesc("Sets the center of where your shield will protect.");
    //
    //    this.addParameter(TypeFaction.get(), "faction", "you");
    //
    //    this.addRequirements(RequirementIsPlayer.get(), ReqHasFaction.get(), ReqRoleIsAtLeast.get(Rel.COLEADER));
    //}

    @Override
    public void perform()
    {
        MPlayer mplayer = MPlayer.get(me);
        Faction faction = mplayer.getFaction();

        if (!MPerm.getPermShield().has(msender, msenderFaction, true)) return;

        if (faction.getBaseRegionSetTime() != null && System.currentTimeMillis() - faction.getBaseRegionSetTime() < com.massivecraft.massivecore.util.TimeUnit.MILLIS_PER_MINUTE * MConf.get().baseRegionSetMinutesCooldown)
        {
            mplayer.msg(LangConf.get().baseRegionCooldownMsg.replace("%time%", StringUtils.trim(TimeUtil.formatTime((TimeUnit.MILLIS_PER_MINUTE * MConf.get().baseRegionSetMinutesCooldown) - (System.currentTimeMillis() - faction.getBaseRegionSetTime())))));
            return;
        }

        if(faction.isShielded()) {
            mplayer.msg("<rose>You cannon change base region while on shield.");
            return;
        }

        RaidData raidData = RaidDataStorage.get().isBeingRaided(faction);
        if(raidData != null) {
            mplayer.msg("<rose>You cannon change base region while being raided.");
            return;
        }

        HashSet<PS> chunksToSave = new HashSet<>();

        for (int x = (me.getLocation().getChunk().getX() - MConf.get().baseRegionLoopRadius); x <= (me.getLocation().getChunk().getX() + MConf.get().baseRegionLoopRadius); x++)
        {
            for (int z = (me.getLocation().getChunk().getZ() - MConf.get().baseRegionLoopRadius); z <= (me.getLocation().getChunk().getZ() + MConf.get().baseRegionLoopRadius); z++)
            {
                PS chunk = PS.valueOf(me.getLocation().getWorld().getName(), x, z);

                if (isOutsideOfBorder(new Location(chunk.asBukkitWorld(), (chunk.getChunkX() * 16), 1, (chunk.getChunkZ() * 16))))
                {
                    continue;
                }

                Faction factionAt = BoardColl.get().getFactionAt(chunk);

                if (faction.isNone() || !faction.equals(factionAt))
                {
                    continue;
                }

                chunksToSave.add(chunk);
            }
        }

        faction.setBaseRegionPs(chunksToSave);
        faction.setBaseRegionSetTime(System.currentTimeMillis());
        mplayer.msg(LangConf.get().setBaseRegionMsg.replace("%claims%", String.valueOf(chunksToSave.size())));
    }

    private boolean isOutsideOfBorder(Location loc)
    {
        final double size = loc.getWorld().getWorldBorder().getSize() / 2;

        double x = loc.getX();
        double z = loc.getZ();

        return (x > size - 1) || (-x > size) || (z > size - 1) || (-z > size);
    }
}
