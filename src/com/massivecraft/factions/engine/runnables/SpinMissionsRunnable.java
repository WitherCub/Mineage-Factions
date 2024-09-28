package com.massivecraft.factions.engine.runnables;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.objects.Mission;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.missions.MissionsManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinMissionsRunnable extends BukkitRunnable
{
	
	private NumberFormat priceFormat;
	private Random random;
	private MPlayer mPlayer;
	private int timesSpun = 0;
	
	public SpinMissionsRunnable(MPlayer mPlayer)
	{
		this.mPlayer = mPlayer;
		this.random = new Random();
		this.priceFormat = NumberFormat.getInstance();
		this.priceFormat.setGroupingUsed(true);
	}
	
	@Override
	public void run()
	{
		Mission mission = MissionsManager.get().getMissions().get(random.nextInt(MissionsManager.get().getMissions().size()));
		
		List<String> lore = new ArrayList<>();
		
		for (String s : MConf.get().missionsGuiMissionLore)
		{
			lore.add(Txt.parse(s
								   .replace("%missionDescription%", mission.getDescription())
								   .replace("%creditsReward%", String.valueOf(mission.getCreditsReward()))
								   .replace("%timeRemaining%", TimeUtil.formatTime(MConf.get().missionTimerHours))
								   .replace("%requirementComplete%", priceFormat.format(mPlayer.getFaction().getMissionRequirementComplete()))
								   .replace("%requirement%", priceFormat.format(mission.getRequirement()))));
		}
		
		if (mPlayer.getPlayer().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(Txt.parse("&aMissions")))
		{
			mPlayer.getPlayer().getOpenInventory().getTopInventory().setItem(2, new ItemBuilder(mission.getMissionGuiIconMaterial()).durability(mission.getMissionGuiIconDurability()).amount(1).name(Txt.parse(mission.getMissionDisplayname())).setLore(lore));
		}
		
		if (timesSpun == MConf.get().missionSpinTimes)
		{
			mPlayer.getFaction().msg(MConf.get().missionAssignedMsg.replace("%mission%", mission.getMissionDisplayname()));
			
			mPlayer.getFaction().setMissionStartTime(System.currentTimeMillis());
			mPlayer.getFaction().setMissionRequirementComplete(0);
			mPlayer.getFaction().setActiveMission(mission.getMissionName());
			mPlayer.getFaction().setSpinningMission(false);
			
			timesSpun = 0;
			this.cancel();
		}
		else
		{
			++timesSpun;
		}
	}
}