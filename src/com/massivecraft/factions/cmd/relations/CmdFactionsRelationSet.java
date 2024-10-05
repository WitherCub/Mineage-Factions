package com.massivecraft.factions.cmd.relations;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeRelation;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.coll.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsRelationChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsRelationSet extends FactionsCommand
{
	private static CmdFactionsRelationSet i = new CmdFactionsRelationSet();
	public static CmdFactionsRelationSet get() { return i; }
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRelationSet()
	{
		// Parameter
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeRelation.get(), "relation");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction otherFaction = this.readArg();
		Rel newRelation = this.readArg();
		
		// MPerm
		if (!MPerm.getPermRel().has(msender, msenderFaction, true)) return;
		
		if (newRelation == Rel.TRUCE && (MConf.get().truceLimit == 0 || getRelationAmount(msenderFaction, Rel.TRUCE) >= MConf.get().truceLimit))
		{
			msender.msg(ChatColor.RED + "You cannot set any more truces.");
			return;
		}
		if (newRelation == Rel.ALLY && (MConf.get().allyLimit == 0 || getRelationAmount(msenderFaction, Rel.ALLY) >= MConf.get().allyLimit))
		{
			msender.msg(ChatColor.RED + "You cannot set any more allies.");
			return;
		}
		if (newRelation == Rel.NEUTRAL && (MConf.get().neutralLimit == 0 || getRelationAmount(msenderFaction, Rel.NEUTRAL) >= MConf.get().neutralLimit))
		{
			msender.msg(ChatColor.RED + "You cannot set any more neutrals.");
			return;
		}
		
		// Verify
		if (otherFaction == msenderFaction)
		{
			throw new MassiveException().setMsg("<b>Nope! You can't declare a relation to yourself :)");
		}
		if (msenderFaction.getRelationWish(otherFaction) == newRelation)
		{
			throw new MassiveException().setMsg("<b>You already have that relation wish set with %s.", otherFaction.getName());
		}
		
		// Event
		EventFactionsRelationChange event = new EventFactionsRelationChange(sender, msenderFaction, otherFaction, newRelation);
		event.run();
		if (event.isCancelled()) return;
		newRelation = event.getNewRelation();
		
		// try to set the new relation
		msenderFaction.setRelationWish(otherFaction, newRelation);
		Rel currentRelation = msenderFaction.getRelationTo(otherFaction, true);
		
		// if the relation change was successful
		if (newRelation == currentRelation)
		{
			otherFaction.msg("%s<i> is now %s.", msenderFaction.describeTo(otherFaction, true), newRelation.getDescFactionOne());
			msenderFaction.msg("%s<i> is now %s.", otherFaction.describeTo(msenderFaction, true), newRelation.getDescFactionOne());
		}
		// inform the other faction of your request
		else
		{
			MassiveCommand command = i;
			String colorOne = newRelation.getColor() + newRelation.getDescFactionOne();
			
			// Mson creation
			Mson factionsRelationshipChange = mson(
				Mson.parse("%s<i> wishes to be %s.", msenderFaction.describeTo(otherFaction, true), colorOne),
				Mson.SPACE,
				mson("[Accept]").color(ChatColor.AQUA).command(command, msenderFaction.getName(), newRelation.name())
			);
			
			otherFaction.sendMessage(factionsRelationshipChange);
			msenderFaction.msg("%s<i> were informed that you wish to be %s<i>.", otherFaction.describeTo(msenderFaction, true), colorOne);
		}
		
		// TODO: The ally case should work!!
		// this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
		if (newRelation != Rel.TRUCE && otherFaction.getFlag(MFlag.getFlagPeaceful()))
		{
			otherFaction.msg("<i>This will have no effect while your faction is peaceful.");
			msenderFaction.msg("<i>This will have no effect while their faction is peaceful.");
		}
		
		if (newRelation != Rel.TRUCE && msenderFaction.getFlag(MFlag.getFlagPeaceful()))
		{
			otherFaction.msg("<i>This will have no effect while their faction is peaceful.");
			msenderFaction.msg("<i>This will have no effect while your faction is peaceful.");
		}
		
		// Mark as changed
		msenderFaction.changed();
	}
	
	public Integer getRelationAmount(Faction faction, Rel relation)
	{
		List<Faction> factionList = new ArrayList<>();
		for (Faction fac : FactionColl.get().getAll())
		{
			Rel rel = fac.getRelationTo(faction);
			if ((rel == relation) && (!fac.equals(FactionColl.get().getNone()) && !fac.equals(FactionColl.get().getSafezone()) && !fac.equals(FactionColl.get().getWarzone())))
			{
				factionList.add(fac);
			}
		}
		return factionList.size();
	}
	
}
