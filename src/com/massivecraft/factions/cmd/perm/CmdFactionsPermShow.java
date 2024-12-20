package com.massivecraft.factions.cmd.perm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.container.TypeSet;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CmdFactionsPermShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermShow()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(TypeSet.get(TypeMPerm.get()), "perms", "all", true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Arg: Faction
		Faction faction = this.readArg(msenderFaction);
		Collection<MPerm> mperms = this.readArg(MPerm.getAll());
		
		// Create messages
		List<Object> messages = new ArrayList<>();
		
		messages.add(Txt.titleize("Perm for " + faction.describeTo(msender, false)));
		messages.add(MPerm.getStateHeaders());
		for (MPerm mperm : mperms)
		{
			if (mperm.isVisible()) {
				messages.add(mperm.getStateInfo(faction.getPermitted(mperm), mperm, true));
			} else if (msender.isOverriding()) {
				messages.add(mperm.getStateInfo(faction.getPermitted(mperm), mperm, true));
			}
		}
		
		// Send messages
		message(messages);
	}
	
}
