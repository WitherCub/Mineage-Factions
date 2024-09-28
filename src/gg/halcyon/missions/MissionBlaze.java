package gg.halcyon.missions;

import com.massivecraft.factions.entity.ConfMission;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.objects.Mission;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class MissionBlaze extends Mission
{
	private ConfMission confMission;
	
	public MissionBlaze()
	{
		confMission = MissionsManager.get().getConfMissionByName("Blaze");
	}
	
	@Override
	public String getMissionName()
	{
		return "Blaze";
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
	public void onMobKill(EntityDeathEvent event)
	{
		if (event.getEntity().getKiller() == null) return;
		if (event.getEntityType() != EntityType.BLAZE) return;
		
		MissionsManager.get().incrementProgress(this, MPlayer.get(MPlayer.get(event.getEntity().getKiller())));
	}
}