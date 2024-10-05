package com.massivecraft.factions;

import com.massivecraft.factions.adapter.BoardAdapter;
import com.massivecraft.factions.adapter.BoardMapAdapter;
import com.massivecraft.factions.adapter.RelAdapter;
import com.massivecraft.factions.adapter.TerritoryAccessAdapter;
import com.massivecraft.factions.chat.ChatActive;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.type.TypeFactionChunkChangeType;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.factions.engine.*;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.migrator.MigratorFaction001Invitations;
import com.massivecraft.factions.entity.migrator.MigratorMConf001EnumerationUtil;
import com.massivecraft.factions.entity.migrator.MigratorMConf002CleanInactivity;
import com.massivecraft.factions.entity.migrator.MigratorMConf003CleanInactivity;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.integration.V18.IntegrationV18;
import com.massivecraft.factions.integration.V19.IntegrationV19;
import com.massivecraft.factions.integration.coreprotect.IntegrationCoreProtect;
import com.massivecraft.factions.integration.essentials.IntegrationEssentials;
import com.massivecraft.factions.integration.factionkore.IntegrationFKore;
import com.massivecraft.factions.integration.mobextras.IntegrationMobExtras;
import com.massivecraft.factions.integration.polar.IntegrationPolar;
import com.massivecraft.factions.integration.spigot.IntegrationSpigot;
import com.massivecraft.factions.integration.supplydrop.IntegrationSupplyDrop;
import com.massivecraft.factions.integration.worldguard.IntegrationWorldGuard;
import com.massivecraft.factions.mixin.PowerMixin;
import com.massivecraft.factions.task.*;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.command.type.RegistryType;
import com.massivecraft.massivecore.store.migrator.MigratorUtil;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.xlib.gson.GsonBuilder;
import gg.halcyon.EngineExtras;
import gg.halcyon.EngineShield;
import gg.halcyon.missions.MissionsManager;
import gg.halcyon.stats.EngineStats;
import gg.halcyon.upgrades.UpgradesManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

public class Factions extends MassivePlugin
{

	private NumberFormat priceFormat;

	public NumberFormat getPriceFormat() {
		return priceFormat;
	}

	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	// -------------------------------------------- //
	
	public final static String FACTION_MONEY_ACCOUNT_ID_PREFIX = "faction-";
	
	public final static String ID_NONE = "none";
	public final static String ID_SAFEZONE = "safezone";
	public final static String ID_WARZONE = "warzone";
	
	public final static String NAME_NONE_DEFAULT = ChatColor.DARK_GREEN.toString() + "Wilderness";
	public final static String NAME_SAFEZONE_DEFAULT = "SafeZone";
	public final static String NAME_WARZONE_DEFAULT = "WarZone";
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static Factions i;
	
	public Factions()
	{
		Factions.i = this;
	}
	
