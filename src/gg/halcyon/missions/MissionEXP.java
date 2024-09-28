package gg.halcyon.missions;

import com.massivecraft.factions.entity.ConfMission;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.objects.Mission;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class MissionEXP extends Mission
{
	private ConfMission confMission;
	
	public MissionEXP()
	{
		confMission = MissionsManager.get().getConfMissionByName("Exp");
	}
	
	@Override
	public String getMissionName()
	{
		return "EXP";
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
	public void gainXp(PlayerExpChangeEvent event)
	{
		if (event.getAmount() > 0)
		{
			MissionsManager.get().incrementProgress(this, MPlayer.get(event.getPlayer()), event.getAmount());
		}
	}
}