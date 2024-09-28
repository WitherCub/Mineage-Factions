package gg.halcyon.missions;

import com.massivecraft.factions.entity.ConfMission;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.objects.Mission;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class MissionTravel extends Mission
{
	private ConfMission confMission;
	
	public MissionTravel()
	{
		confMission = MissionsManager.get().getConfMissionByName("Travel");
	}
	
	@Override
	public String getMissionName()
	{
		return "Travel";
	}
	
	@Override
	public String getMissionDisplayname()
	{
		return confMission.getMissionDisplayName();
	}
	
	@Override
	public String getDescription()
	{
		return confMission.getMissionDescription();
	}
	
	@Override
	public Double getRequirement()
	{
		return confMission.getMissionRequirementComplete();
	}
	
	@Override
	public Integer getCreditsReward()
	{
		return confMission.getCreditsReward();
	}
	
	@Override
	public Material getMissionGuiIconMaterial()
	{
		return confMission.getMissionGuiIconMaterial();
	}
	
	@Override
	public Integer getMissionGuiIconDurability()
	{
		return confMission.getMissionGuiIconDurability();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (MUtil.isSameBlock(event)) return;
		
		MissionsManager.get().incrementProgress(this, MPlayer.get(event.getPlayer()));
	}
}