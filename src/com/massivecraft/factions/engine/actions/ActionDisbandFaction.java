package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ActionDisbandFaction extends ChestActionAbstract
{
	
	private Faction faction;
	private Faction msenderFaction;
	private MPlayer msender;
	private Player me;
	private CommandSender sender;
	
	public ActionDisbandFaction(Faction faction, Faction msenderFaction, MPlayer msender, Player me, CommandSender sender)
	{
		this.faction = faction;
		this.msenderFaction = msenderFaction;
		this.msender = msender;
		this.me = me;
		this.sender = sender;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		// Event
		EventFactionsDisband eventFactionsDisband = new EventFactionsDisband(me, faction);
		eventFactionsDisband.run();
		if (eventFactionsDisband.isCancelled()) return false;
		
		// Merged Apply and Inform
		
		// Run event for each player in the faction
		for (MPlayer mplayer : faction.getMPlayers())
		{
			EventFactionsMembershipChange membershipChangeEvent = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.DISBAND, false);
			membershipChangeEvent.run();
		}
		
		// Inform
		for (MPlayer mplayer : faction.getMPlayersWhereOnline(true))
		{
			mplayer.msg("<h>%s<i> disbanded your faction.", msender.describeTo(mplayer));
		}
		
		if (msenderFaction != faction)
		{
			msender.msg("<i>You disbanded <h>%s<i>.", faction.describeTo(msender));
		}
		
		// Drop all /f chest items at disbander's feet
		if (faction.getLevel(MissionUpgradeConf.get().factionChestUpgrade.getUpgradeName()) > 0)
		{
			if (faction.getInventory() != null)
			{
				for (HumanEntity he : faction.getInventory().getViewers())
				{
					he.closeInventory();
				}
				
				for (ItemStack itemStack : faction.getInventory().getContents())
				{
					if (itemStack != null)
					{
						me.getWorld().dropItemNaturally(me.getLocation(), itemStack);
					}
				}
				
				msender.msg("<i>As result of disbanding <h>%s<i>, all /f chest contents have been dropped at your feet.", faction.describeTo(msender));
			}
		}
		
		// Inform loss of virtual TNT
		if (faction.getTnt() > 0)
		{
			msender.msg("<i>As result of disbanding <h>%s<i>, <v>%s <i>virtually stored tnt has been destroyed.", faction.describeTo(msender), faction.getTnt());
		}
		
		// Log
		if (MConf.get().logFactionDisband)
		{
			Factions.get().log(Txt.parse("<i>The faction <h>%s <i>(<h>%s<i>) was disbanded by <h>%s<i>.", faction.getName(), faction.getId(), msender.getDisplayName(IdUtil.getConsole())));
		}
		
		// Apply
		faction.detach();
		return true;
	}
	
}