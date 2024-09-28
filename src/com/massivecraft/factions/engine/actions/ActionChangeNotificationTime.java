package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsCheck;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;

public class ActionChangeNotificationTime extends ChestActionAbstract
{
	
	private MPlayer mPlayer;
	
	public ActionChangeNotificationTime(MPlayer mPlayer)
	{
		this.mPlayer = mPlayer;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		Faction faction = mPlayer.getFaction();
		
		if (faction.isSystemFaction()) return false;
		
		if (!MPerm.getPermCheckSettings().has(mPlayer, faction, true)) return false;
		
		mPlayer.getPlayer().closeInventory();
		
		if (faction.getNotificationTimeMinutes() == 0)
		{
			faction.setNotificationTimeMinutes(5);
		}
		else if (faction.getNotificationTimeMinutes() == 5)
		{
			faction.setNotificationTimeMinutes(10);
		}
		else if (faction.getNotificationTimeMinutes() == 10)
		{
			faction.setNotificationTimeMinutes(15);
		}
		else if (faction.getNotificationTimeMinutes() == 15)
		{
			faction.setNotificationTimeMinutes(20);
		}
		else if (faction.getNotificationTimeMinutes() == 20)
		{
			faction.setNotificationTimeMinutes(25);
		}
		else if (faction.getNotificationTimeMinutes() == 25)
		{
			faction.setNotificationTimeMinutes(30);
		}
		else if (faction.getNotificationTimeMinutes() == 30)
		{
			faction.setNotificationTimeMinutes(0);
		}

		CmdFactionsCheck.get().execute(mPlayer.getSender(), Collections.emptyList());
		return true;
	}
	
}