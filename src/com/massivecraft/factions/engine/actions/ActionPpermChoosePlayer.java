package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.mixin.MixinCommand;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ActionPpermChoosePlayer extends ChestActionAbstract
{
	
	private MPlayer mPlayer;
	
	public ActionPpermChoosePlayer(MPlayer mPlayer)
	{
		this.mPlayer = mPlayer;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		if (mPlayer.getFaction().isSystemFaction()) return false;
		
		MixinCommand.get().dispatchCommand(event.getWhoClicked(), "f pperm gui " + mPlayer.getName());
		
		return true;
	}
}