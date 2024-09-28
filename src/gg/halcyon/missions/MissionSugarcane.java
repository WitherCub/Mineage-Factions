package gg.halcyon.missions;

import com.massivecraft.factions.entity.ConfMission;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.objects.Mission;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class MissionSugarcane extends Mission
{
	private ConfMission confMission;
	
	public MissionSugarcane()
	{
		confMission = MissionsManager.get().getConfMissionByName("Sugarcane");
	}
	
	@Override
	public String getMissionName()
	{
		return "Sugarcane";
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
		if (event.getBlock() == null || event.getBlock().getType() != Material.SUGAR_CANE_BLOCK) return;
		
		int sugarcaneAmount = 0;
		for (int i = event.getBlock().getY(); i < 256; ++i)
		{
			final Block block = new Location(event.getBlock().getWorld(), (double) event.getBlock().getX(), (double) i, (double) event.getBlock().getZ()).getBlock();
			if (!block.getType().equals(Material.SUGAR_CANE_BLOCK)) break;
			++sugarcaneAmount;
		}
		
		MissionsManager.get().incrementProgress(this, MPlayer.get(event.getPlayer()), sugarcaneAmount);
	}
}