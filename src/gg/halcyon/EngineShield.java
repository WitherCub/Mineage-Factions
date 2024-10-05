package gg.halcyon;

import com.massivecraft.factions.cmd.CmdFactionsShield;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.RaidData;
import com.massivecraft.factions.entity.objects.RaidtimerShield;
import com.massivecraft.factions.util.TitleAPI;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.events.DispenseTNTEvent;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class EngineShield extends Engine {

    private static final EngineShield i = new EngineShield();
    private final MassiveMap<String, Long> lastRemindedFactionNotYourRaidMillis = new MassiveMap<>();

    public static EngineShield get() {
        return i;
    }

    @EventHandler
    public void onInventoryCloseShield(InventoryCloseEvent event) {
        if (event.getInventory().getName().equals(Txt.parse(GuiConf.get().shieldMangerGuiTitle))) {
            CmdFactionsShield.get().playersWithShieldGuiOpen.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerLogoutShield(PlayerKickEvent event) {
        CmdFactionsShield.get().playersWithShieldGuiOpen.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKickShield(PlayerKickEvent event) {
        CmdFactionsShield.get().playersWithShieldGuiOpen.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            PS explosionLocation = PS.valueOf(event.getLocation());
            Faction factionBreached = BoardColl.get().getFactionAt(explosionLocation);

            PS sourceLocation = PS.valueOf(((TNTPrimed) event.getEntity()).getSourceLoc());
            Faction factionBreaching = BoardColl.get().getFactionAt(sourceLocation);

            if (factionBreached == null) return;

            if (!MConf.get().allowRaidingWhileOnShield && factionBreaching.isShielded()) {
                event.setCancelled(true);
                return;
            }

            int chunkX = explosionLocation.getChunkX(true);
            int chunkZ = explosionLocation.getChunkZ(true);
            String worldName = explosionLocation.getWorld(true);

            if (!isPsInsideBaseRegion(factionBreached, worldName, chunkX, chunkZ)) {
            	return;
			}

            if (factionBreached.isShielded()) {
                event.setCancelled(true);
                return;
            }

            RaidtimerShield raidtimerShield = RaidDataStorage.get().isPhaseThreeProtected(factionBreached);

            if (raidtimerShield != null && System.currentTimeMillis() - raidtimerShield.getShieldStartMillis() <= (MConf.get().phaseThreeLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE)) {
                event.setCancelled(true);
                return;
            }

            RaidData raidData = RaidDataStorage.get().isBeingRaided(factionBreached);

            if (raidData != null && !factionBreached.getId().equals(factionBreaching.getId())) {
                if (!raidData.getFactionRaidingId().equals(factionBreaching.getId())) {
                    if (!lastRemindedFactionNotYourRaidMillis.containsKey(factionBreaching.getId()) || ((MConf.get().onlyMessageAboutRaidTimerEveryXMinutes * TimeUnit.MILLIS_PER_MINUTE) < System.currentTimeMillis() - lastRemindedFactionNotYourRaidMillis.get(factionBreaching.getId()))) {
                        if (!factionBreaching.getId().equals(factionBreached.getId())) {
                            lastRemindedFactionNotYourRaidMillis.put(factionBreaching.getId(), System.currentTimeMillis());
                            factionBreaching.msg(LangConf.get().alreadyBeingRaidedMsg.replace("%faction%", Faction.get(raidData.getFactionRaidingId()).getName()));
                        }
                    }
                    event.setCancelled(true);
                } else {
                    if (raidData.getPhase() == 2) {
                        factionBreaching.msg(LangConf.get().backToPhaseOneMsg.replace("%faction%", factionBreached.getName()));
                    }
                    raidData.restartRaidStartMillis(System.currentTimeMillis());
                }
            } else {
                if (event.getEntity().getLocation().getBlockY() >= MConf.get().raidTimerStartMinY && !factionBreaching.isNone() && !factionBreached.isNone() && !factionBreached.getId().equals(factionBreaching.getId())) {
                    factionBreaching.msg(LangConf.get().raidStartedMsg.replace("%faction%", factionBreached.getName()));
//					factionBreached.msg(MConf.get().raidStartedOnYouMsg.replace("%yourfaction%", factionBreached.describeTo(factionBreached, false)).replace("%faction%", factionBreaching.describeTo(factionBreached, true)));
                    RaidDataStorage.get().startRaid(factionBreaching, factionBreached);
                }
            }
        } else if (event.getEntity() instanceof Creeper) {
            PS explosionLocation = PS.valueOf(event.getLocation());
            Faction factionBreached = BoardColl.get().getFactionAt(explosionLocation);

            PS sourceLocation = PS.valueOf(event.getEntity().getLocation());
            Faction factionBreaching = BoardColl.get().getFactionAt(sourceLocation);
            if (factionBreached == null) return;

            if (!MConf.get().allowRaidingWhileOnShield && factionBreaching.isShielded()) {
                event.setCancelled(true);
                return;
            }

            RaidtimerShield raidtimerShield = RaidDataStorage.get().isPhaseThreeProtected(factionBreached);

            if (raidtimerShield != null && System.currentTimeMillis() - raidtimerShield.getShieldStartMillis() <= (MConf.get().phaseThreeLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE)) {
                event.setCancelled(true);
                return;
            }

			int chunkX = explosionLocation.getChunkX(true);
			int chunkZ = explosionLocation.getChunkZ(true);
			String worldName = explosionLocation.getWorld(true);

			if (!isPsInsideBaseRegion(factionBreached, worldName, chunkX, chunkZ)) {
				return;
			}

            if (factionBreached.isShielded()) {
             	event.setCancelled(true);
            }
        }
    }

    public boolean isPsInsideBaseRegion(Faction faction, String worldName, int chunkX, int chunkZ) {
        return faction.isChunkWithinRaidtimerRadiusFromSpawnerChunk(worldName, chunkX, chunkZ);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTntDispense(DispenseTNTEvent event) {
        boolean cancelDispense = true;

        Location location = event.getEntity().getLocation();
        PS chunkPsAt = PS.valueOf(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ());
        Faction factionAt = BoardColl.get().getFactionAt(chunkPsAt);

        boolean insideRaidclaim = false;

        int bound = MConf.get().raidClaimsAvailable + 1;
        for (int i1 = 1; i1 < bound; i1++) {
            if (!factionAt.isRaidClaimAvailable(i1) && factionAt.getRaidClaims().get(i1).contains(chunkPsAt)) {
                insideRaidclaim = true;
            }
        }

        if (isPsInsideBaseRegion(factionAt, location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ()) || insideRaidclaim) {
            cancelDispense = false;
        } else if (!factionAt.isSystemFaction()) {
            for (Player player : factionAt.getOnlinePlayers()) {
                TitleAPI.sendTitle(player, 10, 15, 10, LangConf.get().mustUseRaidclaimTitleTopMessage, LangConf.get().mustUseRaidclaimTitleBottomMessage);
            }
        }

        event.setCancelled(cancelDispense);
    }

}