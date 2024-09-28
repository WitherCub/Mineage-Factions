package com.massivecraft.factions.entity;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.engine.EngineChat;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.collections.BackstringSet;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.collections.WorldExceptionSet;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.command.editor.annotation.EditorType;
import com.massivecraft.massivecore.command.editor.annotation.EditorTypeInner;
import com.massivecraft.massivecore.command.type.TypeMillisDiff;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EditorName("config")
public class MConf extends Entity<MConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	private static MConf i;
	public static MConf get() { return i; }
	public static void set(MConf newI) { i = newI; }
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public MConf load(MConf that)
	{
		super.load(that);
		
		this.setShieldTimesChangeable(that.shieldTimesChangeable);
		this.setRosterEnabled(that.rosterEnabled);
		this.setRosterChangeable(that.rosterChangeable);
		this.setGracePeriod(that.gracePeriod);

		// Reactivate EngineChat if currently active.
		// This way some event listeners are registered with the correct priority based on the config.
		EngineChat engine = EngineChat.get();
		if (engine.isActive())
		{
			engine.setActive(false);
			engine.setActive(true);
		}
		
		return this;
	}
	
	// -------------------------------------------- //
	// VERSION
	// -------------------------------------------- //
	
	public int version = 3;
	
	// -------------------------------------------- //
	// COMMAND ALIASES
	// -------------------------------------------- //
	
	// Don't you want "f" as the base command alias? Simply change it here.
	public List<String> aliasesF = MUtil.list("f");
	
	// -------------------------------------------- //
	// WORLDS FEATURE ENABLED
	// -------------------------------------------- //
	
	// Use this blacklist/whitelist system to toggle features on a per world basis.
	// Do you only want claiming enabled on the one map called "Hurr"?
	// In such case set standard to false and add "Hurr" as an exeption to worldsClaimingEnabled.
	public WorldExceptionSet worldsClaimingEnabled = new WorldExceptionSet();
	public WorldExceptionSet worldsPowerLossEnabled = new WorldExceptionSet();
	public WorldExceptionSet worldsPowerGainEnabled = new WorldExceptionSet();
	
	public WorldExceptionSet worldsPvpRulesEnabled = new WorldExceptionSet();
	
	// -------------------------------------------- //
	// DERPY OVERRIDES
	// -------------------------------------------- //
	
	// Add player names here who should bypass all protections.
	// Should /not/ be used for admins. There is "/f adminmode" for that.
	// This is for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections.
	public Set<String> playersWhoBypassAllProtection = new MassiveSet<>();
	
	// -------------------------------------------- //
	// TASKS
	// -------------------------------------------- //
	
	// Define the time in minutes between certain Factions system tasks is ran.
	public double taskPlayerPowerUpdateMinutes = 1;
