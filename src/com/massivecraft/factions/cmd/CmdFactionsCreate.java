package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasntFaction;
import com.massivecraft.factions.cmd.type.TypeFactionNameStrict;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.coll.FactionColl;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.NameReserves;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.store.MStore;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdFactionsCreate extends FactionsCommand
{
	private static CmdFactionsCreate i = new CmdFactionsCreate();
	public static CmdFactionsCreate get() { return i; }
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsCreate()
	{
		// Aliases
		this.addAliases("new");
		
		// Parameters
		this.addParameter(TypeFactionNameStrict.get(), "name");
		
		// Requirements
		this.addRequirements(ReqHasntFaction.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		String newName = this.readArg();
		String lowercaseName = StringUtils.lowerCase(newName);
		
		// Check reserves
		if (!NameReserves.get().canPlayerClaimName(lowercaseName, msender))
		{
			UUID reservedForUuid = NameReserves.get().getNameReservedForWho(lowercaseName);
			Player player = Bukkit.getPlayer(reservedForUuid);
			
			if (player == null)
			{
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(reservedForUuid);
				msender.msg(LangConf.get().factionNameReservedMsg.replace("%faction%", newName).replace("%player%", offlinePlayer.getName()));
			}
			else
			{
				msender.msg(LangConf.get().factionNameReservedMsg.replace("%faction%", newName).replace("%player%", player.getName()));
			}
			return;
		}
		
		// Pre-Generate Id
		String factionId = MStore.createId();
		
		// Event
		EventFactionsCreate createEvent = new EventFactionsCreate(sender, factionId, newName);
		createEvent.run();
		if (createEvent.isCancelled()) return;
		
		// Remove name from reserves if it was reserved
		if (NameReserves.get().isNameReserved(lowercaseName))
		{
			NameReserves.get().removeReservedName(lowercaseName);
		}
		
		// Apply
		Faction faction = FactionColl.get().create(factionId);
		faction.setName(newName);
		
		msender.setRole(Rel.LEADER);
		msender.setFaction(faction);

		EventFactionsMembershipChange joinEvent = new EventFactionsMembershipChange(sender, msender, faction, MembershipChangeReason.CREATE, false);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction
		
		// Inform
		msg("<i>You created the faction %s", faction.getName(msender));
		
		if (MConf.get().enableSetPaypal)
		{
			//message(Mson.mson(mson("You should now: ").color(ChatColor.YELLOW), CmdFactions.get().cmdFactionsSetpaypal.getTemplate()));
		}
		else
		{
			message(Mson.mson(mson("You should now: ").color(ChatColor.YELLOW), CmdFactionsDescription.get().getTemplate()));
		}
		
		message(Mson.mson(mson("You should also: ").color(ChatColor.YELLOW), CmdFactionsShield.get().getTemplate()));
		
		// Log
		if (MConf.get().logFactionCreate)
		{
			Factions.get().log(msender.getName() + " created a new faction: " + newName);
		}
	}
	
}