	public static Factions get()
	{
		return i;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// Mixins
	@Deprecated
	public PowerMixin getPowerMixin()
	{
		return PowerMixin.get();
	}
	
	@Deprecated
	public void setPowerMixin(PowerMixin powerMixin)
	{
		PowerMixin.get().setInstance(powerMixin);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void onEnableInner()
	{
		this.priceFormat = NumberFormat.getInstance();
		this.priceFormat.setGroupingUsed(true);

		// Register types
		RegistryType.register(Rel.class, TypeRel.get());
		RegistryType.register(EventFactionsChunkChangeType.class, TypeFactionChunkChangeType.get());
		
		// Register Faction accountId Extractor
		// TODO: Perhaps this should be placed in the econ integration somewhere?
		MUtil.registerExtractor(String.class, "accountId", ExtractorFactionAccountId.get());
		
		MigratorUtil.addJsonRepresentation(Board.class, Board.MAP_TYPE);

		// Activate
		this.activate(
				MigratorMConf001EnumerationUtil.class,
				MigratorMConf002CleanInactivity.class,
				MigratorMConf003CleanInactivity.class,
				MigratorFaction001Invitations.class
		);
		this.activate(getClassesActiveColls());
		this.activate(
				// Mixin
				PowerMixin.class,

				// Engines
				EngineCanCombatHappen.class,
				EngineChat.class,
				EngineChunkChange.class,
				EngineCleanInactivity.class,
				EngineDenyCommands.class,
				EngineEcon.class,
				EngineExploit.class,
				EngineFactionChat.class,
				EngineFlagEndergrief.class,
				EngineFlagExplosion.class,
				EngineFlagFireSpread.class,
				EngineFlagSpawn.class,
				EngineFlagZombiegrief.class,
				EngineFly.class,
				EngineFtop.class,
				EngineLastActivity.class,
				EngineMotd.class,
				EngineMoveChunk.class,
				EnginePermBuild.class,
				EnginePlayerData.class,
				EnginePower.class,
				EngineRaidtimer.class,
				EngineRoster.class,
				EngineSandbots.class,
				EngineSeeChunk.class,
				EngineShow.class,
				EngineSkull.class,
				EngineSpawnerchunk.class,
				EngineTeleportHomeOnDeath.class,
				EngineTerritoryShield.class,
				EngineVisualizations.class,
				EngineStats.class,
				EngineExtras.class,
				EngineShield.class,

				// Integrations
				IntegrationCoreProtect.class,
				IntegrationFKore.class,
				IntegrationMobExtras.class,
				IntegrationPolar.class,
				IntegrationSpigot.class,
				IntegrationSupplyDrop.class,
				IntegrationV18.class,
				IntegrationV19.class,
				IntegrationWorldGuard.class,
				IntegrationEssentials.class,

				// Command
				CmdFactions.class,

				// Tasks
				TaskAlarm.class,
				TaskClear.class,
				TaskEconLandReward.class,
				TaskFactionsFly.class,
				TaskFactionTopCalculate.class,
				TaskMissionTimer.class,
				TaskPlaceSand.class,
				TaskPlayerPowerUpdate.class,
				TaskRaidTimer.class,
				TaskResetKicks.class,
				TaskUpdateShield.class,
				TaskUpdateShieldGui.class,
				TaskUpdateShieldGuiSeconds.class,
				TaskUpdateBoard.class
		);

		this.activate(this.getClassesActive("chat", ChatActive.class));

		if(!MConf.get().enableSpawnerChunks) {
			EngineSpawnerchunk.get().setActive(false);
		}
		
		// Register Upgrades
		UpgradesManager.get().load();
		
		// Load missions
		MissionsManager.get().load();
		
		// Refresh FactionsTopDataColl
		this.refreshFactionValues();

		// Load corners
		EngineExtras.loadCorners();

		pluginEnableMillis = System.currentTimeMillis();

		// Respawn sandbots
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Faction faction : FactionColl.get().getAll()) {
					for (Sandbot sandbot : faction.getSandbots().values()) {
						if (sandbot == null) continue;
						if (sandbot.isDespawned()) continue;

						faction.spawnSandbot(sandbot, sandbot.getPs());
					}
				}
			}
		}.runTaskLater(this, 100L);
	}

	public Long getPluginEnableMillis() {
		return pluginEnableMillis;
	}

	private Long pluginEnableMillis = null;

	@Override
	public List<Class<?>> getClassesActiveColls()
	{
		// MConf should always be activated first for all plugins. It's simply a standard. The config should have no dependencies.
		// MFlag and MPerm are both dependency free.
		// Next we activate Faction, MPlayer and Board. The order is carefully chosen based on foreign keys and indexing direction.
		// MPlayer --> Faction
		// We actually only have an index that we maintain for the MPlayer --> Faction one.
		// The Board could currently be activated in any order but the current placement is an educated guess.
		// In the future we might want to find all chunks from the faction or something similar.
		// We also have the /f access system where the player can be granted specific access, possibly supporting the idea of such a reverse index.
		return new MassiveList<Class<?>>(
			MConfColl.class,
			MFlagColl.class,
			MPermColl.class,
			FactionColl.class,
			MPlayerColl.class,
			BoardColl.class,
			FactionTopValueColl.class,
			FactionTopDataColl.class,
			MPlayerStatsColl.class,
			FactionPermissionsColl.class,
			MissionUpgradeConfColl.class,
			NameReservesColl.class,
			SkullsColl.class,
			RaidDataStorageColl.class,
			FactionBreachsColl.class
		);
	}
	
	@Override
	public List<Class<?>> getClassesActiveEngines()
	{
		List<Class<?>> ret = super.getClassesActiveEngines();
		
		ret.remove(EngineEcon.class);
		ret.add(EngineEcon.class);
		ret.add(EngineExtras.class); // register extras engine
		ret.add(EngineStats.class); // register stats engine
		ret.add(EngineShield.class);
		
		return ret;
	}
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		return super.getGsonBuilder()
				   .registerTypeAdapter(TerritoryAccess.class, TerritoryAccessAdapter.get())
				   .registerTypeAdapter(Board.class, BoardAdapter.get())
				   .registerTypeAdapter(Board.MAP_TYPE, BoardMapAdapter.get())
				   .registerTypeAdapter(Rel.class, RelAdapter.get())
			;
	}
	
	private void refreshFactionValues()
	{
		log("Ensuring all factions are in the FactionTopDataColl...");
		for (Faction faction : FactionColl.get().getAll())
		{
			if (!FactionTopData.get().factionValues.containsKey(faction.getId()))
			{
				if (faction.isSystemFaction()) continue; // prevent system factions from being added
				FactionTopData.get().addFaction(faction.getId());
			}
		}
		log("All factions are in the FactionTopDataColl!");
	}
	
	@Override
	public void onDisable()
	{
		TaskUpdateBoard.get().invoke(System.currentTimeMillis());

		// Despawn sandbots
		for (Faction faction : FactionColl.get().getAll()) {
			for (Sandbot sandbot : faction.getSandbots().values()) {
				if (sandbot == null) continue;
				if (sandbot.isDespawned()) continue;

				UUID sandbotId = sandbot.getUniqueId();

				if (sandbotId == null) continue;

				NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(sandbot.getUniqueId());

				if (npc != null) {
					npc.despawn();
					npc.destroy();
				}
			}
		}
		
		Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory); // For Faction Chest
		
		// update playtime statistic before player leaves the server
		for (String uuid : EngineStats.get().loginTimes.keySet())
		{
			MPlayer mPlayer = MPlayer.get(uuid);
			MPlayerStats.get().getPlayerStats(mPlayer).setTimePlayed(MPlayerStats.get().getPlayerStats(mPlayer).getTimePlayed());
		}
		
		// remove banners
		for (Location loc : EngineExtras.get().removeBanners)
		{
			if (loc.getBlock().getType() == Material.WALL_BANNER || loc.getBlock().getType() == Material.STANDING_BANNER)
			{
				loc.getBlock().setType(Material.AIR);
				loc.getBlock().getState().update();
			}
		}

		super.onDisable();
	}
	
	@Override
	public boolean isVersionSynchronized()
	{
		return false;
	}
	
}
