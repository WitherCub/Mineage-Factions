package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mixin.MixinTeleport;
import com.massivecraft.massivecore.mixin.TeleporterException;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.teleport.Destination;
import com.massivecraft.massivecore.teleport.DestinationSimple;
import gg.halcyon.EngineExtras;

public class CmdFactionsAssist extends FactionsCommand
{
	private static CmdFactionsAssist i = new CmdFactionsAssist();
	public static CmdFactionsAssist get() { return i; }
	
	public CmdFactionsAssist()
	{
		this.addAliases("assist");
		
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = readArg(msenderFaction);
		
		if (!MPerm.getPermAssist().has(msender, faction, true))
		{
			return;
		}
		if (!EngineExtras.get().assistFactions.containsKey(faction.getId()))
		{
			msender.msg("%s <i>does not need assistance at this time.", faction.describeTo(msender, true));
			return;
		}
		
		Destination destination = new DestinationSimple(PS.valueOf(EngineExtras.get().assistFactions.get(faction.getId())), faction.getName() + "'s banner");
		
		try
		{
			MixinTeleport.get().teleport(me, destination, MConf.get().warpWarmup);
		}
		catch (TeleporterException e)
		{
			msg("<b>%s", e.getMessage());
		}
	}
	
}
