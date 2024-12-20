package com.massivecraft.factions.cmd.access;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsAccessallFaction extends FactionsCommand
{
	public CmdFactionsAccessallFaction()
	{
		this.addParameter(TypeFaction.get(), "faction");
		this.addParameter(TypeBooleanYes.get(), "yes/no");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg();
		boolean newValue = this.readArg();
		
		if (!MPerm.getPermAccess().has(msender, msenderFaction, true)) return;
		
		for (PS ps : BoardColl.get().getChunks(msenderFaction))
		{
			TerritoryAccess ta = BoardColl.get().getTerritoryAccessAt(ps);
			ta = ta.withFactionId(faction.getId(), newValue);
			BoardColl.get().setTerritoryAccessAt(ps, ta);
		}
		
		if (newValue)
		{
			msenderFaction.msg("%s <i>has <g>granted %s <i>access to all claims.", msender.describeTo(msenderFaction), faction.describeTo(msenderFaction));
		}
		else
		{
			msenderFaction.msg("%s <i>has <b>revoked <i>all access for %s<i>.", msender.describeTo(msenderFaction), faction.describeTo(msenderFaction));
		}
	}
}