package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsCheck;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;

public class ActionToggleAlarm extends ChestActionAbstract
{
	
	private MPlayer mPlayer;
	
	public ActionToggleAlarm(MPlayer mPlayer)
	{
		this.mPlayer = mPlayer;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		Faction faction = mPlayer.getFaction();
		
		if (faction.isSystemFaction()) return false;
		
		if (!MPerm.getPermCheck().has(mPlayer, faction, true)) return false;
		
		mPlayer.getPlayer().closeInventory();
		
		mPlayer.setAlertSoundEnabled(!mPlayer.isAlertSoundEnabled());

		CmdFactionsCheck.get().execute(mPlayer.getSender(), Collections.emptyList());
		return true;
	}
	
}