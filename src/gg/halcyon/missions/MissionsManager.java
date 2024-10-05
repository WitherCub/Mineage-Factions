package gg.halcyon.missions;

import com.massivecraft.factions.engine.actions.ActionSpinMissions;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.Mission;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MissionsManager
{
	
	private static MissionsManager i = new MissionsManager();
	private ArrayList<Mission> missions = new ArrayList<>();
	
	public static MissionsManager get()
	{
		return i;
	}
	
	public ConfMission getConfMissionByName(String missionName)
	{
		return MissionUpgradeConf.get().challenges.stream().filter(confChallenge -> confChallenge.getMissionNameDontChange().equalsIgnoreCase(missionName)).findFirst().orElse(null);
	}
	
	public Inventory getMissionsInventory(Faction faction)
	{
		Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Txt.parse("&aMissions"));
		
		ChestGui chestGui = ChestGui.getCreative(inventory);
		chestGui.setAutoclosing(false);
		chestGui.setAutoremoving(false);
		chestGui.setSoundOpen(null);
		chestGui.setSoundClose(null);
		
		NumberFormat priceFormat = NumberFormat.getInstance();
		priceFormat.setGroupingUsed(true);
		
		chestGui.getInventory().setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(0).lore(""));
		chestGui.getInventory().setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(11).lore(""));
		chestGui.getInventory().setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(11).lore(""));
		chestGui.getInventory().setItem(4, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(0).lore(""));
		
		List<String> lore = new ArrayList<>();
		
		Mission activeMission = faction.getActiveMission();
		
		if (activeMission == null)
		{
			if (System.currentTimeMillis() - faction.getMissionStartTime() < MConf.get().missionTimerHours)
			{
				lore.add(Txt.parse("&cCannot start mission for:"));
				lore.add(Txt.parse("&7%s", TimeUtil.formatTime(MConf.get().missionTimerHours - (System.currentTimeMillis() - faction.getMissionStartTime()))));
				chestGui.getInventory().setItem(2, new ItemBuilder(Material.WATCH).name(Txt.parse("&aMission Spinner")).setLore(lore));
			}
			else
			{
				lore.add(Txt.parse("&eClick to pick a new mission"));
				chestGui.getInventory().setItem(2, new ItemBuilder(Material.WATCH).name(Txt.parse("&aMission Spinner")).setLore(lore));
				chestGui.setAction(2, new ActionSpinMissions(faction));
			}
			
			return chestGui.getInventory();
		}
		else
		{
			for (String s : LangConf.get().missionsGuiMissionLore)
			{
				lore.add(Txt.parse(s
									   .replace("%missionDescription%", activeMission.getDescription())
									   .replace("%creditsReward%", String.valueOf(activeMission.getCreditsReward()))
									   .replace("%timeRemaining%", TimeUtil.formatTime(MConf.get().missionTimerHours - (System.currentTimeMillis() - faction.getMissionStartTime())))
									   .replace("%requirementComplete%", priceFormat.format(faction.getMissionRequirementComplete()))
									   .replace("%requirement%", priceFormat.format(activeMission.getRequirement()))));
			}
			
			chestGui.getInventory().setItem(2, new ItemBuilder(activeMission.getMissionGuiIconMaterial()).durability(activeMission.getMissionGuiIconDurability()).amount(1).name(Txt.parse(activeMission.getMissionDisplayname())).setLore(lore));
		}
		
		return chestGui.getInventory();
	}
	
	public void incrementProgress(Mission mission, MPlayer mPlayer, Integer amount)
	{
		if (mPlayer.isConsole()) return;
		this.incrementProgress(mission, mPlayer.getFaction(), amount);
	}
	
	public void incrementProgress(Mission mission, MPlayer mPlayer)
	{
		if (mPlayer.isConsole()) return;
		this.incrementProgress(mission, mPlayer.getFaction());
	}
	
	public void incrementProgress(Mission mission, Faction faction)
	{
		this.incrementProgress(mission, faction, 1);
	}
	
	public void incrementProgress(Mission mission, Faction faction, Integer amount)
	{
		if (faction.getActiveMission() == null) return;
		if (!faction.getActiveMission().getMissionName().equalsIgnoreCase(mission.getMissionName())) return;
		
		Integer missionComplete = faction.getMissionRequirementComplete();
		
		if (missionComplete + amount >= mission.getRequirement() - amount)
		{
			faction.addCredits(mission.getCreditsReward());
			faction.setActiveMission(null);
			faction.setMissionRequirementComplete(0);
			faction.msg(LangConf.get().completeMissionMsg.replace("%mission%", mission.getMissionName()).replace("%rewardAmount%", String.valueOf(mission.getCreditsReward())));
		}
		else
		{
			faction.setMissionRequirementComplete(missionComplete + amount);
		}
	}
	
	public Mission getMissionByName(String missionName)
	{
		return this.missions.stream().filter(mission -> mission.getMissionName().equals(missionName)).findFirst().orElse(null);
	}
	
	public ArrayList<Mission> getMissions()
	{
		return missions;
	}
	
	public void load()
	{
		missions.add(new MissionBlaze());
//		missions.add(new MissionSugarcane());
		missions.add(new MissionTrench());
//		missions.add(new MissionCactus());
		missions.add(new MissionEXP());
		missions.add(new MissionTravel());
	}
	
}