package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.engine.runnables.SpinMissionsRunnable;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionSpinMissions extends ChestActionAbstract
{
	
	private Faction faction;
	
	public ActionSpinMissions(Faction faction)
	{
		this.faction = faction;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		Player playerClicked = (Player) event.getWhoClicked();
		
		if (faction.isSpinningMission()) return false;
		if (faction.getActiveMission() != null) return false; // prevent spinning again when still in inventory
		
		if (faction.getLeader().getPlayer() != playerClicked)
		{
			MixinMessage.get().msgOne(playerClicked, "&cPlease ask your faction leader to start a faction mission.");
			return false;
		}
		
		faction.setSpinningMission(true);
		
		for (Player player : faction.getOnlinePlayers())
		{
			if (player != playerClicked && player.getInventory().getTitle().equalsIgnoreCase(Txt.parse("&aMissions")))
			{
				player.closeInventory();
				MixinMessage.get().msgOne(player, "&6&s &7has begun spinning a new mission... Please check back soon.", playerClicked.getName());
			}
		}
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				new SpinMissionsRunnable(MPlayer.get(event.getWhoClicked())).runTaskTimer(Factions.get(), MConf.get().missionSpinSpeed, MConf.get().missionSpinSpeed);
			}
		}.runTaskLater(Factions.get(), 5L);
		
		return true;
	}
}