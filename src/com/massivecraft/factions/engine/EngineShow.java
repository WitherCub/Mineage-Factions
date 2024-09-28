package com.massivecraft.factions.engine;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.CmdFactionsShield;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.comparator.ComparatorMPlayerRole;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.RaidtimerShield;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsFactionShowAsync;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.upgrades.Upgrade;
import gg.halcyon.upgrades.UpgradesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EngineShow extends Engine
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final String BASENAME = "factions";
	public static final String BASENAME_ = BASENAME + "_";
	
	public static final String SHOW_ID_FACTION_ID = BASENAME_ + "id";
	public static final String SHOW_ID_FACTION_DESCRIPTION = BASENAME_ + "description";
	public static final String SHOW_ID_FACTION_DISCORD = BASENAME_ + "discord";
	public static final String SHOW_ID_FACTION_AGE = BASENAME_ + "age";
	public static final String SHOW_ID_FACTION_UPGRADES = BASENAME_ + "upgrades";
	public static final String SHOW_ID_FACTION_SHIELD = BASENAME_ + "shield";
	public static final String SHOW_ID_FACTION_FLAGS = BASENAME_ + "flags";
	public static final String SHOW_ID_FACTION_POWER = BASENAME_ + "power";
	public static final String SHOW_ID_FACTION_LANDVALUES = BASENAME_ + "landvalue";
	public static final String SHOW_ID_FACTION_BANK = BASENAME_ + "bank";
	public static final String SHOW_ID_FACTION_RELATIONS = BASENAME_ + "allies";
	public static final String SHOW_ID_FACTION_FOLLOWERS = BASENAME_ + "followers";
	public static final String SHOW_ID_FACTION_STRIKES = BASENAME_ + "strikes";


	public static final int SHOW_PRIORITY_FACTION_ID = 1000;
	public static final int SHOW_PRIORITY_FACTION_DESCRIPTION = 2000;
	public static final int SHOW_PRIORITY_FACTION_DISCORD = 2100;
	public static final int SHOW_PRIORITY_FACTION_AGE = 3000;
	public static final int SHOW_PRIORITY_FACTION_UPGRADES = 3100;
	public static final int SHOW_PRIORITY_FACTION_SHIELD = 3200;
	public static final int SHOW_PRIORITY_FACTION_FLAGS = 4000;
	public static final int SHOW_PRIORITY_FACTION_POWER = 5000;
	public static final int SHOW_PRIORITY_FACTION_LANDVALUES = 6000;
	public static final int SHOW_PRIORITY_FACTION_BANK = 7000;
	public static final int SHOW_PRIORITY_FACTION_RELATIONS = 9000;
	public static final int SHOW_PRIORITY_FACTION_FOLLOWERS = 10000;
	public static final int SHOW_PRIORITY_FACTION_STRIKES = 3310;

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineShow i = new EngineShow();
	
	public static EngineShow get()
	{
		return i;
	}
	
	// -------------------------------------------- //
	// FACTION SHOW
	// -------------------------------------------- //
	
	public static String show(String key, String value)
	{
		return Txt.parse("<a>%s: <i>%s", key, value);
	}
	
	public static Mson show(String key, Mson value)
	{
		return Mson.mson(Txt.parse("<a>%s: <i>", key)).add(value);
	}
	
	public static PriorityLines show(int priority, String key, String value)
	{
		return new PriorityLines(priority, show(key, value));
	}
	
	public static PriorityLines show(int priority, String key, Mson value)
	{
		return new PriorityLines(priority, show(key, value));
	}
	
	public static void show(Map<String, PriorityLines> idPriorityLiness, String id, int priority, String key, Mson value)
	{
		idPriorityLiness.put(id, show(priority, key, value));
	}
	
	public static void show(Map<String, PriorityLines> idPriorityLiness, String id, int priority, String key, String value)
	{
		idPriorityLiness.put(id, show(priority, key, value));
	}
	
	public static List<String> table(List<String> strings, int cols)
	{
		List<String> ret = new ArrayList<>();
		
		StringBuilder row = new StringBuilder();
		int count = 0;
		
		Iterator<String> iter = strings.iterator();
		while (iter.hasNext())
		{
			String string = iter.next();
			row.append(string);
			count++;
			
			if (iter.hasNext() && count != cols)
			{
				row.append(Txt.parse(" <i>| "));
			}
			else
			{
				ret.add(row.toString());
				row = new StringBuilder();
				count = 0;
			}
		}
		
		return ret;
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFactionShow(EventFactionsFactionShowAsync event)
	{
		final int tableCols = 4;
		final CommandSender sender = event.getSender();
		final MPlayer mplayer = event.getMPlayer();
		final Faction faction = event.getFaction();
		final boolean normal = faction.isNormal();
		final boolean peaceful = faction.getFlag(MFlag.getFlagPeaceful());
		final Map<String, PriorityLines> idPriorityLiness = event.getIdPriorityLiness();
		String none = Txt.parse("<silver><italic>none");
		
		// ID
		if (mplayer.isOverriding())
		{
			show(idPriorityLiness, SHOW_ID_FACTION_ID, SHOW_PRIORITY_FACTION_ID, "ID", faction.getId());
		}
		
		// DESCRIPTION
		show(idPriorityLiness, SHOW_ID_FACTION_DESCRIPTION, SHOW_PRIORITY_FACTION_DESCRIPTION, "Description", faction.getDescriptionDesc());
		
		// DISCORD
		if (faction.getFactionDiscord() != null)
		{
			show(idPriorityLiness, SHOW_ID_FACTION_DISCORD, SHOW_PRIORITY_FACTION_DISCORD, "Discord", faction.getFactionDiscord());
		}
		
		// SECTION: NORMAL
		if (normal)
		{
			// AGE
			long ageMillis = System.currentTimeMillis() - faction.getCreatedAtMillis();
			show(idPriorityLiness, SHOW_ID_FACTION_AGE, SHOW_PRIORITY_FACTION_AGE, "Age", TimeUtil.formatTime(ageMillis));

			show(idPriorityLiness, SHOW_ID_FACTION_STRIKES, SHOW_PRIORITY_FACTION_STRIKES, "Strikes", String.valueOf(faction.getFactionWarnings().size()));

			// FLAGS
			// We display all editable and non default ones. The rest we skip.
			List<String> flagDescs = new LinkedList<>();
			for (Entry<MFlag, Boolean> entry : faction.getFlags().entrySet())
			{
				final MFlag mflag = entry.getKey();
				if (mflag == null) continue;
				
				final Boolean value = entry.getValue();
				if (value == null) continue;
				
				if (!mflag.isInteresting(value)) continue;
				
				String flagDesc = Txt.parse(value ? "<g>" : "<b>") + mflag.getName();
				flagDescs.add(flagDesc);
			}
			String flagsDesc = Txt.parse("<silver><italic>default");
			if (!flagDescs.isEmpty())
			{
				flagsDesc = Txt.implode(flagDescs, Txt.parse(" <i>| "));
			}
			show(idPriorityLiness, SHOW_ID_FACTION_FLAGS, SHOW_PRIORITY_FACTION_FLAGS, "Flags", flagsDesc);
			
			// POWER
			double powerBoost = faction.getPowerBoost();
			String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
			String powerDesc = Txt.parse("%s/%d/%d%s", mplayer.getFaction() == faction || MConf.get().showFactionLandClaimedValues ? String.valueOf(faction.getLandCount()) : "???", faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);
			show(idPriorityLiness, SHOW_ID_FACTION_POWER, SHOW_PRIORITY_FACTION_POWER, "Land / Power / Maxpower", powerDesc);
			
			// UPGRADES
			
			int unlockedUpgradesAmount = faction.getUpgrades().values().stream().mapToInt(value -> value).sum();
			
			int maxUpgradesAmount = 0;
			
			for (Upgrade upgrade : UpgradesManager.get().getUpgrades())
			{
				maxUpgradesAmount = maxUpgradesAmount + upgrade.getMaxLevel();
			}
			
			List<String> upgradesLore = new ArrayList<>();
			upgradesLore.add(Txt.parse("<a><bold>Upgrade progress"));
			upgradesLore.add("");
			
			for (Upgrade upgrade : UpgradesManager.get().getUpgrades())
			{
				upgradesLore.add(Txt.parse("%s<reset>: <i>%s <reset>/ <i>%s", faction.getLevel(upgrade.getUpgradeName()) == upgrade.getMaxLevel() ? ChatColor.GREEN + ChatColor.stripColor(upgrade.getUpgradeName()) : ChatColor.GRAY + ChatColor.stripColor(upgrade.getUpgradeName()), faction.getLevel(upgrade.getUpgradeName()), upgrade.getMaxLevel()));
			}
			
			show(idPriorityLiness, SHOW_ID_FACTION_UPGRADES, SHOW_PRIORITY_FACTION_UPGRADES, "Upgrades unlocked", Mson.mson(Txt.parse("<i>%s / %s", unlockedUpgradesAmount, maxUpgradesAmount)).tooltip(upgradesLore));

			// SHIELD
			Mson shieldMson;

			RaidtimerShield raidtimerShield = RaidDataStorage.get().getFactionRaidtimerShield(faction);

			if (raidtimerShield != null)
			{
				shieldMson = Mson.mson(Txt.parse("&a&lPHASE 3"));
			}
			else if (faction.getShieldedHoursStartTime() == null) {
				shieldMson = Mson.mson(Txt.parse("&c&lDISABLED"));
			} else if (faction.isShielded()) {
				shieldMson = Mson.mson(Txt.parse("&a&lACTIVE"));
			} else {
				shieldMson = Mson.mson(Txt.parse("&7&lINACTIVE"));
			}

			List<String> shieldLore = new ArrayList<>();

			shieldLore.add(Txt.parse("&c&lShield"));
			shieldLore.add("");

			Date date = new Date();
			date.setHours(TimeUtil.getTimeHours());
			date.setMinutes(TimeUtil.getTimeMinutes());
			date.setSeconds(TimeUtil.getTimeSeconds());

			if (raidtimerShield != null)
			{
				shieldLore.add(Txt.parse("&fRAIDTIMER SHIELD"));
				shieldLore.add("");
				shieldLore.add(Txt.parse("&fRemaining: &e%time% left".replace("%time%", TimeUtil.formatPlayTime((MConf.get().phaseThreeLastsXMinutes * TimeUnit.MILLIS_PER_MINUTE) - (System.currentTimeMillis() - raidtimerShield.getShieldStartMillis())).trim())));
				shieldLore.add("");
			}
			else if (faction.getShieldedHoursStartTime() == null)
			{
				shieldLore.add(Txt.parse("&fShield window: &cnone"));
				shieldLore.add("");
				shieldLore.add(Txt.parse("&fPending Shield: &cnone"));
			} else {
				String startHourFormatted = CmdFactionsShield.get().getTimeFormatted(faction.getShieldedHoursStartTime(), 0);
				String endHourFormatted = CmdFactionsShield.get().getTimeFormatted(MConf.get().shieldStartEndHours.get(faction.getShieldedHoursStartTime()), 0);

				shieldLore.add(Txt.parse("&fShield window: &e%startTime% &f---> &e%endTime%".replace("%startTime%", startHourFormatted).replace("%endTime%", endHourFormatted)));
				shieldLore.add("");

				if (faction.getShieldedHoursChangeRequestNewStartTime() != null) {
					String pendingStartHourFormatted = CmdFactionsShield.get().getTimeFormatted(faction.getShieldedHoursChangeRequestNewStartTime(), 0);
					String pendingEndHourFormatted = CmdFactionsShield.get().getTimeFormatted(MConf.get().shieldStartEndHours.get(faction.getShieldedHoursChangeRequestNewStartTime()), 0);
					shieldLore.add(Txt.parse("&fPending Shield: &e%startTime% &f---> &e%endTime%".replace("%startTime%", pendingStartHourFormatted).replace("%endTime%", pendingEndHourFormatted)));
				} else {
					shieldLore.add(Txt.parse("&fPending Shield: &cnone"));
				}
			}

			shieldLore.add("");
			shieldLore.add(Txt.parse("&fCurrent time:"));
			String currentTime = CmdFactionsShield.get().getTimeFormatted(date.getHours(), date.getMinutes());
			shieldLore.add(Txt.parse("&e%time%".replace("%time%", currentTime)));

			show(idPriorityLiness, SHOW_ID_FACTION_SHIELD, SHOW_PRIORITY_FACTION_SHIELD, "Shield", shieldMson.tooltip(shieldLore));

			// SECTION: ECON
			if (Econ.isEnabled())
			{
				// LANDVALUES
				List<String> landvalueLines = new LinkedList<>();
				long landCount = faction.getLandCount();
				for (EventFactionsChunkChangeType type : EventFactionsChunkChangeType.values())
				{
					Double money = MConf.get().econChunkCost.get(type);
					if (money == null) continue;
					if (money == 0) continue;
					money *= landCount;
					
					String word = "Cost";
					if (money <= 0)
					{
						word = "Reward";
						money *= -1;
					}
					
					String key = Txt.parse("Total Land %s %s", type.toString().toLowerCase(), word);
					String value = Txt.parse("<h>%s", Money.format(money));
					String line = show(key, value);
					landvalueLines.add(line);
				}
				idPriorityLiness.put(SHOW_ID_FACTION_LANDVALUES, new PriorityLines(SHOW_PRIORITY_FACTION_LANDVALUES, landvalueLines));
				
				// BANK
				if (MConf.get().bankEnabled)
				{
					double bank = Money.get(faction);
					String bankDesc = Txt.parse("<h>%s", Money.format(bank, true));
					show(idPriorityLiness, SHOW_ID_FACTION_BANK, SHOW_PRIORITY_FACTION_BANK, "Bank", bankDesc);
				}
			}
			
			// RELATIONS
			List<String> relationLines = new ArrayList<String>();
			String everyone = MConf.get().colorTruce.toString() + Txt.parse("<italic>*EVERYONE*");
			Set<Rel> rels = EnumSet.of(Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL);
			Map<Rel, List<String>> relNames = FactionColl.get().getRelationNames(faction, rels);
			for (Entry<Rel, List<String>> entry : relNames.entrySet())
			{
				Rel rel = entry.getKey();
				List<String> names = entry.getValue();
				if (!names.isEmpty())
				{
					String header = Txt.parse("<a>%s with: %s", Txt.getNicedEnum(rel), (rel == Rel.TRUCE && peaceful) ? everyone : String.join(", ", table(names, tableCols)));
					relationLines.add(header);
				}
			}
			idPriorityLiness.put(SHOW_ID_FACTION_RELATIONS, new PriorityLines(SHOW_PRIORITY_FACTION_RELATIONS, relationLines));
		}
		
		// FOLLOWERS
		List<String> followerLines = new ArrayList<>();
		
		List<String> followerNamesOnline = new ArrayList<>();
		List<String> followerNamesOffline = new ArrayList<>();
		List<String> altNamesOnline = new ArrayList<>();
		List<String> altNamesOffline = new ArrayList<>();
		
		List<MPlayer> followers = faction.getMPlayers();
		followers.sort(ComparatorMPlayerRole.get());
		
		for (MPlayer follower : followers)
		{
			if (follower.isAlt())
			{
				if (follower.isOnline(sender))
				{
					altNamesOnline.add(follower.getNameAndTitle(mplayer));
				}
				else if (normal)
				{
					// For the non-faction we skip the offline members since they are far to many (infinite almost)
					altNamesOffline.add(follower.getNameAndTitle(mplayer));
				}
			}
			else
			{
				if (follower.isOnline(sender))
				{
					followerNamesOnline.add(follower.getNameAndTitle(mplayer));
				}
				else if (normal)
				{
					// For the non-faction we skip the offline members since they are far to many (infinite almost)
					followerNamesOffline.add(follower.getNameAndTitle(mplayer));
				}
			}
		}
		
		String headerOnline = Txt.parse("<a>Members Online (%s):", followerNamesOnline.size());
		followerLines.add(headerOnline);
		if (followerNamesOnline.isEmpty())
		{
			followerLines.add(none);
		}
		else
		{
			followerLines.addAll(table(followerNamesOnline, tableCols));
		}
		
		if (normal)
		{
			String headerOffline = Txt.parse("<a>Members Offline (%s):", followerNamesOffline.size());
			followerLines.add(headerOffline);
			if (followerNamesOffline.isEmpty())
			{
				followerLines.add(none);
			}
			else
			{
				followerLines.addAll(table(followerNamesOffline, tableCols));
			}
			
			if (!MConf.get().hideOtherFacAlts || faction.isSystemFaction())
			{
				String headerAltsOnline = Txt.parse("<a>Alts Online (%s):", altNamesOnline.size());
				followerLines.add(headerAltsOnline);
				if (altNamesOnline.isEmpty())
				{
					followerLines.add(none);
				}
				else
				{
					followerLines.addAll(table(altNamesOnline, tableCols));
				}
				
				String headerAltsOffline = Txt.parse("<a>Alts Offline (%s):", altNamesOffline.size());
				followerLines.add(headerAltsOffline);
				if (altNamesOffline.isEmpty())
				{
					followerLines.add(none);
				}
				else
				{
					followerLines.addAll(table(altNamesOffline, tableCols));
				}
			}
		}
		idPriorityLiness.put(SHOW_ID_FACTION_FOLLOWERS, new PriorityLines(SHOW_PRIORITY_FACTION_FOLLOWERS, followerLines));
	}
	
}
