package com.massivecraft.factions.entity;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.engine.EngineRaidtimer;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LangConf extends Entity<LangConf> {
    private static LangConf i = new LangConf();

    public static LangConf get() {
        return i;
    }
    public static void set(LangConf newI) { i = newI; }

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

    // -------------------------------------------- //
    // PREFIXES
    // -------------------------------------------- //

    // Here you may edit the name prefixes associated with different faction ranks.
    public String prefixLeader = "@";
    public String prefixColeader = "**";
    public String prefixOfficer = "*";
    public String prefixMember = "+";
    public String prefixRecruit = "-";

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
    public String reachedMaximumFillRadius = "&cYou have reached the max radius of 40.";
    public String cantFillLessThanOne = "&cYou can't tntfill less than 1 tnt!";
    public String removeInvisEffectMsg = "&cThe invisibility potion effect is disabled on this server.";
    // Missions
    public String missionAssignedMsg = "&7The &6%mission% &7mission is now active! You have &61 day &7to complete it.";
    public String completeMissionMsg = "&a&lCongratulations! &7Your faction has completed the &6%mission% &7mission and has been rewarded %rewardAmount% credits.";
    public List<String> missionsGuiMissionLore = MUtil.list("", "&7&lChallenge", "&8&l* &7%missionDescription%", "", "&7&lWorth", " &8&l* &7%creditsReward% Credits", "", "&7&lTime Remaining:", " &8&l* &7%timeRemaining%", "", "&7Complete: %requirementComplete%/%requirement%");
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
    public String alarmSoundedMsg = "&cALERT: &a%player% &ehas sounded the alarm! Get to the walls!";
    public String alarmDisabledMsg = "&cALERT: &eThe alarm has been disabled by &a%player%.";
    public String checkWallClearMsg = "&aYou have marked the walls as clear!";
    public String checkWallNotifsDisabled = "&cCheck notifications must be enabled in /f check for this command to function.";
    public String checkWallsNotifMsg = "&cALERT: &eThe walls have not been checked for over %notificationTime% minutes.";
    public String factionPaypalInvalidLengthMsg = "&cYour faction paypal name must be between %minCharacters% and %maxCharacters% characters long!";
    public String notValidPaypalMsg = "&cThat is not a valid paypal email!";
    public String paypalUpdatedPlayerMsg = "&eYour faction's paypal email has been updated to %paypal%";
    public String paypalUpdatedFactionMsg = "&e%player% has just updated your faction's paypal.";
    public String checkFactionPaypalMsg = "&e%factionName%'s Paypal: %paypal%";
    public String factionDiscordInvalidLengthMsg = "&cYour faction discord url must be between %minCharacters% and %maxCharacters% characters long!";
    public String discordUrlRemoved = "&eYour Discord URL has been removed.";
    public String discordUrlUpdated = "&eYour Discord URL has been updated.";
    public String notValidDiscordMsg = "&cThat is not a valid discord URL!";
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
    public String drainAmountLowerThanMinimumMsg = "&cThe smallest drain-to balance you can set is $%amount%";
    public String drainedMsg = "&aSuccessfully drained %membersDrained% Faction members for a total of $%totalDrained%";

    public String factionNameReservedMsg = "&cSorry, the faction named %faction% is currently reserved for %player%.";

    public String alreadyEnabledLoginNotifsMsg = "&cYou already have login notifications enabled.";
    public String loginNotifsNowOnMsg = "&eLogin notifications are now &aENABLED&e. You will now be notified when your faction members login.";
    public String alreadyDisabledLoginNotifsMsg = "&cYou already have login notifications disabled.";
    public String loginNotifsOffMsg = "&eLogin notifications are now &cDISABLED&e. You will no longer be notified when your faction members login.";
    public String shieldedHoursDisabledMsg = "%player% &ehas disabled %faction%&e's shielded hours.";
    public String changingShieldHoursOffMsg = "&cChanging shielded hours is currently disabled.";
    public String shieldHoursChangedMsg = "%player% &ehas set %faction%&e's shielded hours to &7%startHour% &f---> &7%endHour%";
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
    public String spawnerChunkMarkedAt = "&a%player% has marked a spawner chunk for your faction at &6Chunk X: &7%chunkX% &6ChunkZ: &7%chunkZ% &ain &7%world%&a.";
    public String pleaseWaitXSecondsToUseSpawnerchunkCmdAgainMsg = "&cPlease wait %seconds% before using this command again.";
    public String cannotUnmarkSpawnerChunkWithSpawnersInChunkMsg = "&cThere are still &c&l%spawnersInChunk% spawners left in this chunk. Please remove all spawners before attempting to unset a spawner chunk.";
    public String unmarkedSpawnerchunkMsg = "&aYou have successfully unmarked this chunk from being a spawner chunk.";
    public List<String> msgFactionBreached = MUtil.list(
            " ",
            "&c[BREACH] %factionRaiding% &ehas just breached %factionRaided% &eand has gained &a%amount% &evalue.",
            " "
    );
    public String addedStrikeMsg = "&aYou have added a faction strike to %faction%.";
    public String removeStrikeMsg = "&aYou have removed a faction strike from %faction% at their /f strikes index of %index%.";
    public String broadcastStrikeMsg = "&c&l[!] &c%faction% &7has been striked for &c%reason%";
    public String startOfPhaseThreeMsg = "&aThis is the start of Phase III. Your faction is now on grace for the next 30 minutes.";
    public String yourFactionHasNoActiveRaidMsg = "&aYour faction &eis currently not raiding anybody.";
    public String cmdRaidTimeRemainingMsg = "&aYour faction's &eraid on &c%otherFaction% &ehas &a%time% &eremaining. &7&l(&7Phase %phase%&7&l)";
    public String altsCantCheckRaidtimerStatusMsg = "&cFaction alts are not permitted to check raidtimer status.";
    public String raidHasEndedMsg = "&cYour raid on &c&l%faction% &chas ended as time has expired.";
    public String raidHasEndedDisbandMsg = "&cYour raid on &c&l%faction% &chas ended as they have disbanded.";
    public String alreadyBeingRaidedMsg = "&cThis faction is already being raided by %faction%!";
    public String raidStartedMsg = "&aYour faction &ehas started a raid on &c%faction%&e!";
    public String cantMineSpawnersDuringRaidtimerMsg = "&cYou are not permitted to mine spawners while you are on lockdown.";
    public String cantTogglePrinterLockdownMsg = "&cYou are not permitted to toggle printer during Phase 1 of lockdown.";
    public String cantPlaceExplosionMsg = "&cYou are not permitted to use explosions during lockdown.";
    public String backToPhaseOneMsg = "&aYour faction &ehas restarted the raid timer, back to Phase 1 of your raid on &c%faction%&e!";
    public String rosterKicksResetMsg = "&aYour faction's roster kicks have been reset!";
    public String notValidDefaultRankMsg = "&cThat is not a valid default rank.";
    public String systemFactionMsg = "&cYou can not modify the roster for a system faction.";
    public String notPermittedToModifyRosterMsg = "&cYour faction does not allow you to modify the roster.";
    public String playerAlreadyInRosterMsg = "&cThe specified player is already added to your faction's roster.";
    public String factionRosterFullMsg = "&cYour faction's roster is currently full.";
    public String playerAddedToRosterMsg = "&a%player% &7has been added to your faction roster.";
    public String modificationsToRosterNotEnabledMsg = "&cModifications to faction rosters have already been disabled.";
    public String playerNotInRosterMsg = "&cThe specified player is not currently on your faction's roster.";
    public String playerDefaultRankSetMsg = "&c%player% &7now has their default join rank set to %rel%.";
    public String notOnFactionRosterMsg = "&cYou are not on %faction%'s &croster.";
    public String playerRemovedFromRosterMsg = "&a%player% &7has been removed from your faction roster.";
    public String noRosterKicksRemainingMsg = "&cYour faction has no remaining roster kicks.";
    public String cantKickYourselfMsg = "&cYou can not kick yourself!";
    public String cantKickHigherRankMsg = "&cYou can not kick someone of a higher rank than you!";
    public String cannotClaimCornerMsg = "&cYou cannot claim this corner.";
    public String attemptingClaimCornerMsg = "&aAttempting to claim corner...";
    public String mustBeInCornerMsg = "&cYou must be in a corner to use this command.";
    public String couldNotClaimCornerMsg = "&7One or more &cclaims in this corner &7could not be claimed! Total chunks claimed: &c%claims%";
    public String claimedCornerMsg = "&aYou have claimed the corner successfully. &d%claims% &achunks have been claimed.";
    public String noRaidClaimsAvailableMsg = "&cYour faction currently has the maximum amount of raid claims set.";
    public String raidClaimSetMsg = "&aYou have created a raidclaim centered at your current location.";
    public String raidClaimRemovedMsg = "&aYour selected raidclaim has been removed.";

    public String mustUseRaidclaimTitleTopMessage = "&c&lWARNING";
    public String mustUseRaidclaimTitleBottomMessage = "&7You must use /f raidclaim to fire!";
    public String maxRaidclaimRadiusMsg = "&cYou are not permitted to claim a raidclaim with a radius larger than %maxRadius%.";
    public String sandbotDespawnDespawnedMsg = "&aDespawned Sandbot #%sandbotNumber%.";
    public String sandbotDespawnDontExistMsg = "&cYou can't despawn something that doesn't exist.";
    public String sandbotSpawnAlreadySpawnedMsg = "&cThis sandbot is already spawned.";
    public String spawnbotSandPurchaseSandbotMsg = "&cIn order to spawn sandbot #%sandbotNumber% you must unlock it using /f upgrade.";
    public String spawnbotMustBeInFactionLandMsg = "&cYou can only spawn sandbots in your land.";
    public String sandbotDespawnedNoMoneyInFbankMsg = "&cYour sandbots have been despawned due to running out of money in /f bank.";
    public String spawnbotSpawnedMsg = "&aSandbot spawned at your location.";
    public String sandbotName = "&c&lSandbot";

}