//	public double taskPlayerDataRemoveMinutes = 5;
	public double taskEconLandRewardMinutes = 20;
	
	// -------------------------------------------- //
	// REMOVE DATA
	// -------------------------------------------- //
	
	// Should players be kicked from their faction and their data erased when banned?
	public boolean removePlayerWhenBanned = false;
	
	// After how many milliseconds should players be automatically kicked from their faction?
	
	// The Default
	@EditorType(TypeMillisDiff.class)
	public long cleanInactivityToleranceMillis = 10 * TimeUnit.MILLIS_PER_DAY; // 10 days
	
	// Player Age Bonus
	@EditorTypeInner({TypeMillisDiff.class, TypeMillisDiff.class})
	public Map<Long, Long> cleanInactivityToleranceMillisPlayerAgeToBonus = MUtil.map(
		2 * TimeUnit.MILLIS_PER_WEEK, 10 * TimeUnit.MILLIS_PER_DAY  // +10 days after 2 weeks
	);
	
	// Faction Age Bonus
	@EditorTypeInner({TypeMillisDiff.class, TypeMillisDiff.class})
	public Map<Long, Long> cleanInactivityToleranceMillisFactionAgeToBonus = MUtil.map(
		4 * TimeUnit.MILLIS_PER_WEEK, 10 * TimeUnit.MILLIS_PER_DAY, // +10 days after 4 weeks
		2 * TimeUnit.MILLIS_PER_WEEK,  5 * TimeUnit.MILLIS_PER_DAY  // +5 days after 2 weeks
	);
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	// Which faction should new players be followers of?
	// "none" means Wilderness. Remember to specify the id, like "3defeec7-b3b1-48d9-82bb-2a8903df24e3" and not the name.
	public String defaultPlayerFactionId = Factions.ID_NONE;
	
	// What rank should new players joining a faction get?
	// If not RECRUIT then MEMBER might make sense.
	public Rel defaultPlayerRole = Rel.RECRUIT;
	
	// What power should the player start with?
	public double defaultPlayerPower = 50.0;
	
	// -------------------------------------------- //
	// MOTD
	// -------------------------------------------- //
	
	// During which event priority should the faction message of the day be displayed?
	// Choose between: LOWEST, LOW, NORMAL, HIGH, HIGHEST and MONITOR.
	// This setting only matters if "motdDelayTicks" is set to -1
	public EventPriority motdPriority = EventPriority.NORMAL;
	
	// How many ticks should we delay the faction message of the day with?
	// -1 means we don't delay at all. We display it at once.
	// 0 means it's deferred to the upcoming server tick.
	// 5 means we delay it yet another 5 ticks.
	public int motdDelayTicks = -1;

	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
	// What is the maximum player power?
	public double powerMax = 50.0;
	
	// What is the minimum player power?
	// NOTE: Negative minimum values is possible.
	public double powerMin = 0.0;
	
	// How much power should be regained per hour online on the server?
	public double powerPerHour = 50.0;
	
	// How much power should be lost on death?
	public double powerPerDeath = -4.0;
	
	// Can players with negative power leave their faction?
	// NOTE: This only makes sense to set to false if your "powerMin" setting is negative.
	public boolean canLeaveWithNegativePower = true;
	
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //

	// Is there a maximum amount of members per faction?
	// 0 means there is not. If you set it to 100 then there can at most be 100 members per faction.
	public int factionMemberLimit = 50;
	
	// Is there a maximum faction power cap?
	// 0 means there is not. Set it to a positive value in case you wan't to use this feature.
	public double factionPowerMax = 0.0;
	
	// Limit the length of faction names here.
	public int factionNameLengthMin = 3;
	public int factionNameLengthMax = 12;
	
	// Should faction names automatically be converted to upper case?
	// You probably don't want this feature.
	// It's a remnant from old faction versions.
	public boolean factionNameForceUpperCase = false;
	
	// -------------------------------------------- //
	// SET LIMITS
	// -------------------------------------------- //
	
	// When using radius setting of faction territory, what is the maximum radius allowed?
	public int setRadiusMax = 30;
	
	// When using fill setting of faction territory, what is the maximum chunk count allowed?
	public int setFillMax = 1000;
	
	// -------------------------------------------- //
	// CLAIMS
	// -------------------------------------------- //
	
	// Must claims be connected to each other?
	// If you set this to false you will allow factions to claim more than one base per world map.
	// That would makes outposts possible but also potentially ugly weird claims messing up your Dynmap and ingame experiance.
	public boolean claimsMustBeConnected = false;
	
	// Would you like to allow unconnected claims when conquering land from another faction?
	// Setting this to true would allow taking over someone elses base even if claims normally have to be connected.
	// Note that even without this you can pillage/unclaim another factions territory in war.
	// You just won't be able to take the land as your own.
	public boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	
	// Is claiming from other factions even allowed?
	// Set this to false to disable territorial warfare altogether.
	public boolean claimingFromOthersAllowed = true;
	
	// Is a minimum distance (measured in chunks) to other factions required?
	// 0 means the feature is disabled.
	// Set the feature to 10 and there must be 10 chunks of wilderness between factions.
	// Factions may optionally allow their allies to bypass this limit by configuring their faction permissions ingame themselves.
	public int claimMinimumChunksDistanceToOthers = 3;
	
	// Do you need a minimum amount of faction members to claim land?
	// 1 means just the faction leader alone is enough.
	public int claimsRequireMinFactionMembers = 1;
	
	// Is there a maximum limit to chunks claimed?
	// 0 means there isn't.
	public int claimedLandsMax = 4000;
	
	// The max amount of worlds in which a player can have claims in.
	public int claimedWorldsMax = -1;
	
	// The limit to how far away you can claim with the click to claim map
	public int claimChunkMaxDis = 28;
	
	// The limit to how many chunks you can claim in a line
	public int setLineMax = 20;
	
	// -------------------------------------------- //
	// PROTECTION
	// -------------------------------------------- //
	
	public boolean protectionLiquidFlowEnabled = true;
	
	// -------------------------------------------- //
	// HOMES/WARPS
	// -------------------------------------------- //
	
	// Is the home feature enabled?
	// If you set this to false players can't set homes or teleport home.
	public boolean homesEnabled = true;
	
	// Must homes be located inside the faction's territory?
	// It's usually a wise idea keeping this true.
	// Otherwise players can set their homes inside enemy territory.
	public boolean homesMustBeInClaimedTerritory = true;
	
	// Is the home teleport command available?
	// One reason you might set this to false is if you only want players going home on respawn after death.
	public boolean homesTeleportCommandEnabled = true;
	
	// These options can be used to limit rights to tp home under different circumstances.
	public boolean homesTeleportAllowedFromEnemyTerritory = true;
	public boolean homesTeleportAllowedFromDifferentWorld = true;
	public double homesTeleportAllowedEnemyDistance = 0.0;
	public boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	// Should players teleport to faction home on death?
	// Set this to true to override the default respawn location.
	public boolean homesTeleportToOnDeathActive = false;
	
	// This value can be used to tweak compatibility with other plugins altering the respawn location.
	// Choose between: LOWEST, LOW, NORMAL, HIGH, HIGHEST and MONITOR.
	public EventPriority homesTeleportToOnDeathPriority = EventPriority.NORMAL;
	
	// Do warps have to be in claimed territory?
	public boolean warpsMustBeInClaimedTerritory = true;
	
	// Warps configuration
	public int amountOfWarps = 5;
	public int costPerWarp = 50000;
	
	// Teleport warmup
	public int warpWarmup = 5;
	
	// Inspect logs
	public int coreprotectInspectLimit = 100;
	
	// -------------------------------------------- //
	// TERRITORY INFO
	// -------------------------------------------- //
	
	public boolean territoryInfoTitlesDefault = false;

	public String territoryInfoTitlesMain = "{relcolor}{name}";
	public String territoryInfoTitlesSub = "<i>{desc}";
	public int territoryInfoTitlesTicksIn = 5;
	public int territoryInfoTitlesTicksStay = 60;
	public int territoryInfoTitleTicksOut = 5;

	public String territoryInfoChat = "<i> ~ {relcolor}{name} <i>{desc}";
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	// Set this to true if want to block the promotion of new leaders for permanent factions.
	// I don't really understand the user case for this option.
	public boolean permanentFactionsDisableLeaderPromotion = false;
	
	// How much health damage should a player take upon placing or breaking a block in a "pain build" territory?
	// 2.0 means one heart.
	public double actionDeniedPainAmount = 2.0D;
	
	// If you set this option to true then factionless players cant partake in PVP.
	// It works in both directions. Meaning you must join a faction to hurt players and get hurt by players.
	public boolean disablePVPForFactionlessPlayers = false;
	
	// If you set this option to true then factionless players cant damage each other.
	// So two factionless can't PvP, but they can PvP with others if that is allowed.
	public boolean enablePVPBetweenFactionlessPlayers = true;
	
	// Set this option to true to create an exception to the rule above.
	// Players inside their own faction territory can then hurt facitonless players.
	// This way you may "evict" factionless trolls messing around in your home base.
	public boolean enablePVPAgainstFactionlessInAttackersLand = false;
	
	// Inside your own faction territory you take less damage.
	// 0.1 means that you take 10% less damage at home.
	public double territoryShieldFactor = 0.1D;
	
	// Protects the faction land from piston extending/retracting
	// through the denying of MPerm build
	public boolean handlePistonProtectionThroughDenyBuild = true;
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// A list of commands to block for members of permanent factions.
	// I don't really understand the user case for this option.
	public List<String> denyCommandsPermanentFactionMember = new ArrayList<>();

	// Lists of commands to deny depending on your relation to the current faction territory.
	// You may for example not type /home (might be the plugin Essentials) in the territory of your enemies.
	public Map<Rel, List<String>> denyCommandsTerritoryRelation = MUtil.map(
		Rel.ENEMY, MUtil.list(
			// Essentials commands
			"sethome",
			"createhome",
			"esethome",
			"ecreatehome",
			"essentials:sethome",
			"essentials:createhome"
		),
		Rel.NEUTRAL, MUtil.list(
			// Essentials commands
			"sethome",
			"createhome",
			"esethome",
			"ecreatehome",
			"essentials:sethome",
			"essentials:createhome"
		),
		Rel.TRUCE, MUtil.list(
			// Essentials commands
			"sethome",
			"createhome",
			"esethome",
			"ecreatehome",
			"essentials:sethome",
			"essentials:createhome"
		),
		Rel.ALLY, MUtil.list(
			// Essentials commands
			"sethome",
			"createhome",
			"esethome",
			"ecreatehome",
			"essentials:sethome",
			"essentials:createhome"
		),
		Rel.MEMBER, new ArrayList<String>()
	);
	
	// The distance for denying the following commands. Set to -1 to disable.
	public double denyCommandsDistance = -1;
	
	// Lists of commands to deny depending on your relation to a nearby enemy in the above distance.
	public Map<Rel, List<String>> denyCommandsDistanceRelation = MUtil.map(
		Rel.ENEMY, MUtil.list(
			"home"
		),
		Rel.NEUTRAL, new ArrayList<String>(),
		Rel.TRUCE, new ArrayList<String>(),
		Rel.ALLY, new ArrayList<String>(),
		Rel.MEMBER, new ArrayList<String>()
	);
	
	// Allow bypassing the above setting when in these territories.
	public List<Rel> denyCommandsDistanceBypassIn = MUtil.list(
		Rel.MEMBER,
		Rel.ALLY
	);
	
	// -------------------------------------------- //
	// CHAT
	// -------------------------------------------- //
	
	// Should Factions set the chat format?
	// This should be kept at false if you use an external chat format plugin.
	// If you are planning on running a more lightweight server you can set this to true.
	public boolean chatSetFormat = false;
	
	// At which event priority should the chat format be set in such case?
	// Choose between: LOWEST, LOW, NORMAL, HIGH and HIGHEST.
	public EventPriority chatSetFormatAt = EventPriority.LOWEST;
	
	// What format should be set?
	public String chatSetFormatTo = "<{factions_relcolor}§l{factions_roleprefix}§r{factions_relcolor}{factions_name|rp}§f%1$s> %2$s";
	
	// Should the chat tags such as {factions_name} be parsed?
	// NOTE: You can set this to true even with chatSetFormat = false.
	// But in such case you must set the chat format using an external chat format plugin.
	public boolean chatParseTags = true;
	
	// At which event priority should the faction chat tags be parsed in such case?
	// Choose between: LOWEST, LOW, NORMAL, HIGH, HIGHEST.
	public EventPriority chatParseTagsAt = EventPriority.LOW;
	
	// -------------------------------------------- //
	// FACTION CHAT
	// -------------------------------------------- //
	
	public static String factionChatFormat = "%s:" + ChatColor.WHITE + " %s";
	public static String allianceChatFormat = ChatColor.DARK_PURPLE + "%s:" + ChatColor.WHITE + " %s";
	public static String truceChatFormat = ChatColor.LIGHT_PURPLE + "%s:" + ChatColor.WHITE + " %s";
	
	// -------------------------------------------- //
	// COLORS
	// -------------------------------------------- //
	
	// Here you can alter the colors tied to certain faction relations and settings.
	// You probably don't want to edit these to much.
	// Doing so might confuse players that are used to Factions.
	public ChatColor colorMember = ChatColor.GREEN;
	public ChatColor colorAlly = ChatColor.DARK_PURPLE;
	public ChatColor colorTruce = ChatColor.LIGHT_PURPLE;
	public ChatColor colorNeutral = ChatColor.WHITE;
	public ChatColor colorEnemy = ChatColor.RED;
	
	// This one is for example applied to SafeZone since that faction has the pvp flag set to false.
	public ChatColor colorNoPVP = ChatColor.GOLD;
	
	// This one is for example applied to WarZone since that faction has the friendly fire flag set to true.
	public ChatColor colorFriendlyFire = ChatColor.DARK_RED;
	
	// Health bar settings
	public boolean disableInvisPotsForAdmins = false;
	public String disableInvisPotsBypassPermission = "factions.bypass.invispotcheck";
	
	// Faction ally, truce, neutral Limits
	public int truceLimit = 1;
	public int allyLimit = 1;
	public int neutralLimit = 1;
	
	// -------------------------------------------- //
	// PREFIXES
	// -------------------------------------------- //
	
	// Here you may edit the name prefixes associated with different faction ranks.
	public String prefixLeader = "@";
	public String prefixColeader = "**";
	public String prefixOfficer = "*";
	public String prefixMember = "+";
	public String prefixRecruit = "-";
	
	// -------------------------------------------- //
	// EXPLOITS
	// -------------------------------------------- //
	
	public boolean handleExploitObsidianGenerators = true;
	public boolean handleExploitEnderPearlClipping = true;
	public boolean handleExploitTNTWaterlog = false;
	public boolean handleNetherPortalTrap = true;
	
	// -------------------------------------------- //
	// SEE CHUNK
	// -------------------------------------------- //
	
	// These options can be used to tweak the "/f seechunk" particle effect.
	// They are fine as is but feel free to experiment with them if you want to.
	
	// Use 1 or multiple of 3, 4 or 5.
	public int seeChunkSteps = 1;
	
	// White/Black List for creating sparse patterns.
	public int seeChunkKeepEvery = 5;
	public int seeChunkSkipEvery = 0;
	
	@EditorType(TypeMillisDiff.class)
	public long seeChunkPeriodMillis = 500;
	public int seeChunkParticleAmount = 30;
	public float seeChunkParticleOffsetY = 2;
	public float seeChunkParticleDeltaY = 2;
	
	// -------------------------------------------- //
	// UNSTUCK
	// -------------------------------------------- //
	
	public int unstuckSeconds = 30;
	public int unstuckChunkRadius = 10;
	
	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	
	// Here you can disable logging of certain events to the server console.
	
	public boolean logFactionCreate = true;
	public boolean logFactionDisband = true;
	public boolean logFactionJoin = true;
	public boolean logFactionKick = true;
	public boolean logFactionLeave = true;
	public boolean logLandClaims = true;
	public boolean logMoneyTransactions = true;
	public boolean logFactionReserve = true;
	
	// -------------------------------------------- //
	// ENUMERATIONS
	// -------------------------------------------- //
	// In this configuration section you can add support for Forge mods that add new Materials and EntityTypes.
	// This way they can be protected in Faction territory.
	// Use the "UPPER_CASE_NAME" for the Material or EntityType in question.
	// If you are running a regular Spigot server you don't have to edit this section.
	// In fact all of these sets can be empty on regular Spigot servers without any risk.
	
	// Interacting with these materials when they are already placed in the terrain results in an edit.
	public BackstringSet<Material> materialsEditOnInteract = new BackstringSet<>(Material.class);
	
	// Interacting with the the terrain holding this item in hand results in an edit.
	// There's no need to add all block materials here. Only special items other than blocks.
	public BackstringSet<Material> materialsEditTools = new BackstringSet<>(Material.class);
	
	// Interacting with these materials placed in the terrain results in door toggling.
	public BackstringSet<Material> materialsDoor = new BackstringSet<>(Material.class);
	
	// Interacting with these materials placed in the terrain results in opening a container.
	public BackstringSet<Material> materialsContainer = new BackstringSet<>(Material.class);
	
	// Interacting with these entities results in an edit.
	public BackstringSet<EntityType> entityTypesEditOnInteract = new BackstringSet<>(EntityType.class);
	
	// Damaging these entities results in an edit.
	public BackstringSet<EntityType> entityTypesEditOnDamage = new BackstringSet<>(EntityType.class);
	
	// Interacting with these entities results in opening a container.
	public BackstringSet<EntityType> entityTypesContainer = new BackstringSet<>(EntityType.class);
	
	// The complete list of entities considered to be monsters.
	public BackstringSet<EntityType> entityTypesMonsters = new BackstringSet<>(EntityType.class);
	
	// List of entities considered to be animals.
	public BackstringSet<EntityType> entityTypesAnimals = new BackstringSet<>(EntityType.class);
	
	// -------------------------------------------- //
	// INTEGRATION: HeroChat
	// -------------------------------------------- //
	
	// I you are using the chat plugin HeroChat Factions ship with built in integration.
	// The two channels Faction and Allies will be created.
	// Their data is actually stored right here in the factions config.
	// NOTE: HeroChat will create it's own database files for these two channels.
	// You should ignore those and edit the channel settings from here.
	// Those HeroChat channel database files aren't read for the Faction and Allies channels.
	
	// The Faction Channel
	public String herochatFactionName = "Faction";
	public String herochatFactionNick = "F";
	public String herochatFactionFormat = "{color}[&l{nick}&r{color} &l{factions_roleprefix}&r{color}{factions_title|rp}{sender}{color}] &f{msg}";
	public ChatColor herochatFactionColor = ChatColor.GREEN;
	public int herochatFactionDistance = 0;
	public boolean herochatFactionIsShortcutAllowed = false;
	public boolean herochatFactionCrossWorld = true;
	public boolean herochatFactionMuted = false;
	public Set<String> herochatFactionWorlds = new HashSet<>();
	
	// The Allies Channel
	public String herochatAlliesName = "Allies";
	public String herochatAlliesNick = "A";
	public String herochatAlliesFormat = "{color}[&l{nick}&r&f {factions_relcolor}&l{factions_roleprefix}&r{factions_relcolor}{factions_name|rp}{sender}{color}] &f{msg}";
	public ChatColor herochatAlliesColor = ChatColor.DARK_PURPLE;
	public int herochatAlliesDistance = 0;
	public boolean herochatAlliesIsShortcutAllowed = false;
	public boolean herochatAlliesCrossWorld = true;
	public boolean herochatAlliesMuted = false;
	public Set<String> herochatAlliesWorlds = new HashSet<>();
	
	// -------------------------------------------- //
	// INTEGRATION: LWC
	// -------------------------------------------- //
	
	// Do you need faction build rights in the territory to create an LWC protection there?
	public boolean lwcMustHaveBuildRightsToCreate = true;
	
	// The config option above does not handle situations where a player creates an LWC protection in Faction territory and then leaves the faction.
	// The player would then have an LWC protection in a territory where they can not build.
	// Set this config option to true to enable an automatic removal feature.
	// LWC protections that couldn't be created will be removed on an attempt to open them by any player.
	public boolean lwcRemoveIfNoBuildRights = false;
	
	// WARN: Experimental and semi buggy.
	// If you change this to true: alien LWC protections will be removed upon using /f set.
	public Map<EventFactionsChunkChangeType, Boolean> lwcRemoveOnChange = MUtil.map(
		EventFactionsChunkChangeType.BUY, false, // when claiming from wilderness
		EventFactionsChunkChangeType.SELL, false, // when selling back to wilderness
		EventFactionsChunkChangeType.CONQUER, false, // when claiming from another player faction
		EventFactionsChunkChangeType.PILLAGE, false // when unclaiming (to wilderness) from another player faction
	);
	
	// -------------------------------------------- //
	// INTEGRATION: WorldGuard
	// -------------------------------------------- //
	
	// Global WorldGuard Integration Switch
	public boolean worldguardCheckEnabled = false;
	
	// Enable the WorldGuard check per-world
	// Specify which worlds the WorldGuard Check can be used in
	public WorldExceptionSet worldguardCheckWorldsEnabled = new WorldExceptionSet();
	
	// -------------------------------------------- //
	// INTEGRATION: ECONOMY
	// -------------------------------------------- //
	
	// Should economy features be enabled?
	// This requires that you have the external plugin called "Vault" installed.
	public boolean econEnabled = true;
	
	// A money reward per chunk. This reward is divided among the players in the faction.
	// You set the time inbetween each reward almost at the top of this config file. (taskEconLandRewardMinutes)
	public double econLandReward = 0.00;
	
	// When paying a cost you may specify an account that should receive the money here.
	// Per default "" the money is just destroyed.
	public String econUniverseAccount = "";
	
	// What is the price per chunk when using /f set?
	public Map<EventFactionsChunkChangeType, Double> econChunkCost = MUtil.map(
		EventFactionsChunkChangeType.BUY, 0.0, // when claiming from wilderness
		EventFactionsChunkChangeType.SELL, 0.0, // when selling back to wilderness
		EventFactionsChunkChangeType.CONQUER, 0.0, // when claiming from another player faction
		EventFactionsChunkChangeType.PILLAGE, 0.0 // when unclaiming (to wilderness) from another player faction
	);
	
	// What is the price to create a faction?
	public double econCostCreate = 0.0;
	
	// And so on and so forth ... you get the idea.
	public double econCostSethome = 0.0;
	public double econCostJoin = 0.0;
	public double econCostLeave = 0.0;
	public double econCostKick = 0.0;
	public double econCostInvite = 0.0;
	public double econCostDeinvite = 0.0;
	public double econCostHome = 0.0;
	public double econCostName = 0.0;
	public double econCostDescription = 0.0;
	public double econCostTitle = 0.0;
	public double econCostFlag = 0.0;
	
	public Map<Rel, Double> econRelCost = MUtil.map(
		Rel.ENEMY, 0.0,
		Rel.ALLY, 0.0,
		Rel.TRUCE, 0.0,
		Rel.NEUTRAL, 0.0
	);
	
	// Should the faction bank system be enabled?
	// This enables the command /f money.
	public boolean bankEnabled = true;
	
	// That costs should the faciton bank take care of?
	// If you set this to false the player executing the command will pay instead.
	public boolean bankFactionPaysCosts = true;
	
	// -------------------------------------------- //
	// FLY
	// -------------------------------------------- //
	
	public boolean factionsFlyEnabled = true;
	public int factionsFlyXZDistanceCheck = 60;
	public int factionsFlyYDistanceCheck = 60;
	public int factionsFlyMaxHeight = 280;
	public boolean factionsFlyCanUsePearls = false;
	public String factionsFlyNoPearlsMsg = "&cYou cannot use enderpearls while flying.";
	public String factionsFlyDisabledMaxHeightMessage = "&cYour flight has been disabled. You have reached the maximum fly height.";
	public String factionsFlyDisabledEnemyNearbyMessage = "&cYour flight has been disabled. You are in enemy land or near an enemy player!";
	public String factionsFlyDisabledNoPerms = "&cYour flight has been disabled. You do not have permission to fly here.";
	public String factionsFlyEnabledMessage = "&aYour flight has been enabled. You have entered land where you are permitted to fly.";
	public String factionsFlyOverridePerm = "factions.fly.override";
	
	public String alreadyInStealthMsg = "&cYou are already in stealth mode.";
	public String stealthNowOnMsg = "&eStealth Mode is now &aON&e. You will no longer prevent enemies from flying.";
	public String alreadyOutOfStealthMsg = "&cYou are already out of stealth mode.";
	public String stealthNowOffMsg = "&eStealth Mode is now &cOFF&e. You will now prevent enemies from flying.";
	
	public String flyNowOnMsg = "&eYou have toggled flight &aON&e. You will now automatically enter fly.";
	public String alreadyOutOfFlyMsg = "&cYou already have fly disabled.";
	public String flyNowOffMsg = "&eYou have toggled flight &cOFF&e. You will no longer automatically enter fly.";
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public String playerQuitMessage = "&8[&c-&8] %PLAYER% &7has left the game!";
	public String playerJoinMessage = "&8[&a+&8] %PLAYER% &7has joined the game!";
	
	public String homePatchMessage = "&cYou are not allowed to teleport to your home in territory that you don't have access to located at %LOC%.";
	
	public long removePlayerMillisFromLand = 900000L;
	
	public int costForBanner = 100;
	public int bannerPlaceCooldownSeconds = 15;
	public int timeTillAssistTimeout = 60;
	
	public Integer maximumFillRadius = 40;
	public String reachedMaximumFillRadius = "&cYou have reached the max radius of 40.";
	public String cantFillLessThanOne = "&cYou can't tntfill less than 1 tnt!";
	
	public boolean disableInvisPots = true;
	public String removeInvisEffectMsg = "&cThe invisibility potion effect is disabled on this server.";
	
	public int altLimit = 40;
	public boolean hideOtherFacAlts = false;
	public List<String> materialsAltsCanBuild = MUtil.list("SAND", "GRAVEL");
	
	// Missions
	public String missionAssignedMsg = "&7The &6%mission% &7mission is now active! You have &61 day &7to complete it.";
	public String completeMissionMsg = "&a&lCongratulations! &7Your faction has completed the &6%mission% &7mission and has been rewarded %rewardAmount% credits.";
	public List<String> missionsGuiMissionLore = MUtil.list("", "&7&lChallenge", "&8&l* &7%missionDescription%", "", "&7&lWorth", " &8&l* &7%creditsReward% Credits", "", "&7&lTime Remaining:", " &8&l* &7%timeRemaining%", "", "&7Complete: %requirementComplete%/%requirement%");
	public int missionSpinTimes = 10;
	public long missionSpinSpeed = 6L;
	
	// -------------------------------------------- //
	// FTOP
	// -------------------------------------------- //
	
	public boolean enableFactionsTop = true;
	
	public String ftopRecalculatePerm = "factionstop.recalculate";
	
	public int taskFactionTopCalculateMillis = 60000;
	public int autoRecalculateMillis = 3600000;

	public int ftopChunksToCalculatePerSecond = 500;
	public boolean ftopOnlyCalculateSpawnerChunks = true;
	
	public String ftopAlreadyRecalculatingMsg = "&cThe server is currently recalculating!";
	public String ftopRecalculateNoPermMsg = "&cYou do not have permission to execute this command!";
	public String ftopTotalsResynchronizeStartMsg = "&eBeginning to recalculate faction top totals...";
	public List<String> ftopTotalsResynchronizedMsg = MUtil.list("&eFaction top totals have been resynchronized.");
	public String ftopCommandLastUpdateFooterMsg = "&eLast updated &6%time%&eago";

	public String cmdFtopTotalPrefix = " &6Total: &7$%total%";
	public String cmdFtopAmountOnlinePrefix = " &6%online%/%total% online";
	public String ftopPagerName = "&6Top wealth";
	public String ptopPagerName = "&6Potential wealth";
	public List<String> factionsTopLoreFormat = MUtil.list(
			"&f&l&nF Top Summary for %faction%",
			" ",
			"&fPosition: &6#%position%",
			" ",
			"&fTotal 24 hour change: &a%twentyFourHourChangeValue% %twentyFourHourChangePercent%",
			"&rCurrent Worth: &6%currentWorth%",
			"&rPotential Value: &6%potentialWorth%",
			" ",
			"&f&l&nPlaced Spawner Values",
			" ",
			"%placedSpawnerValues%",
			" ",
			"&fLast updated %updatedAgoTime% ago"
	);
	public String factionsTopGuiLoreSpawnerValueEntry = "&6%spawnerType%: &7%value% &7(&r%amount%&7)";
	public String factionsTopGuiLoreSpawnerValuesEmpty = "&fNone";
	
	public Map<EntityType, String> entityTypeDisplayNamesMap = MUtil.map(EntityType.PIG_ZOMBIE, "Zombie Pigman", EntityType.CAVE_SPIDER, "Cave Spider", EntityType.MAGMA_CUBE, "Magma Cube", EntityType.ENDER_DRAGON, "Ender Dragon", EntityType.MUSHROOM_COW, "Mooshroom", EntityType.SNOWMAN, "Snow Golem", EntityType.OCELOT, "Ocelot", EntityType.IRON_GOLEM, "Iron Golem", EntityType.HORSE, "Horse", EntityType.WITHER, "Wither");
	
	public boolean ftopKnockMsgEnabled = true;
	public List<String> ftopKnockMsg = MUtil.list(" ", "&e{newfaction} &ehas just passed {oldfaction} &efor ftop rank {rank}!", " ");
	public int ftopKnockTopXPositions = 3;
	
	public double spawnerInitalValue = 0.10;
	public double gainFullValueAfterPlaceForXSeconds = 259200;
	
	// -------------------------------------------- //
	// FACTION ALARM
	// -------------------------------------------- //
	
	public Material notificationTimeMaterial = Material.WATCH;
	public String notificationTimeName = "&e&lNotification Time";
	public List<String> notificationTimeLore = MUtil.list("&rCurrent Setting: &6%currentNotificationTime%", "", "&7This setting controls the time it takes", "&7for a notification to be sent if", "&7the walls are not marked as clear", "&7with /f clear", "", "&7If notifications are disabled", "&7then so is the /clear command");
	
	public Material permsFclearMaterial = Material.BLAZE_ROD;
	public String permsFclearName = "&e&lPerms for /f clear";
	
	public Material alarmSoundsOnMaterial = Material.WOOL;
	public Integer alarmSoundsOnData = 5;
	public Material alarmSoundsOffMaterial = Material.WOOL;
	public Integer alarmSoundsOffData = 14;
	
	public String alarmSoundsName = "&e&lAlarm Sounds";
	public List<String> alarmSoundsLore = MUtil.list("&rCurrent Setting: &6%currentAlarmSounds%", "", "&7This setting is for you only", "&7and will toggle the alarm sounds", "&7when someone types /f alarm");
	
	public Long alarmDelayMillis = 5000L;
	public String alarmSound = "NOTE_BASS";
	public float alarmVolume = 1.0f;
	public float alarmPitch = 1.0f;
	
	public String alarmSoundedMsg = "&cALERT: &a%player% &ehas sounded the alarm! Get to the walls!";
	public String alarmDisabledMsg = "&cALERT: &eThe alarm has been disabled by &a%player%.";
	
	public Long checkWallReminderMillis = 15000L;
	public String checkWallClearMsg = "&aYou have marked the walls as clear!";
	public String checkWallNotifsDisabled = "&cCheck notifications must be enabled in /f check for this command to function.";
	public String checkWallsNotifMsg = "&cALERT: &eThe walls have not been checked for over %notificationTime% minutes.";
	
	public Material pageableGuiNextPageIconMaterial = Material.STAINED_GLASS_PANE;
	public Integer pageableGuiNextPageIconDurability = 14;
	public String pageableGuiNextPageIconName = "&7Next Page";
	
	public Material pageableGuiPreviousPageIconMaterial = Material.STAINED_GLASS_PANE;
	public Integer pageableGuiPreviousPageIconDurability = 14;
	public String pageableGuiPreviousPageIconName = "&7Previous Page";
	
	public boolean enableSetPaypal = true;
	public int minFactionPaypalLength = 3;
	public int maxFactionPaypalLength = 24;
	public String factionPaypalInvalidLengthMsg = "&cYour faction paypal name must be between %minCharacters% and %maxCharacters% characters long!";
	public String notValidPaypalMsg = "&cThat is not a valid paypal email!";
	public String paypalUpdatedPlayerMsg = "&eYour faction's paypal email has been updated to %paypal%";
	public String paypalUpdatedFactionMsg = "&e%player% has just updated your faction's paypal.";
	public String checkFactionPaypalMsg = "&e%factionName%'s Paypal: %paypal%";
	
	public int minFactionDiscordLength = 3;
	public int maxFactionDiscordLength = 24;
	public String factionDiscordInvalidLengthMsg = "&cYour faction discord url must be between %minCharacters% and %maxCharacters% characters long!";
	public String discordUrlRemoved = "&eYour Discord URL has been removed.";
	public String discordUrlUpdated = "&eYour Discord URL has been updated.";
	public String notValidDiscordMsg = "&cThat is not a valid discord URL!";
	
	public String warpGuiPanelWarpName = "&c%warpName%";
	public List<String> warpGuiPanelFormat = MUtil.list(" ", "&7World &8» &c%world%", "&7X &8» &c%x%", "&7Y &8» &c%y%", "&7Z &8» &c%z%", " ", "&7Password &8» %password%", " ", "&7Click to warp.");
	
	public String tntStickName = "&eTNT Fill Tool";
	public int tntStickPoints = 50;
	
	public String drainAlreadyDisallowedMsg = "&eYou already have drain &cDISALLOWED";
	public String drainAlreadyAllowedMsg = "&eYou already have drain &eALLOWED";
	public String drainStatusAllowedMsg = "&a%player% &ehas changed their drain status to &aALLOWED";
	public String drainStatusDisallowedMsg = "&a%player% &ehas changed their drain status to &cDISALLOWED";
	public String drainingSetAllowedInformMsg = "&aYou have set draining to ALLOWED. &7This means your faction leaders can drain your personal /balance at any time.";
	public String drainingSetDisallowedInformMsg = "&cYou have set draining to DISALLOWED. &7This means your personal balance will not be included in faction drains.";
	public String currentDrainStatusAllowedMsg = "&fCurrent drain status: &aALLOWED";
	public String currentDrainStatusDisallowedMsg = "&fCurrent drain status: &cDISALLOWED";
	public String lastDrainTime = "&fLast drain for %factionName%: &e%timeAgo%";
	public String lastDrainAmount = "&fLast drain amount taken from you: &b$%amount%";
	public String initiateDrainMsg = "&7&oUse &7&n&o/f drain <amount to drain>&7&o to initiate a faction drain";
	public int minimumDrainAmount = 50000;
	public String drainAmountLowerThanMinimumMsg = "&cThe smallest drain-to balance you can set is $%amount%";
	public String drainedMsg = "&aSuccessfully drained %membersDrained% Faction members for a total of $%totalDrained%";
	
	public String factionNameReservedMsg = "&cSorry, the faction named %faction% is currently reserved for %player%.";
	
	public String alreadyEnabledLoginNotifsMsg = "&cYou already have login notifications enabled.";
	public String loginNotifsNowOnMsg = "&eLogin notifications are now &aENABLED&e. You will now be notified when your faction members login.";
	public String alreadyDisabledLoginNotifsMsg = "&cYou already have login notifications disabled.";
	public String loginNotifsOffMsg = "&eLogin notifications are now &cDISABLED&e. You will no longer be notified when your faction members login.";
	
	public String shieldMangerGuiTitle = "&c&lShield Manager";
	
	public List<String> shieldManagerGuiLoreCurrently = MUtil.list(
		"",
		"&aYour shielded hours are currently",
		"&e%startHour% &f---> &e%endHour% &7(&e8 hours total&7)",
		"",
		"&fCurrent time:",
		"&e%currentTime%"
	);
	
	public List<String> shieldManagerGuiLoreChangeTo = MUtil.list(
		"",
		"&aYour shielded hours are currently",
		"&aset to change to",
		"&e%startHour% &f---> &e%endHour% &7(&e8 hours total&7)",
		"",
		"&fCurrent time:",
		"&e%currentTime%"
	);
	
	public List<String> shieldManagerGuiLoreChange = MUtil.list(
		"",
		"&aClick to change the Shielded hours to",
		"&e%startHour% &f---> &e%endHour% &7(&e8 hours total&7)",
		"",
		"&fCurrent time:",
		"&e%currentTime%"
	);
	
	public String timeZone = "America/New_York";
	
	public Map<Integer, Integer> shieldStartEndHours = MUtil.map(
		0, 8,
		1, 9,
		2, 10,
		3, 11,
		4, 12,
		5, 13,
		6, 14,
		7, 15,
		8, 16,
		9, 17,
		10, 18,
		11, 19,
		12, 20,
		13, 21,
		14, 22,
		15, 23,
		16, 24,
		17, 0,
		18, 1,
		19, 2,
		20, 3,
		21, 4,
		22, 5,
		23, 6,
		24, 7
	);
	
	public int shieldLengthHrs = 8;
	public String shieldInformationGuiName = "&e&lShield Information";
	public List<String> shieldInformationGuiLore = MUtil.list("", "&fDuring Shield, a faction cannot", "&fbe raided by cannons, nor raid others", "", "&fChanging the Shield time period takes", "&e1 day &fto update", "", "&cAbuse of this mechanic in any way", "&cwill be punished severely");
	public String disableShieldGuiItemName = "&c&lDisable Shield";
	public List<String> disableShieldGuiItemLore = MUtil.list("", "&fYou will need to wait &e1 day", "&fto reactivate it");
	public String shieldedHoursDisabledMsg = "%player% &ehas disabled %faction%&e's shielded hours.";
	public String changingShieldHoursOffMsg = "&cChanging shielded hours is currently disabled.";
	public String shieldHoursChangedMsg = "%player% &ehas set %faction%&e's shielded hours to &7%startHour% &f---> &7%endHour%";
	public String pendingChangeGuiName = "&b&lPending Shield Change";
	public List<String> pendingChangeGuiLoreNoChange = MUtil.list("", "&cThere is no pending change to", "&cyour faction's Shield time period");
	public List<String> pendingChangeGuiLoreChangeAfterDisable = MUtil.list("", "&cYour faction has recently disabled their", "&cshield. You can reselect a time period in", "&c%time%");
	public List<String> pendingChangeGuiLoreChangeActive = MUtil.list("", "&fYour faction's Shield time period", "&fwill be updated to &e%startHour% &f---> &e%endHour%", "&fin &e%time%");
	public long shieldHoursChangeTimeBeforeUpdate = 86400000;
	public String shieldHoursRequestedChangeMsg = "%player% &ehas requested to change %faction%&e's shielded hours to &7%startHour% &f---> &7%endHour%";
	public String shieldHoursUpdatedMsg = "&Your faction&e's shielded hours have been updated to &7%startHour% &f---> &7%endHour%";
	public String mustWaitBeforeChangingShieldedHoursFromDisableMsg = "&cYour faction has recently disabled your shield. You must wait %time% to reconfigure your shield again!";
	public String shieldAlreadyDisabledMsg = "&cYour faction has already recently disabled your shield. You must wait %time% to reconfigure your shield again!";
	public String shieldNotEnabledMsg = "&cYour faction already has their shield disabled.";

	public String onlySetSpawnerchunkOwnTerritoryMsg = "&cYou can only set spawner chunks in your own territory!";
	public List<String> placeSpawnerToMarkMsg = MUtil.list("&ePlace a spawner in this chunk within the next minute to validate this spawner chunk.", "&7&oIf another Faction manages to breach within %spawnerChunkLockdownRadius% chunks of any Spawner Chunk then your Faction will be placed on Lockdown until the raid completes.");
	public String chunkAlreadySpawnerchunkMsg = "&cThis chunk is already marked as a spawner chunk!";
	//	public String mustBeNextToSpawnerchunkMsg = "&cYou can only set spawner chunks next to existing spawner chunks.";
	public String youCanOnlyPlaceSpawnersInSpawnerchunksMsg = "&cYou can only place spawners in spawner chunks. Use &d/f spawnerchunk &cto mark this chunk as a spawner chunk.";
	public String cantPlaceSpawnersInWild = "&cYou are not permitted to place spawners in wilderness.";
	public boolean enableSpawnerChunks = false;
	public int spawnerChunkRequestExpireAfterXSeconds = 60;
	public String spawnerChunkMarkedAt = "&a%player% has marked a spawner chunk for your faction at &6Chunk X: &7%chunkX% &6ChunkZ: &7%chunkZ% &ain &7%world%&a.";
	public String pleaseWaitXSecondsToUseSpawnerchunkCmdAgainMsg = "&cPlease wait %seconds% before using this command again.";
	public int spawnerChunkCommandUseCooldownXSeconds = 5;
	public String cannotUnmarkSpawnerChunkWithSpawnersInChunkMsg = "&cThere are still &c&l%spawnersInChunk% spawners left in this chunk. Please remove all spawners before attempting to unset a spawner chunk.";
	public String unmarkedSpawnerchunkMsg = "&aYou have successfully unmarked this chunk from being a spawner chunk.";
	public int spawnerChunkLockdownRadius = 25;

	public long breachCooldown = 28800000;
	public List<String> msgFactionBreached = MUtil.list(
			" ",
			"&c[BREACH] %factionRaiding% &ehas just breached %factionRaided% &eand has gained &a%amount% &evalue.",
			" "
	);

	public int stainedGlassGUIDurability = 7;
	
	private boolean shieldTimesChangeable = true;
	
	public boolean isShieldTimesChangeable()
	{
		return shieldTimesChangeable;
	}
	
	public void setShieldTimesChangeable(boolean shieldTimesChangeable)
	{
		this.shieldTimesChangeable = shieldTimesChangeable;
		this.changed();
	}
	
	public long missionTimerHours = 10800000;

	public boolean allowRaidingWhileOnShield = true;

	public boolean enableStrikes = true;
	public String addedStrikeMsg = "&aYou have added a faction strike to %faction%.";
	public String removeStrikeMsg = "&aYou have removed a faction strike from %faction% at their /f strikes index of %index%.";
	public String broadcastStrikeMsg = "&c&l[!] &c%faction% &7has been striked for &c%reason%";
	public String strikePagerName = "&6Strikes for %faction%";
	public boolean broadcastStrike = true;

	public int phaseOneLastsXMinutes = 15;
	public int phaseTwoLastsXMinutes = 15;
	public int phaseThreeLastsXMinutes = 30;
	public String startOfPhaseThreeMsg = "&aThis is the start of Phase III. Your faction is now on grace for the next 30 minutes.";
	public String yourFactionHasNoActiveRaidMsg = "&aYour faction &eis currently not raiding anybody.";
	public String cmdRaidTimeRemainingMsg = "&aYour faction's &eraid on &c%otherFaction% &ehas &a%time% &eremaining. &7&l(&7Phase %phase%&7&l)";
	public boolean allowAltsToCheckRaidtimerStatus = false;
	public String altsCantCheckRaidtimerStatusMsg = "&cFaction alts are not permitted to check raidtimer status.";
	public String raidHasEndedMsg = "&cYour raid on &c&l%faction% &chas ended as time has expired.";
	public String raidHasEndedDisbandMsg = "&cYour raid on &c&l%faction% &chas ended as they have disbanded.";
	public int onlyMessageAboutRaidTimerEveryXMinutes = 3;
	public String alreadyBeingRaidedMsg = "&cThis faction is already being raided by %faction%!";
	public String raidStartedMsg = "&aYour faction &ehas started a raid on &c%faction%&e!";
	public String cantMineSpawnersDuringRaidtimerMsg = "&cYou are not permitted to mine spawners while you are on lockdown.";
	public String cantTogglePrinterLockdownMsg = "&cYou are not permitted to toggle printer during Phase 1 of lockdown.";
	public String cantPlaceGenbucketLockdownMsg = "&cYou are not permitted to place genbuckets during Phase 1 of lockdown.";
	public String cantPlaceExplosionMsg = "&cYou are not permitted to use explosions during lockdown.";
	public String backToPhaseOneMsg = "&aYour faction &ehas restarted the raid timer, back to Phase 1 of your raid on &c%faction%&e!";
	public String endOfPhaseOneMsg = "&aThis is the end of Phase I. You are now permitted to toggle printer & place genbuckets.";
	public String endOfPhaseOneFactionRaidingMsg = "&aThis is the end of Phase I. %faction% is now permitted to toggle printer & place genbuckets.";
	public int raidTimerStartMinY = 40;

	public boolean rosterEnabled = true;

	public void setRosterEnabled(boolean toggle) {
		this.rosterEnabled = toggle;
		this.changed();
	}

	public int maxRosterSize = 15;
	public boolean unlimitedRosterKicks = false;
	public int defaultRosterKicks = 5;
	public boolean resetRosterKicksDailyAtMidnight = true;
	public String rosterKicksResetMsg = "&aYour faction's roster kicks have been reset!";
	public String rosterBypassJoinPermission = "roster.bypass";
	public Set<Rel> acceptableDefaultRankFactionRelations = MUtil.set(Rel.RECRUIT, Rel.MEMBER, Rel.OFFICER, Rel.COLEADER);
	public String notValidDefaultRankMsg = "&cThat is not a valid default rank.";
	public String systemFactionMsg = "&cYou can not modify the roster for a system faction.";
	public String notPermittedToModifyRosterMsg = "&cYour faction does not allow you to modify the roster.";
	public String playerAlreadyInRosterMsg = "&cThe specified player is already added to your faction's roster.";
	public String factionRosterFullMsg = "&cYour faction's roster is currently full.";
	public String playerAddedToRosterMsg = "&a%player% &7has been added to your faction roster.";
	public Set<Rel> permittedRanksToModifyRoster = MUtil.set(Rel.LEADER, Rel.COLEADER);
	public String modificationsToRosterNotEnabledMsg = "&cModifications to faction rosters have already been disabled.";
	public String playerNotInRosterMsg = "&cThe specified player is not currently on your faction's roster.";
	public String playerDefaultRankSetMsg = "&c%player% &7now has their default join rank set to %rel%.";
	public String notOnFactionRosterMsg = "&cYou are not on %faction%'s &croster.";
	public String playerRemovedFromRosterMsg = "&a%player% &7has been removed from your faction roster.";
	public String noRosterKicksRemainingMsg = "&cYour faction has no remaining roster kicks.";
	public String cantKickYourselfMsg = "&cYou can not kick yourself!";
	public String cantKickHigherRankMsg = "&cYou can not kick someone of a higher rank than you!";

	public String factionRosterGuiTitle = "&4&lFaction Roster";
	public int factionRosterGuiStainedGlassBorderColorId = 14;
	public String factionRosterGuiMemberNameEntry = "%playerDesc%";
	public String factionRosterGuiNotMemberNameEntry = "%playerDesc% &7(&fnot member&7)";
	public List<String> factionRosterGuiLore = MUtil.list(
			"&fJoins as: &7a %rel% in your faction",
			"&eClick to kick from roster",
			" ",
			"&7Roster kicks remaining: &e%kicks%",
			" ",
			"&cNote: &fIf this player is currently",
			"&fin the faction, you must also have",
			"&fthe kick perm so they can be kicked",
			"&fas well"
	);
	public String factionRosterGuiLoreUnlimitedKicksPlaceholderRet = "Unlimited";

	public int factionRosterGuiNextPageSlot = 51;
	public int factionRosterGuiPreviousPageSlot = 47;
	public String factionRosterGuiNextPageName = "&eNext Page";
	public String factionRosterGuiPreviousPageName = "&ePrevious Page";
	public String factionRosterGuiNextPageButtonTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";
	public String factionRosterGuiPreviousPageButtonTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";

	private boolean rosterChangeable = true;

	public boolean isRosterChangeable() {
		return rosterChangeable;
	}

	public void setRosterChangeable(boolean rosterChangeable) {
		this.rosterChangeable = rosterChangeable;
		this.changed();
	}


	public int factionBufferSize = 20;
	public String cannotClaimCornerMsg = "&cYou cannot claim this corner.";
	public String attemptingClaimCornerMsg = "&aAttempting to claim corner...";
	public String mustBeInCornerMsg = "&cYou must be in a corner to use this command.";
	public String couldNotClaimCornerMsg = "&7One or more &cclaims in this corner &7could not be claimed! Total chunks claimed: &c%claims%";
	public String claimedCornerMsg = "&aYou have claimed the corner successfully. &d%claims% &achunks have been claimed.";
	public List<String> worldsToShowInCornerlist = MUtil.list("world");
	public boolean showFactionOwnerInCornerlist = true;

	public List<String> tntAllowedWorldsDuringGrace = MUtil.list("raidingoutpost");

	public boolean gracePeriod = false;

	public void setGracePeriod(boolean gracePeriod) {
		this.gracePeriod = gracePeriod;
		this.changed();
	}

	// RaidClaims below

	public int raidClaimsAvailable = 2;
	public Map<Integer, Integer> raidClaimNumberAndGuiSlot = MUtil.map(1, 1, 2,2);

	public boolean raidClaimGuiHopper = true;
	public int raidClaimGuiSize = 9;

	public String raidClaimGuiInventoryTitle = "&c&lRaid Claim Manager";

	public int raidClaimGuiHelpButtonSlot = 0;
	public Material raidClaimGuiHelpButtonMaterial = Material.BOOK_AND_QUILL;
	public String raidClaimGuiHelpButtonName = "&bRaid Claim Help";
	public List<String> raidClaimGuiHelpButtonLore = MUtil.list("&5&oYou must claim your cannon with raid claims", "&5&oif you want to damage someone's base.", "&5&oYou do not need raid claims for counter cannoning.");

	public Material raidClaimGuiRaidclaimButtonMaterial = Material.TNT;
	public String raidClaimGuiRaidclaimButtonName = "&fRaid Claim &e%number%";
	public List<String> raidClaimGuiRaidclaimButtonLoreClaimed = MUtil.list("&fLocation:", " ", "&fWorld: &b%worldName%", "&fX: &b%x%", "&fZ: &b%z%", " ", "&eClick to unclaim");
	public List<String> raidClaimGuiRaidclaimButtonLoreUnclaimed = MUtil.list("&cClaim a Raid Claim with /f raidclaim <radius>");

	public String noRaidClaimsAvailableMsg = "&cYour faction currently has the maximum amount of raid claims set.";
	public String raidClaimSetMsg = "&aYou have created a raidclaim centered at your current location.";
	public String raidClaimRemovedMsg = "&aYour selected raidclaim has been removed.";

	public String mustUseRaidclaimTitleTopMessage = "&c&lWARNING";
	public String mustUseRaidclaimTitleBottomMessage = "&7You must use /f raidclaim to fire!";

	public int maxRaidclaimRadius = 5;
	public String maxRaidclaimRadiusMsg = "&cYou are not permitted to claim a raidclaim with a radius larger than %maxRadius%.";

	public String sandbotGuiName = "&e&lSand Bots";
	public int sandbotGuiInfoItemSlot = 4;
	public Material sandbotGuiInfoItemMaterial = Material.BOOK;
	public int sandbotGuiInfoItemMaterialDurability = 0;
	public String sandbotGuiInfoItemName = "&e&lInformation";
	public List<String> sandbotGuiInfoItemLore = MUtil.list(
			"&7Sand bots will automatically place sand",
			"&7below and &cbrick blocks &7they find nearby.",
			" ",
			"&7Each sand they place will cost your",
			"&7faction &f$10 &7per block.",
			" ",
			"&7Once the bot runs out of charges",
			"&7or your faction runs out of money",
			"&7the bot will despawn.",
			" ",
			"&cUsing the bots for any purpose other",
			"&cthan loading cannons will be punished."
	);

	public long refreshSandbotBlocksEveryXMillis = 5000;
	public long placeSandDelay = 200L;
	public int pricePerSand = 10;
	public long delaySandPlacementXMillisAfterPluginEnable = 5000;
	public int sandbotRadius = 4;
	public int sandbotYRadius = 2;
	public List<Material> sandbotTriggerBlocks = MUtil.list(Material.BRICK, Material.LAPIS_BLOCK, Material.SANDSTONE);

	public boolean sandbotGuiBorderGlassEnabled = true;
	public int sandbotGuiBorderGlassDurabilityId = 7;
	public boolean sandbotGuiBorderGlassGlowEnabled = false;

	public String sandbotSpawnButtonName = "&a&lSpawn Sand Bot #%sandbotNumber%";
	public String sandbotSpawnButtonHeadData = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQ1YzlhY2VhOGRhNzFiNGYyNTJjZDRkZWI1OTQzZjQ5ZTdkYmMwNzY0Mjc0YjI1YTZhNmY1ODc1YmFlYTMifX19";
	public String sandbotInfoButtonName = "&e&lSand Bot #%sandbotNumber%";
	public String sandbotInfoButtonHeadData = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ3ZTg2NGNlODliMGY2NGVmZDMxM2NlMjU4N2NiYjRlNjVkM2RmMThiMmExMjNkYzJhZjJlNTY2ZDAifX19";
	public String sandbotDespawnButtonName = "&c&lDespawn Sand Bot #%sandbotNumber%";
	public String sandbotDespawnButtonHeadData = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ1ZmNkMzEyODdkNjNlNzgyNmVhNzYwYTdlZDE1NGY2ODVkZmRjN2YzNDY1NzMyYTk2ZTYxOWIyZTEzNDcifX19";

	public String sandbotDespawnDespawnedMsg = "&aDespawned Sandbot #%sandbotNumber%.";
	public String sandbotDespawnDontExistMsg = "&cYou can't despawn something that doesn't exist.";
	public String sandbotSpawnAlreadySpawnedMsg = "&cThis sandbot is already spawned.";
	public String spawnbotSandPurchaseSandbotMsg = "&cIn order to spawn sandbot #%sandbotNumber% you must unlock it using /f upgrade.";
	public String spawnbotMustBeInFactionLandMsg = "&cYou can only spawn sandbots in your land.";
	public String sandbotDespawnedNoMoneyInFbankMsg = "&cYour sandbots have been despawned due to running out of money in /f bank.";
	public String spawnbotSpawnedMsg = "&aSandbot spawned at your location.";
	public String sandbotName = "&c&lSandbot";

	public List<String> sandbotInfoButtonLoreInactive = MUtil.list("&fStatus: &cInactive");
	public List<String> sandbotInfoButtonLoreActive = MUtil.list("&fStatus: &aActive");

	public boolean showFactionLandClaimedValues = false;

	public long updateBoardTimeMs = 30000;

}