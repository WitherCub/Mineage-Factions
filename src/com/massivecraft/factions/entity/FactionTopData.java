package com.massivecraft.factions.entity;

import com.massivecraft.factions.entity.objects.FactionValue;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.collections.MassiveMapDef;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.IntStream;

@EditorName("config")
public class FactionTopData extends Entity<FactionTopData> {
	private static FactionTopData i;
	public static void set(FactionTopData newI) { i = newI; }

	public MassiveMapDef<String, FactionValue> factionValues = new MassiveMapDef<>();
	public MassiveMapDef<String, FactionValue> backupFactionValues = new MassiveMapDef<>();
	public MassiveMapDef<String, FactionValue> factionValuesPtop = new MassiveMapDef<>();
	public NavigableMap<Long, MassiveMap<String, Long>> historicTotals = new TreeMap<>();
	private long lastRunTime = System.currentTimeMillis();
	private HashMap<String, Long> placeTimes = new HashMap<>();

	public static FactionTopData get() {
		return i;
	}

	public MassiveMapDef<String, FactionValue> getFactionValuesPtop() {
		return factionValuesPtop;
	}

	public void setFactionValuesPtop(MassiveMapDef<String, FactionValue> factionValuesPtop) {
		this.factionValuesPtop = factionValuesPtop;
		this.changed();
	}

	public void setHistoricTotals(NavigableMap<Long, MassiveMap<String, Long>> historicTotals) {
		this.historicTotals = historicTotals;
	}

	@Override
	public FactionTopData load(FactionTopData that) {
		super.load(that);

		this.setFactionValues(that.factionValues);
		this.setBackupFactionValues(that.backupFactionValues);
		this.setLastRunTime(that.lastRunTime);
		this.setHistoricTotals(that.historicTotals);
		this.setPlaceTimes(that.placeTimes);
		this.setFactionValuesPtop(that.factionValuesPtop);

		return this;
	}

	public MassiveMapDef<String, FactionValue> getFactionValues() {
		return factionValues;
	}

	public void setFactionValues(MassiveMapDef<String, FactionValue> factionValues) {
		this.factionValues.putAll(factionValues);
	}

	public void sortData() {
		backupData();

		List<FactionValue> factionValues = new ArrayList<>(getFactionValues().values());
		factionValues.sort((o1, o2) -> Long.compare(o2.getTotalSpawnerValue(), o1.getTotalSpawnerValue()));

		long now = System.currentTimeMillis();

		MassiveMap<String, Long> worth = new MassiveMap<>();

		for (String string : this.factionValues.keySet()) {
			worth.put(string, this.factionValues.get(string).getTotalSpawnerValue());
		}

		this.historicTotals.put(now, worth);
		this.historicTotals.entrySet().removeIf(entry -> now - entry.getKey() > 129600000L);

		this.factionValues.clear();

		factionValues.forEach(value -> this.factionValues.put(value.getFactionID(), value));

		if (LangConf.get().ftopKnockMsgEnabled && this.factionValues.size() > 1 && this.backupFactionValues.size() > 1) {
			IntStream.range(0, MConf.get().ftopKnockTopXPositions).forEach(i -> {
				String faction = ((i >= factionValues.size()) ? null : getByIndex(this.factionValues, i).getFactionID());
				String last = ((i >= backupFactionValues.size()) ? null : getByIndex(this.backupFactionValues, i).getFactionID());
				if (faction != null && last != null && !faction.equals(last) && (getIndexOf(this.backupFactionValues, faction) == -1 || getIndexOf(this.backupFactionValues, faction) >= factionValues.indexOf(this.factionValues.get(faction)))) {
					Faction newFaction = Faction.get(faction);
					Faction lastFaction = Faction.get(last);

					if (newFaction != null && lastFaction != null) {
						for (String line : LangConf.get().ftopKnockMsg) {
							Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{newfaction}", newFaction.getName()).replace("{oldfaction}", lastFaction.getName()).replace("{rank}", String.valueOf(i + 1)));
						}
					}
				}
			});
		}

		this.backupFactionValues.clear();
		factionValues.forEach(value -> this.backupFactionValues.put(value.getFactionID(), value));

		this.sortPtop(false);

		this.changed();
	}

	public void sortPtop(boolean markChanged) {
		this.factionValuesPtop.clear();

		List<FactionValue> factionValuesPtop = new ArrayList<>(getFactionValues().values());
		factionValuesPtop.sort((o1, o2) -> Long.compare(o2.getTotalPotentialValue(), o1.getTotalPotentialValue()));
		factionValuesPtop.forEach(value -> this.factionValuesPtop.put(value.getFactionID(), value));

		if (markChanged) {
			this.changed();
		}
	}

	public long getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(long lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	public void backupData() {
		factionValues.forEach((key, value) -> backupFactionValues.put(key, value));
	}

	public MassiveMapDef<String, FactionValue> getBackupFactionValues() {
		return backupFactionValues;
	}

	public void setBackupFactionValues(MassiveMapDef<String, FactionValue> backupFactionValues) {
		this.backupFactionValues.putAll(backupFactionValues);
	}

	public void addFaction(String id) {
		factionValues.put(id, new FactionValue(id, new MassiveSet<>(), 0, 0));
		this.changed();
	}

	public void removeFaction(String id) {
		if (factionValues.containsKey(id)) {
			factionValues.remove(id);
			this.changed();
		}
	}

	public Long getNearestTotal(long time, Faction faction) {
		Long before = this.historicTotals.floorKey(time);
		Long after = this.historicTotals.ceilingKey(time);

		Long key;

		if (before == null) {
			key = after;
		} else if (after == null) {
			key = before;
		} else {
			key = (((((time - before) < (after - time)) || ((after - time) < 0L)) && ((time - before) > 0L)) ? before : after);
		}

		return key == null ? null : this.historicTotals.get(key).get(faction.getId());
	}

	public HashMap<String, Long> getPlaceTimes() {
		return placeTimes;
	}

	public void setPlaceTimes(HashMap<String, Long> placeTimes) {
		this.placeTimes = placeTimes;
		this.changed();
	}

	public void addPlacetime(String loc, long time) {
		this.placeTimes.put(loc, time);
		this.changed();
	}

	public void removePlacetime(String loc) {
		this.placeTimes.remove(loc);
		this.changed();
	}

	private int getIndexOf(MassiveMapDef<String, FactionValue> hMap, String factionId) {
		return new ArrayList<>(hMap.keySet()).indexOf(factionId);
	}

	private FactionValue getByIndex(MassiveMapDef<String, FactionValue> hMap, int index) {
		return (FactionValue) hMap.values().toArray()[index];
	}

}