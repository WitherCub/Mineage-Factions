package gg.halcyon.missions;

import com.massivecraft.factions.entity.ConfMission;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.objects.Mission;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class MissionTrench extends Mission
{
	private ConfMission confMission;
	
	public MissionTrench()
	{
		confMission = MissionsManager.get().getConfMissionByName("Trench");
	}
	
	@Override
	public String getMissionName()
	{
		return "Trench";
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
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.getBlock() == null) return;
		
		MissionsManager.get().incrementProgress(this, MPlayer.get(event.getPlayer()));
	}
}