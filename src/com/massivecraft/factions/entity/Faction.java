package com.massivecraft.factions.entity;

import com.massivecraft.factions.*;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.coll.FactionColl;
import com.massivecraft.factions.coll.MPlayerColl;
import com.massivecraft.factions.entity.objects.ChestTransaction;
import com.massivecraft.factions.entity.objects.FactionValue;
import com.massivecraft.factions.entity.objects.Mission;
import com.massivecraft.factions.predicate.PredicateCommandSenderFaction;
import com.massivecraft.factions.predicate.PredicateMPlayerRole;
import com.massivecraft.factions.task.TaskFactionTopCalculate;
import com.massivecraft.factions.util.*;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.collections.MassiveMapDef;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.predicate.Predicate;
import com.massivecraft.massivecore.predicate.PredicateAnd;
import com.massivecraft.massivecore.predicate.PredicateVisibleTo;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.store.EntityInternalMap;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.missions.MissionsManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.trait.Gravity;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Map.Entry;

public class Faction extends Entity<Faction> implements FactionsParticipator
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final transient String NODESCRIPTION = Txt.parse("<em><silver>no description set");
	public static final transient String NOMOTD = Txt.parse("<em><silver>no message of the day set");
	public static final transient String PAYPAL = "paypal@paypal.com";
	
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	public int version = 1;
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	// Faction banner
	public MassiveList<String> banner = new MassiveList<String>();
	// The actual faction id looks something like "54947df8-0e9e-4471-a2f9-9af509fb5889" and that is not too easy to remember for humans.
	// Thus we make use of a name. Since the id is used in all foreign key situations changing the name is fine.
	// Null should never happen. The name must not be null.
	private String name = null;
	
	// -------------------------------------------- //
	// VERSION
	// -------------------------------------------- //
	// Factions can optionally set a description for themselves.
	// This description can for example be seen in territorial alerts.
	// Null means the faction has no description.
	private String description = null;
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since just the getter and setter logic takes up quite some place.
	// Factions can optionally set a message of the day.
	// This message will be shown when logging on to the server.
	// Null means the faction has no motd
	private String motd = null;
	// We store the creation date for the faction.
	// It can be displayed on info pages etc.
	private long createdAtMillis = System.currentTimeMillis();
	// Factions can optionally set a home location.
	// If they do their members can teleport there using /f home
	// Null means the faction has no home.
	private PS home = null;
	// Faction Warps
	private MassiveMapDef<String, PS> warps = new MassiveMapDef<>();
	private MassiveMapDef<String, String> warpsPasswords = new MassiveMapDef<>();
	private MassiveMapDef<String, Material> warpItems = new MassiveMapDef<>();
	// Factions usually do not have a powerboost. It defaults to 0.
	// The powerBoost is a custom increase/decrease to default and maximum power.
	// Null means the faction has powerBoost (0).
	private Double powerBoost = null;
	// This is the ids of the invited players.
	// They are actually "senderIds" since you can invite "@console" to your faction.
	// Null means no one is invited
	private EntityInternalMap<Invitation> invitations = new EntityInternalMap<>(this, Invitation.class);
	// The keys in this map are factionIds.
	// Null means no special relation whishes.
	private MassiveMapDef<String, Rel> relationWishes = new MassiveMapDef<>();
	
	// Can anyone join the Faction?
	// If the faction is open they can.
	// If the faction is closed an invite is required.
	// Null means default.
	// private Boolean open = null;
	// The flag overrides are modifications to the default values.
	// Null means default.
	private MassiveMapDef<String, Boolean> flags = new MassiveMapDef<>();
	// The perm overrides are modifications to the default values.
	// Null means default.
	private MassiveMapDef<String, Set<Rel>> perms = new MassiveMapDef<>();
	// Credits balance
	private Integer credits = 0;
	// Store faction upgrades here
	private HashMap<String, Integer> upgrades = new HashMap<>();
	// Credits balance
	private Integer tnt = 0;
	// Chest Inventory
	private String inventoryString = "";
	private transient Inventory inventory;
	private ArrayList<ChestTransaction> chestTransactions = new ArrayList<>();
	
	public static Faction get(Object oid)
	{
		return FactionColl.get().get(oid);
	}

	private static String getMotdDesc(String motd)
	{
		if (motd == null) motd = NOMOTD;
		return motd;
	}

	// FIXME this probably needs to be moved elsewhere
	public static String clean(String message)
	{
		String target = message;
		if (target == null) return null;
		
		target = target.trim();
		if (target.isEmpty()) target = null;
		
		return target;
	}
	
	// -------------------------------------------- //
	// FACTION BANNERS
	// -------------------------------------------- //
	
	@Override
	public Faction load(Faction that)
	{
		this.setName(that.name);
		this.setDescription(that.description);
		this.setMotd(that.motd);
		this.setCreatedAtMillis(that.createdAtMillis);
		this.setHome(that.home);
		this.setPowerBoost(that.powerBoost);
		this.invitations.load(that.invitations);
		this.setRelationWishes(that.relationWishes);
		this.setFlagIds(that.flags);
		this.setPermIds(that.perms);
		
		that.warps.forEach((key, value) -> this.setWarp(value, key, that.warpsPasswords.get(key), that.warpItems.get(key)));
		
		this.setCredits(that.credits);
		this.setTnt(that.tnt);
		this.setUpgrades(that.upgrades);
		this.setInventoryString(that.inventoryString);
		this.setChestTransactions(that.chestTransactions);
		this.setBanner(that.banner);
		
		this.setNotificationTimeMinutes(that.notificationTimeMinutes);
		this.setAlarmEnabled(that.alarmEnabled);
		
		this.setBannedPlayerUuids(that.bannedPlayerUuids);
		
		this.setAltsOpen(that.altsOpen);
		
		// Missions
		this.setMissionRequirementComplete(that.missionRequirementComplete);
		this.setActiveMission(that.activeMission);
		this.setMissionStartTime(that.missionStartTime);
		
		this.setFactionWarnings(that.factionWarnings);
		
		this.setFactionPayPal(that.factionPayPal);
		this.setFactionDiscord(that.factionDiscord);
		
		this.setLastDrainMillis(that.lastDrainMillis);
		
		this.setShieldedHoursStartTime(that.shieldedHoursStartTime);
		this.requestChangeShieldedHours(that.shieldedHoursChangeRequestNewStartTime, that.shieldedHoursChangeRequestMillis);
		this.setShieldedHoursCooldownFromDisable(that.shieldedHoursCooldownFromDisable);

		this.setSpawnerChunks(that.spawnerChunks);

		this.setRoster(that.roster);
		this.setRosterKicksRemaining(that.rosterKicksRemaining);

		this.setRaidClaims(that.raidClaims);
		this.setSandbots(that.sandbots);

//		if (!isSystemFaction())
//		{
//			setFlag(MFlag.ID_EXPLOSIONS, true);
//			setFlag(MFlag.ID_OFFLINEEXPLOSIONS, true);
//
//			setRelationPermitted(MPerm.getPermContainer(), Rel.ALLY, true);
//			setRelationPermitted(MPerm.getPermContainer(), Rel.ENEMY, true);
//			setRelationPermitted(MPerm.getPermContainer(), Rel.NEUTRAL, true);
//		}
		
		return this;
	}

	private Set<ChunkPos> spawnerChunks = new HashSet<>();

	public Set<ChunkPos> getSpawnerChunks() {
		return spawnerChunks;
	}

	public void setSpawnerChunks(Set<ChunkPos> spawnerChunks) {
		this.spawnerChunks = spawnerChunks;
		this.changed();
	}

	public void addSpawnerChunk(ChunkPos ps) {
		this.spawnerChunks.add(ps);
		this.changed();
	}

	public void removeSpawnerChunk(ChunkPos ps) {
		this.spawnerChunks.remove(ps);
		this.changed();
	}

	public boolean isChunkWithinRaidtimerRadiusFromSpawnerChunk(String locWorldName, int x1, int z1) {
		if (spawnerChunks.isEmpty()) return false;

		boolean withinRadius = false;

		for (ChunkPos spawnerChunkPs : spawnerChunks) {
			if (!MUtil.equals(locWorldName, spawnerChunkPs.getWorld())) continue;

			int x2 = spawnerChunkPs.getX();
			int z2 = spawnerChunkPs.getZ();

			double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));

			if (distance <= MConf.get().spawnerChunkLockdownRadius) {
				withinRadius = true;
				break;
			}
		}

		return withinRadius;
	}

	private MassiveMap<Integer, Set<PS>> raidClaims = new MassiveMap<>();

	public MassiveMap<Integer, Set<PS>> getRaidClaims() {
		return raidClaims;
	}

	public void setRaidClaims(MassiveMap<Integer, Set<PS>> raidClaims) {
		this.raidClaims = raidClaims;
		this.changed();
	}

	public boolean isRaidClaimAvailable(int raidclaimNumber) {
		if (!raidClaims.containsKey(raidclaimNumber)) {
			raidClaims.put(raidclaimNumber, null);
		}

		Set<PS> raidClaimsRet = raidClaims.get(raidclaimNumber);

		return raidClaimsRet == null || raidClaimsRet.isEmpty();
	}

	// Shield hours
	private Integer shieldedHoursStartTime = null;
	
	public Integer getShieldedHoursStartTime()
	{
		return shieldedHoursStartTime;
	}
	
	public void setShieldedHoursStartTime(Integer startTime)
	{
		this.shieldedHoursStartTime = startTime;
		this.changed();
	}
	
	private Long shieldedHoursCooldownFromDisable = null;
	
	public Long getShieldedHoursCooldownFromDisable() {
		return shieldedHoursCooldownFromDisable;
	}
	
	public void setShieldedHoursCooldownFromDisable(Long millis) {
		this.shieldedHoursCooldownFromDisable = millis;
		this.changed();
	}
	
	private Long shieldedHoursChangeRequestMillis = null;
	private Integer shieldedHoursChangeRequestNewStartTime = null;
	
	public Long getShieldedHoursChangeRequestMillis()
	{
		return shieldedHoursChangeRequestMillis;
	}
	
	public Integer getShieldedHoursChangeRequestNewStartTime()
	{
		return shieldedHoursChangeRequestNewStartTime;
	}
	
	public void requestChangeShieldedHours(Integer newStartTime, Long millis)
	{
		this.shieldedHoursChangeRequestMillis = millis;
		this.shieldedHoursChangeRequestNewStartTime = newStartTime;
		this.changed();
	}
	
	public boolean isShielded()
	{
		if (shieldedHoursStartTime == null) return false;
		
		Date date = new Date();
		date.setHours(TimeUtil.getTimeHours());
		date.setMinutes(TimeUtil.getTimeMinutes());
		date.setSeconds(TimeUtil.getTimeSeconds());
		
		return getAllowedHours().contains(date.getHours());
	}
	
	private Set<Integer> getAllowedHours()
	{
		Set<Integer> allowedHours = new HashSet<>();
		
		for (int i = 0; i < MConf.get().shieldLengthHrs; i++)
		{
			int hour = shieldedHoursStartTime + i;
			
			if (hour > 24)
			{
				allowedHours.add(0);
				hour = hour - 24;
			}
			
			allowedHours.add(hour);
		}
		
		return allowedHours;
	}
	
	@Override
	public void preDetach(String id)
	{
		if (!this.isLive()) return;
		
		// NOTE: Existence check is required for compatibility with some plugins.
		// If they have money ...
		if (Money.exists(this))
		{
			// ... remove it.
			Money.set(this, null, 0, "Factions");
		}
	}
	
	// MORE ALT STUFF
	
	private boolean altsOpen = false;
	
	public boolean isAltsOpen() {
		return altsOpen;
	}
	
	public void setAltsOpen(boolean altsOpen) {
		this.altsOpen = altsOpen;
		this.changed();
	}
	
	//
	
	// MISSIONS
	
	private transient boolean isSpinningMission = false;
	private String activeMission = null;
	private long missionStartTime = 0;
	private Integer missionRequirementComplete = 0;
	
	public boolean isSpinningMission() {
		return isSpinningMission;
	}
	
	public void setSpinningMission(boolean spinningMission) {
		this.isSpinningMission = spinningMission;
	}
	
	public Mission getActiveMission()
	{
		return MissionsManager.get().getMissionByName(activeMission);
	}
	
	public void setActiveMission(String mission)
	{
		this.activeMission = mission;
		this.changed();
	}
	
	public long getMissionStartTime()
	{
		return this.missionStartTime;
	}
	
	public void setMissionStartTime(long missionStartTime)
	{
		this.missionStartTime = missionStartTime;
		this.changed();
	}
	
	public Integer getMissionRequirementComplete()
	{
		return missionRequirementComplete;
	}
	
	public void setMissionRequirementComplete(Integer amount)
	{
		this.missionRequirementComplete = amount;
		this.changed();
	}
	
	// END MISSIONS
	
	public void setFactionWarnings(List<String> factionWarnings)
	{
		this.factionWarnings = factionWarnings;
		this.changed();
	}
	
	private List<String> factionWarnings = new ArrayList<>();
	
	public List<String> getFactionWarnings()
	{
		return factionWarnings;
	}
	
	public void addWarning(String reason)
	{
		this.factionWarnings.add(reason);
		this.changed();
	}
	
	public void removeWarning(String reason)
	{
		this.factionWarnings.remove(reason);
		this.changed();
	}
	
	// F Paypal
	private String factionDiscord = null;
	
	public String getFactionDiscord()
	{
		return factionDiscord;
	}
	
	public void setFactionDiscord(String factionDiscord)
	{
		this.factionDiscord = factionDiscord;
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: Faction Paypal
	// -------------------------------------------- //
	
	// F Paypal
	private String factionPayPal = null;
	
	public String getFactionPayPal()
	{
		return factionPayPal == null ? PAYPAL : factionPayPal;
	}
	
	public void setFactionPayPal(String factionPayPal)
	{
		this.factionPayPal = factionPayPal;
		this.changed();
	}
	
	// f alarm
	private boolean alarmEnabled = false;
	
	public boolean isAlarmEnabled()
	{
		return alarmEnabled;
	}
	
	public void setAlarmEnabled(boolean alarmEnabled)
	{
		this.alarmEnabled = alarmEnabled;
		this.changed();
	}
	
	// banned players
	private Set<String> bannedPlayerUuids = new HashSet<>();
	
	public Set<String> getBannedPlayerUuids()
	{
		return bannedPlayerUuids;
	}
	
	public void setBannedPlayerUuids(Set<String> bannedPlayerUuids)
	{
		this.bannedPlayerUuids = bannedPlayerUuids;
	}
	
	public void banPlayer(String uuid)
	{
		this.bannedPlayerUuids.add(uuid);
		this.changed();
	}
	
	public void unbanPlayer(String uuid)
	{
		this.bannedPlayerUuids.remove(uuid);
		this.changed();
	}
	
	public boolean isPlayerBanned(Player player)
	{
		return this.bannedPlayerUuids.contains(player.getUniqueId().toString());
	}
	
	private long lastCheckedMillis = System.currentTimeMillis();
	
	// Faction Check
	public int notificationTimeMinutes = 0;
	
	public long getLastCheckedMillis()
	{
		return lastCheckedMillis;
	}
	
	public void setLastCheckedMillis(long millis)
	{
		this.lastCheckedMillis = millis;
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: check
	// -------------------------------------------- //
	
	public int getNotificationTimeMinutes()
	{
		return notificationTimeMinutes;
	}
	
	public void setNotificationTimeMinutes(int notificationTimeMinutes)
	{
		this.notificationTimeMinutes = notificationTimeMinutes;
		this.changed();
	}
	
	public void setBanner(List<String> pattern)
	{
		banner.clear();
		banner.addAll(pattern);
		this.changed();
	}
	
	public ItemStack getBanner()
	{
		ItemStack banner = new ItemStack(Material.BANNER);
		BannerMeta data = (BannerMeta) banner.getItemMeta();
		DyeColor basecolor = DyeColor.valueOf(this.banner.get(0));
		data.setBaseColor(basecolor);
		int x = this.banner.size();
		for (int i = 1; i < x; i++)
		{
			String[] temp = (this.banner).get(i).split(" ");
			String c = temp[0];
			String p = temp[1];
			
			DyeColor dyecolor = DyeColor.BLACK;
			PatternType patterntype = PatternType.BASE;
			for (DyeColor dc : DyeColor.values())
			{
				if (c.equalsIgnoreCase(dc.name())) dyecolor = dc;
			}
			for (PatternType pt : PatternType.values())
			{
				if (p.equalsIgnoreCase(pt.toString())) patterntype = pt;
			}
			Pattern pattern = new Pattern(dyecolor, patterntype);
			data.addPattern(pattern);
		}
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GRAY + getName() + "'s banner");
		data.setLore(lore);
		banner.setItemMeta(data);
		return banner;
	}
	
	// -------------------------------------------- //
	// FACTION CHEST
	// -------------------------------------------- //
	
	public void setBanner(MassiveList<String> pattern)
	{
		banner.clear();
		banner.addAll(pattern);
		this.changed();
	}
	
	public boolean hasBanner()
	{
		return this.banner.size() != 0;
	}
	
	public void saveInventory()
	{
		if (inventory != null)
		{
			inventoryString = InventoryUtil.toBase64(inventory);
		}
	}
	
	public Inventory getInventory()
	{
		if (inventory == null)
		{
			inventory = InventoryUtil.fromBase64(inventoryString, ChatColor.GREEN + getName() + " - Faction Chest");
		}
		return inventory;
	}
	
	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
	}
	
	public void setInventoryString(String inventoryString)
	{
		this.inventoryString = inventoryString;
	}
	
	public void addChestTransaction(ChestTransaction chestTransaction)
	{
		this.chestTransactions.add(chestTransaction);
		this.changed();
	}
	
	// -------------------------------------------- //
	// FACTION UPGRADES
	// -------------------------------------------- //
	
	public ArrayList<ChestTransaction> getChestTransactions()
	{
		return this.chestTransactions;
	}
	
	public void setChestTransactions(ArrayList<ChestTransaction> chestTransactions)
	{
		this.chestTransactions = chestTransactions;
		this.changed();
	}
	
	public HashMap<String, Integer> getUpgrades()
	{
		return upgrades;
	}
	
	public void setUpgrades(HashMap<String, Integer> upgrades)
	{
		this.upgrades = upgrades;
	}
	
	// -------------------------------------------- //
	// FIELD: id
	// -------------------------------------------- //
	
	// FINER
	
	public int getLevel(String upgrade)
	{
		if (isSystemFaction())
		{
			return 0;
		}
		return upgrades.get(upgrade) == null ? 0 : upgrades.get(upgrade);
	}
	
	public void increaseLevel(String upgrade)
	{
		if (isSystemFaction()) return;
		int newLevel = getLevel(upgrade) + 1;
		upgrades.remove(upgrade);
		upgrades.put(upgrade, newLevel);
		this.changed();
	}
	
	public boolean isNone()
	{
		return this.getId().equals(Factions.ID_NONE);
	}
	
	// -------------------------------------------- //
	// FIELD: tnt
	// -------------------------------------------- //
	
	public boolean isNormal()
	{
		return !this.isNone();
	}

	public boolean isSystemFaction() {
//		if (IntegrationRaidingoutpost.get().isIntegrationActive() && getId().equals(RaidingOutpostPlugin.get().getRaidingOutpost().getId()))
//		{
//			return true;
//		}

		return getId() == null || (getId().equalsIgnoreCase(Factions.ID_NONE) || (getId().equalsIgnoreCase(Factions.ID_SAFEZONE) || getId().equalsIgnoreCase(Factions.ID_WARZONE)));
	}
	
	public int getTnt()
	{
		Integer ret = this.tnt;
		if (ret == null) ret = 0;
		return ret;
	}
	
	public void setTnt(Integer tnt)
	{
		// Clean input
		Integer target = tnt;
		
		if (target == null || target == 0) target = null;
		
		// Detect Nochange
		if (MUtil.equals(this.tnt, target)) return;
		
		// Apply
		this.tnt = target;
		
		// Mark as changed
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: credits
	// -------------------------------------------- //

	public int getCredits()
	{
		Integer ret = this.credits;
		if (ret == null) ret = 0;
		return ret;
	}

	public void setCredits(Integer amount)
	{
		this.credits = amount;
		this.changed();
	}

	public void addCredits(Integer amount)
	{
		this.credits = this.credits + amount;
		this.changed();
	}

	public void takeCredits(Integer amount)
	{
		this.credits = this.credits - amount;
		this.changed();
	}

	public void addTnt(Integer tnt)
	{
		this.setTnt(getTnt() + tnt);
		this.changed();
	}
	
	public void takeTnt(Integer tnt)
	{
		this.setTnt(getTnt() - tnt);
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: name
	// -------------------------------------------- //
	
	// Drain
	private long lastDrainMillis = 0;
	
	public long getLastDrainMillis()
	{
		return lastDrainMillis;
	}
	
	public void setLastDrainMillis(long millis)
	{
		this.lastDrainMillis = millis;
		this.changed();
	}
	
	// FINER
	
	@Override
	public String getName()
	{
		String ret = this.name;
		
		if (MConf.get().factionNameForceUpperCase)
		{
			ret = ret.toUpperCase();
		}
		
		return ret;
	}
	
	public void setName(String name)
	{
		// Clean input
		String target = name;
		
		// Detect Nochange
		if (MUtil.equals(this.name, target)) return;
		
		// Apply
		this.name = target;
		
		// Mark as changed
		this.changed();
	}
	
	public String getComparisonName()
	{
		return MiscUtil.getComparisonString(this.getName());
	}
	
	// -------------------------------------------- //
	// FIELD: description
	// -------------------------------------------- //
	
	// RAW
	
	public String getName(String prefix)
	{
		return prefix + this.getName();
	}
	
	public String getName(RelationParticipator observer)
	{
		if (observer == null) return getName();
		return this.getName(this.getColorTo(observer).toString());
	}
	
	public boolean hasDescription()
	{
		return this.description != null;
	}
	
	// FINER
	
	public String getDescription()
	{
		return this.description;
	}
	
	// -------------------------------------------- //
	// FIELD: motd
	// -------------------------------------------- //
	
	// RAW
	
	public void setDescription(String description)
	{
		// Clean input
		String target = clean(description);
		
		// Detect Nochange
		if (MUtil.equals(this.description, target)) return;
		
		// Apply
		this.description = target;
		
		// Mark as changed
		this.changed();
	}
	
	public String getDescriptionDesc()
	{
		String motd = this.getDescription();
		if (motd == null) motd = NODESCRIPTION;
		return motd;
	}
	
	public boolean hasMotd()
	{
		return this.motd != null;
	}
	
	// FINER
	
	public String getMotd()
	{
		return this.motd;
	}
	
	public void setMotd(String motd)
	{
		// Clean input
		String target = clean(motd);
		
		// Detect Nochange
		if (MUtil.equals(this.motd, target)) return;
		
		// Apply
		this.motd = target;
		
		// Mark as changed
		this.changed();
	}
	
	public String getMotdDesc()
	{
		return getMotdDesc(this.getMotd());
	}
	
	// -------------------------------------------- //
	// FIELD: createdAtMillis
	// -------------------------------------------- //
	
	public List<Object> getMotdMessages()
	{
		// Create
		List<Object> ret = new MassiveList<>();
		
		// Fill
		Object title = this.getName() + " - Message of the Day";
		title = Txt.titleize(title);
		ret.add(title);
		
		String motd = Txt.parse("<i>") + this.getMotdDesc();
		ret.add(motd);
		
		ret.add("");
		
		// Return
		return ret;
	}
	
	public long getCreatedAtMillis()
	{
		return this.createdAtMillis;
	}
	
	public void setCreatedAtMillis(long createdAtMillis)
	{
		// Clean input
		long target = createdAtMillis;
		
		// Detect Nochange
		if (MUtil.equals(this.createdAtMillis, createdAtMillis)) return;
		
		// Apply
		this.createdAtMillis = target;
		
		// Mark as changed
		this.changed();
	}
	
	public long getAge()
	{
		return this.getAge(System.currentTimeMillis());
	}
	
	public long getAge(long now)
	{
		return now - this.getCreatedAtMillis();
	}
	
	// -------------------------------------------- //
	// FIELD: home/warp
	// -------------------------------------------- //
	
	public void setWarpItem(String name, Material material)
	{
		warpItems.put(name, material);
		this.changed();
	}
	
	public Material getWarpItem(String name) {
		Material material = Material.STAINED_GLASS_PANE;
		
		if (warpItems.containsKey(name)) {
			material = warpItems.get(name);
		}
		
		return material;
	}
	
	public PS getWarp(String name)
	{
		if (warps.containsKey(name))
		{
			this.verifyWarp(name);
			return warps.getOrDefault(name, null);
		}
		else
			return null;
	}
	
	public boolean deleteWarp(String name)
	{
		if (warps.containsKey(name))
		{
			warps.remove(name);
			warpsPasswords.remove(name);
			warpItems.remove(name);
			return true;
		}
		else
			return false;
	}
	
	public ArrayList<String> getAllWarps()
	{
		ArrayList<String> warp = new ArrayList<String>();
		for (Entry<String, PS> entry : this.warps.entrySet())
		{
			warp.add(entry.getKey());
		}
		return warp;
	}
	
	public String getWarpPassword(String name)
	{
		return warpsPasswords.getOrDefault(name, null);
	}
	
	public void verifyWarp(String name)
	{
		if (this.isValidWarp(warps.get(name)))
			return;
		warps.remove(name);
		warpsPasswords.remove(name);
		warpItems.remove(name);
		this.changed();
		msg("<b>Your faction warp " + name + " has been un-set since it is no longer in your territory.");
	}
	
	public boolean isValidWarp(PS ps)
	{
		return ps == null || !MConf.get().warpsMustBeInClaimedTerritory || BoardColl.get().getFactionAt(ps) == this;
	}
	
	public void setWarp(PS home, String name, String password, Material material)
	{
		if (warps.containsKey(name))
		{
			if (MUtil.equals(warps.get(name), home))
				return;
			warps.replace(name, home);
			warpsPasswords.replace(name, password);
			warpItems.replace(name, material);
			
			this.changed();
			
			return;
		}
		
		// Apply
		warps.put(name, home);
		warpsPasswords.put(name, password);
		warpItems.put(name, material);
		
		// Mark as changed
		this.changed();
	}
	
	public PS getHome()
	{
		this.verifyHomeIsValid();
		return this.home;
	}
	
	public void setHome(PS home)
	{
		// Clean input
		PS target = home;
		
		// Detect Nochange
		if (MUtil.equals(this.home, target)) return;
		
		// Apply
		this.home = target;
		
		// Mark as changed
		this.changed();
	}
	
	public void verifyHomeIsValid()
	{
		if (this.isValidHome(this.home)) return;
		this.home = null;
		this.changed();
		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
	}
	
	public boolean isValidHome(PS ps)
	{
		if (ps == null) return true;
		if (!MConf.get().homesMustBeInClaimedTerritory) return true;
		if (BoardColl.get().getFactionAt(ps) == this) return true;
		return false;
	}
	
	// -------------------------------------------- //
	// FIELD: powerBoost
	// -------------------------------------------- //
	
	public boolean hasHome()
	{
		return this.getHome() != null;
	}
	
	// RAW
	@Override
	public double getPowerBoost()
	{
		Double ret = this.powerBoost;
		if (ret == null) ret = 0D;
		return ret;
	}
	
	// -------------------------------------------- //
	// FIELD: open
	// -------------------------------------------- //
	
	// Nowadays this is a flag!
	
	@Override
	public void setPowerBoost(Double powerBoost)
	{
		// Clean input
		Double target = powerBoost;
		
		if (target == null || target == 0) target = null;
		
		// Detect Nochange
		if (MUtil.equals(this.powerBoost, target)) return;
		
		// Apply
		this.powerBoost = target;
		
		// Mark as changed
		this.changed();
	}
	
	@Deprecated
	public boolean isDefaultOpen()
	{
		return MFlag.getFlagOpen().isStandard();
	}
	
	@Deprecated
	public boolean isOpen()
	{
		return this.getFlag(MFlag.getFlagOpen());
	}
	
	// -------------------------------------------- //
	// FIELD: invitedPlayerIds
	// -------------------------------------------- //
	
	// RAW
	
	@Deprecated
	public void setOpen(Boolean open)
	{
		MFlag flag = MFlag.getFlagOpen();
		if (open == null) open = flag.isStandard();
		this.setFlag(flag, open);
	}
	
	// FINER
	
	public EntityInternalMap<Invitation> getInvitations()
	{
		return this.invitations;
	}
	
	public boolean isInvitedAlt(String playerId) {
		return this.isInvited(playerId) && this.getInvitations().get(playerId).isAlt();
	}
	
	public boolean isInvitedAlt(MPlayer mPlayer)
	{
		return this.isInvitedAlt(mPlayer.getId());
	}
	
	public boolean isInvited(String playerId)
	{
		return this.getInvitations().containsKey(playerId);
	}
	
	public boolean isInvited(MPlayer mplayer)
	{
		return this.isInvited(mplayer.getId());
	}
	
	public boolean uninvite(String playerId)
	{
		System.out.println(playerId);
		return this.getInvitations().detachId(playerId) != null;
	}
	
	public boolean uninvite(MPlayer mplayer)
	{
		return uninvite(mplayer.getId());
	}
	
	// -------------------------------------------- //
	// FIELD: relationWish
	// -------------------------------------------- //
	
	// RAW
	
	public void invite(String playerId, Invitation invitation)
	{
		uninvite(playerId);
		this.invitations.attach(invitation, playerId);
	}
	
	public Map<String, Rel> getRelationWishes()
	{
		return this.relationWishes;
	}
	
	// FINER
	
	public void setRelationWishes(Map<String, Rel> relationWishes)
	{
		// Clean input
		MassiveMapDef<String, Rel> target = new MassiveMapDef<>(relationWishes);
		
		// Detect Nochange
		if (MUtil.equals(this.relationWishes, target)) return;
		
		// Apply
		this.relationWishes = target;
		
		// Mark as changed
		this.changed();
	}
	
	public Rel getRelationWish(String factionId)
	{
		Rel ret = this.getRelationWishes().get(factionId);
		if (ret == null) ret = Rel.ENEMY;
		return ret;
	}
	
	public Rel getRelationWish(Faction faction)
	{
		return this.getRelationWish(faction.getId());
	}
	
	public void setRelationWish(String factionId, Rel rel)
	{
		Map<String, Rel> relationWishes = this.getRelationWishes();
		if (rel == null || rel == Rel.ENEMY)
		{
			relationWishes.remove(factionId);
		}
		else
		{
			relationWishes.put(factionId, rel);
		}
		this.setRelationWishes(relationWishes);
	}
	
	// -------------------------------------------- //
	// FIELD: flagOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public void setRelationWish(Faction faction, Rel rel)
	{
		this.setRelationWish(faction.getId(), rel);
	}
	
	public Map<MFlag, Boolean> getFlags()
	{
		// We start with default values ...
		Map<MFlag, Boolean> ret = new MassiveMap<>();
		for (MFlag mflag : MFlag.getAll())
		{
			ret.put(mflag, mflag.isStandard());
		}
		
		// ... and if anything is explicitly set we use that info ...
		Iterator<Entry<String, Boolean>> iter = this.flags.entrySet().iterator();
		while (iter.hasNext())
		{
			// ... for each entry ...
			Entry<String, Boolean> entry = iter.next();
			
			// ... extract id and remove null values ...
			String id = entry.getKey();
			if (id == null)
			{
				iter.remove();
				this.changed();
				continue;
			}
			
			// ... resolve object and skip unknowns ...
			MFlag mflag = MFlag.get(id);
			if (mflag == null) continue;
			
			ret.put(mflag, entry.getValue());
		}
		
		return ret;
	}
	
	public void setFlags(Map<MFlag, Boolean> flags)
	{
		Map<String, Boolean> flagIds = new MassiveMap<>();
		for (Entry<MFlag, Boolean> entry : flags.entrySet())
		{
			flagIds.put(entry.getKey().getId(), entry.getValue());
		}
		setFlagIds(flagIds);
	}
	
	// FINER
	
	public void setFlagIds(Map<String, Boolean> flagIds)
	{
		// Clean input
		MassiveMapDef<String, Boolean> target = new MassiveMapDef<>();
		for (Entry<String, Boolean> entry : flagIds.entrySet())
		{
			String key = entry.getKey();
			if (key == null) continue;
			key = key.toLowerCase(); // Lowercased Keys Version 2.6.0 --> 2.7.0
			
			Boolean value = entry.getValue();
			if (value == null) continue;
			
			target.put(key, value);
		}
		
		// Detect Nochange
		if (MUtil.equals(this.flags, target)) return;
		
		// Apply
		this.flags = new MassiveMapDef<>(target);
		
		// Mark as changed
		this.changed();
	}
	
	public boolean getFlag(String flagId)
	{
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.get(flagId);
		if (ret != null) return ret;
		
		MFlag flag = MFlag.get(flagId);
		if (flag == null) throw new NullPointerException("flag");
		
		return flag.isStandard();
	}
	
	public boolean getFlag(MFlag flag)
	{
		if (flag == null) throw new NullPointerException("flag");
		
		String flagId = flag.getId();
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.get(flagId);
		if (ret != null) return ret;
		
		return flag.isStandard();
	}
	
	public Boolean setFlag(String flagId, boolean value)
	{
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.put(flagId, value);
		if (ret == null || ret != value) this.changed();
		return ret;
	}
	
	// -------------------------------------------- //
	// FIELD: permOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public Boolean setFlag(MFlag flag, boolean value)
	{
		if (flag == null) throw new NullPointerException("flag");
		
		String flagId = flag.getId();
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.put(flagId, value);
		if (ret == null || ret != value) this.changed();
		return ret;
	}
	
	public Map<MPerm, Set<Rel>> getPerms()
	{
		// We start with default values ...
		Map<MPerm, Set<Rel>> ret = new MassiveMap<>();
		for (MPerm mperm : MPerm.getAll())
		{
			ret.put(mperm, new MassiveSet<>(mperm.getStandard()));
		}
		
		// ... and if anything is explicitly set we use that info ...
		Iterator<Entry<String, Set<Rel>>> iter = this.perms.entrySet().iterator();
		while (iter.hasNext())
		{
			// ... for each entry ...
			Entry<String, Set<Rel>> entry = iter.next();
			
			// ... extract id and remove null values ...
			String id = entry.getKey();
			if (id == null)
			{
				iter.remove();
				continue;
			}
			
			// ... resolve object and skip unknowns ...
			MPerm mperm = MPerm.get(id);
			if (mperm == null) continue;
			
			ret.put(mperm, new MassiveSet<>(entry.getValue()));
		}
		
		return ret;
	}
	
	public void setPerms(Map<MPerm, Set<Rel>> perms)
	{
		Map<String, Set<Rel>> permIds = new MassiveMap<>();
		for (Entry<MPerm, Set<Rel>> entry : perms.entrySet())
		{
			permIds.put(entry.getKey().getId(), entry.getValue());
		}
		setPermIds(permIds);
	}
	
	// FINER
	
	public void setPermIds(Map<String, Set<Rel>> perms)
	{
		// Clean input
		MassiveMapDef<String, Set<Rel>> target = new MassiveMapDef<>();
		for (Entry<String, Set<Rel>> entry : perms.entrySet())
		{
			String key = entry.getKey();
			if (key == null) continue;
			key = key.toLowerCase(); // Lowercased Keys Version 2.6.0 --> 2.7.0
			
			Set<Rel> value = entry.getValue();
			if (value == null) continue;
			
			target.put(key, value);
		}
		
		// Detect Nochange
		if (MUtil.equals(this.perms, target)) return;
		
		// Apply
		this.perms = target;
		
		// Mark as changed
		this.changed();
	}
	
	public boolean isPermitted(String permId, Rel rel, MPlayer mPlayer)
	{
		if (permId == null) throw new NullPointerException("permId");

		if (mPlayer != null && mPlayer.getFaction().getId().equals(getId()) && mPlayer.getPlayerPermValue(permId) != null)
		{
			return mPlayer.getPlayerPermValue(permId);
		}
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels.contains(rel);
		
		MPerm perm = MPerm.get(permId);
		if (perm == null) throw new NullPointerException("perm");
		
		return perm.getStandard().contains(rel);
	}
	
	// ---
	
	public boolean isPermitted(MPerm perm, Rel rel, MPlayer mPlayer)
	{
		if (perm == null) throw new NullPointerException("perm");
		
		String permId = perm.getId();
		if (permId == null) throw new NullPointerException("permId");

		if (mPlayer != null && mPlayer.getFaction().getId().equals(getId()) && mPlayer.getPlayerPermValue(perm) != null)
		{
			return mPlayer.getPlayerPermValue(perm);
		}
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels.contains(rel);
		
		return perm.getStandard().contains(rel);
	}
	
	public Set<Rel> getPermitted(MPerm perm)
	{
		if (perm == null) throw new NullPointerException("perm");
		
		String permId = perm.getId();
		if (permId == null) throw new NullPointerException("permId");
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels;
		
		return perm.getStandard();
	}
	
	public Set<Rel> getPermitted(String permId)
	{
		if (permId == null) throw new NullPointerException("permId");
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels;
		
		MPerm perm = MPerm.get(permId);
		if (perm == null) throw new NullPointerException("perm");
		
		return perm.getStandard();
	}
	
	// ---
	// TODO: Fix these below. They are reworking the whole map.
	
	@Deprecated
	// Use getPermitted instead. It's much quicker although not immutable.
	public Set<Rel> getPermittedRelations(MPerm perm)
	{
		return this.getPerms().get(perm);
	}
	
	public void setPermittedRelations(MPerm perm, Set<Rel> rels)
	{
		Map<MPerm, Set<Rel>> perms = this.getPerms();
		perms.put(perm, rels);
		this.setPerms(perms);
	}
	
	public void setPermittedRelations(MPerm perm, Rel... rels)
	{
		Set<Rel> temp = new HashSet<>();
		temp.addAll(Arrays.asList(rels));
		this.setPermittedRelations(perm, temp);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: RelationParticipator
	// -------------------------------------------- //
	
	public void setRelationPermitted(MPerm perm, Rel rel, boolean permitted)
	{
		Map<MPerm, Set<Rel>> perms = this.getPerms();
		
		Set<Rel> rels = perms.get(perm);
		
		boolean changed;
		if (permitted)
		{
			changed = rels.add(rel);
		}
		else
		{
			changed = rels.remove(rel);
		}
		
		this.setPerms(perms);
		
		if (changed) this.changed();
	}
	
	@Override
	public String describeTo(RelationParticipator observer, boolean ucfirst)
	{
		return RelationUtil.describeThatToMe(this, observer, ucfirst);
	}
	
	@Override
	public String describeTo(RelationParticipator observer)
	{
		return RelationUtil.describeThatToMe(this, observer);
	}
	
	@Override
	public Rel getRelationTo(RelationParticipator observer)
	{
		return RelationUtil.getRelationOfThatToMe(this, observer);
	}
	
	@Override
	public Rel getRelationTo(RelationParticipator observer, boolean ignorePeaceful)
	{
		return RelationUtil.getRelationOfThatToMe(this, observer, ignorePeaceful);
	}
	
	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	// TODO: Implement a has enough feature.
	
	@Override
	public ChatColor getColorTo(RelationParticipator observer)
	{
		return RelationUtil.getColorOfThatToMe(this, observer);
	}
	
	public double getPower()
	{
		if (this.getFlag(MFlag.getFlagInfpower())) return 999999;
		
		double ret = 0;
		for (MPlayer mplayer : this.getMPlayers())
		{
			ret += mplayer.getPower();
		}
		
		ret = this.limitWithPowerMax(ret);
		ret += this.getPowerBoost();
		
		return ret;
	}
	
	public double getPowerMax()
	{
		if (this.getFlag(MFlag.getFlagInfpower())) return 999999;
		
		double ret = 0;
		for (MPlayer mplayer : this.getMPlayers())
		{
			ret += mplayer.getPowerMax();
		}
		
		ret = this.limitWithPowerMax(ret);
		ret += this.getPowerBoost();
		
		return ret;
	}
	
	private double limitWithPowerMax(double power)
	{
		// NOTE: 0.0 powerMax means there is no max power
		double powerMax = MConf.get().factionPowerMax;
		
		return powerMax <= 0 || power < powerMax ? power : powerMax;
	}
	
	public int getPowerRounded()
	{
		return (int) Math.round(this.getPower());
	}
	
	public int getPowerMaxRounded()
	{
		return (int) Math.round(this.getPowerMax());
	}
	
	public int getLandCount()
	{
		return BoardColl.get().getCount(this);
	}
	
	public int getLandCountInWorld(String worldName)
	{
		return Board.get(worldName).getCount(this);
	}
	
	// -------------------------------------------- //
	// WORLDS
	// -------------------------------------------- //
	
	public boolean hasLandInflation()
	{
		return this.getLandCount() > this.getPowerRounded();
	}
	
	// -------------------------------------------- //
	// FOREIGN KEY: MPLAYER
	// -------------------------------------------- //
	
	public Set<String> getClaimedWorlds()
	{
		return BoardColl.get().getClaimedWorlds(this);
	}
	
	public List<MPlayer> getMPlayers()
	{
		return new MassiveList<>(FactionsIndex.get().getMPlayers(this));
	}
	
	public List<MPlayer> getMPlayers(Predicate<? super MPlayer> where, Comparator<? super MPlayer> orderby, Integer limit, Integer offset)
	{
		return MUtil.transform(this.getMPlayers(), where, orderby, limit, offset);
	}
	
	public List<MPlayer> getMPlayersWhere(Predicate<? super MPlayer> predicate)
	{
		return this.getMPlayers(predicate, null, null, null);
	}
	
	public List<MPlayer> getMPlayersWhereOnline(boolean online)
	{
		return this.getMPlayersWhere(online ? SenderColl.PREDICATE_ONLINE : SenderColl.PREDICATE_OFFLINE);
	}
	
	public List<MPlayer> getMPlayersWhereOnlineTo(Object senderObject)
	{
		return this.getMPlayersWhere(PredicateAnd.get(SenderColl.PREDICATE_ONLINE, PredicateVisibleTo.get(senderObject)));
	}
	
	public List<MPlayer> getMPlayersWhereRole(Rel role)
	{
		return this.getMPlayersWhere(PredicateMPlayerRole.get(role));
	}
	
	public MPlayer getLeader()
	{
		List<MPlayer> ret = this.getMPlayersWhereRole(Rel.LEADER);
		if (ret.size() == 0) return null;
		return ret.get(0);
	}
	
	public List<CommandSender> getOnlineCommandSenders()
	{
		// Create Ret
		List<CommandSender> ret = new MassiveList<>();
		
		// Fill Ret
		for (CommandSender sender : IdUtil.getLocalSenders())
		{
			if (MUtil.isntSender(sender)) continue;
			
			MPlayer mplayer = MPlayer.get(sender);
			if (mplayer.getFaction() != this) continue;
			
			ret.add(sender);
		}
		
		// Return Ret
		return ret;
	}
	
	public List<Player> getOnlinePlayers()
	{
		// Create Ret
		List<Player> ret = new MassiveList<>();
		
		// Fill Ret
		for (Player player : MUtil.getOnlinePlayers())
		{
			if (MUtil.isntPlayer(player)) continue;
			
			MPlayer mplayer = MPlayer.get(player);
			if (mplayer.getFaction() != this) continue;
			
			ret.add(player);
		}
		
		// Return Ret
		return ret;
	}
	
	// -------------------------------------------- //
	// FACTION ONLINE STATE
	// -------------------------------------------- //
	
	// used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader()
	{
		if (!this.isNormal()) return;
		if (this.getFlag(MFlag.getFlagPermanent()) && MConf.get().permanentFactionsDisableLeaderPromotion) return;
		
		MPlayer oldLeader = this.getLeader();
		
		// get list of officers, or list of normal members if there are no officers
		List<MPlayer> replacements = this.getMPlayersWhereRole(Rel.OFFICER);
		if (replacements == null || replacements.isEmpty())
		{
			replacements = this.getMPlayersWhereRole(Rel.MEMBER);
		}
		
		if (replacements == null || replacements.isEmpty())
		{
			// faction leader is the only member; one-man faction
			if (this.getFlag(MFlag.getFlagPermanent()))
			{
				if (oldLeader != null)
				{
					// TODO: Where is the logic in this? Why MEMBER? Why not LEADER again? And why not OFFICER or RECRUIT?
					oldLeader.setRole(Rel.MEMBER);
				}
				return;
			}
			
			// no members left and faction isn't permanent, so disband it
			if (MConf.get().logFactionDisband)
			{
				Factions.get().log("The faction " + this.getName() + " (" + this.getId() + ") has been disbanded since it has no members left.");
			}
			
			for (MPlayer mplayer : MPlayerColl.get().getAllOnline())
			{
				mplayer.msg("<i>The faction %s<i> was disbanded.", this.getName(mplayer));
			}
			
			this.detach();
		}
		else
		{
			// promote new faction leader
			if (oldLeader != null)
			{
				oldLeader.setRole(Rel.MEMBER);
			}
			
			replacements.get(0).setRole(Rel.LEADER);
			this.msg("<i>Faction leader <h>%s<i> has been removed. %s<i> has been promoted as the new faction leader.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
			Factions.get().log("Faction " + this.getName() + " (" + this.getId() + ") leader was removed. Replacement leader: " + replacements.get(0).getName());
		}
	}
	
	public boolean isAllMPlayersOffline()
	{
		return this.getMPlayersWhereOnline(true).size() == 0;
	}
	
	public boolean isAnyMPlayersOnline()
	{
		return !this.isAllMPlayersOffline();
	}
	
	public boolean isFactionConsideredOffline()
	{
		return this.isAllMPlayersOffline();
	}
	
	public boolean isFactionConsideredOnline()
	{
		return !this.isFactionConsideredOffline();
	}
	
	// -------------------------------------------- //
	// MESSAGES
	// -------------------------------------------- //
	// These methods are simply proxied in from the Mixin.
	
	// CONVENIENCE SEND MESSAGE
	
	public boolean isExplosionsAllowed()
	{
		boolean explosions = this.getFlag(MFlag.getFlagExplosions());
		boolean offlineexplosions = this.getFlag(MFlag.getFlagOfflineexplosions());
		
		if (explosions && offlineexplosions) return true;
		if (!explosions && !offlineexplosions) return false;
		
		boolean online = this.isFactionConsideredOnline();
		
		return (online && explosions) || (!online && offlineexplosions);
	}
	
	public boolean sendMessage(Object message)
	{
		return MixinMessage.get().messagePredicate(new PredicateCommandSenderFaction(this), message);
	}
	
	public boolean sendMessage(Object... messages)
	{
		return MixinMessage.get().messagePredicate(new PredicateCommandSenderFaction(this), messages);
	}
	
	// CONVENIENCE MSG
	
	public boolean sendMessage(Collection<Object> messages)
	{
		return MixinMessage.get().messagePredicate(new PredicateCommandSenderFaction(this), messages);
	}
	
	public boolean msg(String msg)
	{
		return MixinMessage.get().msgPredicate(new PredicateCommandSenderFaction(this), msg);
	}
	
	public boolean msg(String msg, Object... args)
	{
		return MixinMessage.get().msgPredicate(new PredicateCommandSenderFaction(this), msg, args);
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public boolean msg(Collection<String> msgs)
	{
		return MixinMessage.get().msgPredicate(new PredicateCommandSenderFaction(this), msgs);
	}

	private MassiveMap<UUID, Rel> roster = new MassiveMap<>();
	private int rosterKicksRemaining = MConf.get().defaultRosterKicks;

	public void setRoster(MassiveMap<UUID, Rel> roster) {
		this.roster = roster;
		this.changed();
	}

	public MassiveMap<UUID, Rel> getRoster() {
		return roster;
	}

	public void addToRoster(UUID uuid, Rel rel) {
		this.roster.put(uuid, rel);
		this.changed();
	}

	public void removeFromRoster(UUID uuid) {
		this.roster.remove(uuid);
		this.changed();
	}

	public int getRosterKicksRemaining() {
		return rosterKicksRemaining;
	}

	public void setRosterKicksRemaining(int rosterKicksRemaining) {
		this.rosterKicksRemaining = rosterKicksRemaining;
	}

	public long getFactionValue() {
		MassiveMapDef<String, FactionValue> data;

		if (TaskFactionTopCalculate.get().isRunning()) {
			data = (MassiveMapDef<String, FactionValue>) FactionTopData.get().getBackupFactionValues().clone();
		} else {
			data = (MassiveMapDef<String, FactionValue>) FactionTopData.get().getFactionValues().clone();
		}

		List<String> dataKeys = new ArrayList<>(data.keySet());

		int factionPlace = (dataKeys.indexOf(this.getId()) + 1);

		List<FactionValue> valueKeys = new ArrayList<>(data.values());

		if (valueKeys.size() >= factionPlace) {
			FactionValue factionValue = valueKeys.get(factionPlace - 1);

			if (factionValue != null) {
				return factionValue.getTotalSpawnerValue();
			}
		}

		return 0;
	}

	// FACTION SANDBOTS
	private MassiveMap<Integer, Sandbot> sandbots = new MassiveMap<>();

	public MassiveMap<Integer, Sandbot> getSandbots() {
		return sandbots;
	}

	public void setSandbots(MassiveMap<Integer, Sandbot> sandbots) {
		this.sandbots = sandbots;
		this.changed();
	}

	public void addSandbot(Integer slot, Sandbot sandbot) {
		this.sandbots.put(slot, sandbot);
		this.changed();
	}

	public void despawnSandbot(Sandbot sandbot) {
		NPC byUniqueId = CitizensAPI.getNPCRegistry().getByUniqueId(sandbot.getUniqueId());

		if (byUniqueId != null) {
			byUniqueId.despawn();
			byUniqueId.destroy();
		}

		sandbot.setUuid(null);
		sandbot.setDespawned(true);
		this.changed();
	}

	public void spawnSandbot(Sandbot sandbot, PS ps) {
		if (sandbot.getUniqueId() != null || sandbot.getPs() != null || !sandbot.isDespawned()) {
			despawnSandbot(sandbot);
		}

		Location npcLocation = ps.asBukkitLocation().clone();

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, LangConf.get().sandbotName);
		npc.data().set("removefromplayerlist", false);
		npc.setFlyable(true);
		npc.setProtected(true);
		Gravity gravity = CitizensAPI.getTraitFactory().getTrait("gravity");
		gravity.gravitate(true);
		npc.addTrait(gravity);
		Owner owner = CitizensAPI.getTraitFactory().getTrait("owner");
		owner.setOwner(getLeader().getName(), getLeader().getUuid());
		npc.addTrait(owner);
		npc.data().setPersistent("removefromplayerlist", false);
		npc.setProtected(true);

		npc.data().set("cached-skin-uuid-name", "null");
		npc.data().set("player-skin-name", "null");
		npc.data().set("cached-skin-uuid", UUID.randomUUID().toString());
		npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, "ewogICJ0aW1lc3RhbXAiIDogMTU4OTEzMTEwMzI5MCwKICAicHJvZmlsZUlkIiA6ICI5MWYwNGZlOTBmMzY0M2I1OGYyMGUzMzc1Zjg2ZDM5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdG9ybVN0b3JteSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83OTJlNzkwZTRjY2QyMTQ3MzI5OGVkZmMwNjViZTc0MWYxZmIzYzUwYzBhYjRhOTk4NjRmZjM3ZDc2ZjAzODA1IgogICAgfQogIH0KfQ==");
		npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, "i1jagW+HbpdrBSw5eCTWyq9r3I6Gw1HyzQ+cpfi+4QtzOQYp/Pd2grInH3rkV3SCTkWXhGfUbUC+48h8aWo9i6806XkRIXYDBu2Mj0oc3y51bkmf+FkCT2Zpu3Ub/6XHp38EFqYxsgFXVQoEYPKsVp6RVJNTPr5mW2P2pk6udDjO9gzEmHvObwd38WJzkdlfWuNDIUdOs6AhKSm6SNw+XGkswDBV1A2TrkI2xZuVu6GsRGklaWGApGG5oSq70tVA+W1dZh8s2l6/EMBYbOFiQTrwzw3m2JAv2xbqbRxt1qOD/dVmugESShAnUyKHAGEiNpFEJ3UqvLbM7dXi4aStUOevFmkcBTGGJyZjd4Nr7cWRPNGWisV+1FjGZUEKGLo+DPZoh9kpuWrSgiMW/1OoGC8RlolATdGRLe5FL16a4P9msmrZ70aoJx3J8mAxjliux7ThCG9mI8O03787t2FDwA+Kc8e7gVBu9wJBJAUVSv0X/o9MLJoVvaE1/Yf92ByAATPWIpJM7q20u8fT+osMOXkg07e3Pd1L5ITZWV8J0a4ImO7RFanLCCC2KrAgqSrZ+T2pF/AloCbYZG0kDauZ4+DyAg2s5gOo9dT/1gyKzXTy+ESlKsSG+/2A9fEMsdV9WI96WLFys/WND/WEtg6UsIFHz1FPSpzXi9uKQNczBDs=");
		npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);

		npc.spawn(npcLocation);
		npc.getEntity().setVelocity(npc.getEntity().getVelocity().add(new Vector(0.0, 0.0, 0.0)));

		sandbot.setDespawned(false);
		sandbot.setUuid(npc.getUniqueId().toString());
		sandbot.setLoc(ps);
		this.changed();
	}

	public boolean isSandbot(org.bukkit.entity.Entity entity) {
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);

		if (npc == null) return false;

		for (Sandbot sandbot : getSandbots().values()) {
			if (npc.getUniqueId().equals(sandbot.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	public Sandbot getSandbot(UUID uuid) {
		for (Sandbot sandbot : getSandbots().values()) {
			if (sandbot.getUniqueId().equals(uuid)) {
				return sandbot;
			}
		}
		return null;
	}

}