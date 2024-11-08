package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Material;

import java.util.List;

public class GuiConf extends Entity<GuiConf> {
    private static GuiConf i = new GuiConf();

    public static GuiConf get() {
        return i;
    }
    public static void set(GuiConf newI) { i = newI; }

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

    public Material pageableGuiNextPageIconMaterial = Material.STAINED_GLASS_PANE;
    public Integer pageableGuiNextPageIconDurability = 14;
    public String pageableGuiNextPageIconName = "&7Next Page";

    public Material pageableGuiPreviousPageIconMaterial = Material.STAINED_GLASS_PANE;
    public Integer pageableGuiPreviousPageIconDurability = 14;
    public String pageableGuiPreviousPageIconName = "&7Previous Page";
    public String warpGuiPanelWarpName = "&c%warpName%";
    public List<String> warpGuiPanelFormat = MUtil.list(" ", "&7World &8» &c%world%", "&7X &8» &c%x%", "&7Y &8» &c%y%", "&7Z &8» &c%z%", " ", "&7Password &8» %password%", " ", "&7Click to warp.");

    public String tntStickName = "&eTNT Fill Tool";
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
    public String pendingChangeGuiName = "&b&lPending Shield Change";
    public List<String> pendingChangeGuiLoreNoChange = MUtil.list("", "&cThere is no pending change to", "&cyour faction's Shield time period");
    public List<String> pendingChangeGuiLoreChangeAfterDisable = MUtil.list("", "&cYour faction has recently disabled their", "&cshield. You can reselect a time period in", "&c%time%");
    public List<String> pendingChangeGuiLoreChangeActive = MUtil.list("", "&fYour faction's Shield time period", "&fwill be updated to &e%startHour% &f---> &e%endHour%", "&fin &e%time%");

    public String shieldInformationGuiName = "&e&lShield Information";
    public List<String> shieldInformationGuiLore = MUtil.list("", "&fDuring Shield, a faction cannot", "&fbe raided by cannons, nor raid others", "", "&fChanging the Shield time period takes", "&e1 day &fto update", "", "&cAbuse of this mechanic in any way", "&cwill be punished severely");
    public String disableShieldGuiItemName = "&c&lDisable Shield";
    public List<String> disableShieldGuiItemLore = MUtil.list("", "&fYou will need to wait &e1 day", "&fto reactivate it");
    public String strikePagerName = "&6Strikes for %faction%";

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
    public boolean sandbotGuiBorderGlassEnabled = true;
    public int sandbotGuiBorderGlassDurabilityId = 7;
    public boolean sandbotGuiBorderGlassGlowEnabled = false;

    public String sandbotSpawnButtonName = "&a&lSpawn Sand Bot #%sandbotNumber%";
    public String sandbotSpawnButtonHeadData = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQ1YzlhY2VhOGRhNzFiNGYyNTJjZDRkZWI1OTQzZjQ5ZTdkYmMwNzY0Mjc0YjI1YTZhNmY1ODc1YmFlYTMifX19";
    public String sandbotInfoButtonName = "&e&lSand Bot #%sandbotNumber%";
    public String sandbotInfoButtonHeadData = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ3ZTg2NGNlODliMGY2NGVmZDMxM2NlMjU4N2NiYjRlNjVkM2RmMThiMmExMjNkYzJhZjJlNTY2ZDAifX19";
    public String sandbotDespawnButtonName = "&c&lDespawn Sand Bot #%sandbotNumber%";
    public String sandbotDespawnButtonHeadData = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ1ZmNkMzEyODdkNjNlNzgyNmVhNzYwYTdlZDE1NGY2ODVkZmRjN2YzNDY1NzMyYTk2ZTYxOWIyZTEzNDcifX19";

    public List<String> sandbotInfoButtonLoreInactive = MUtil.list("&fStatus: &cInactive");
    public List<String> sandbotInfoButtonLoreActive = MUtil.list("&fStatus: &aActive");

}
