package com.massivecraft.factions.cmd.access;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsAccessallPlayer extends FactionsCommand
{
	public CmdFactionsAccessallPlayer()
	{
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeBooleanYes.get(), "yes/no");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MPlayer mplayer = this.readArg();
		boolean newValue = this.readArg();
		
		if (!MPerm.getPermAccess().has(msender, msenderFaction, true)) return;
		
		for (PS ps : BoardColl.get().getChunks(msenderFaction))
		{
			TerritoryAccess ta = BoardColl.get().getTerritoryAccessAt(ps);
			ta = ta.withPlayerId(mplayer.getId(), newValue);
			BoardColl.get().setTerritoryAccessAt(ps, ta);
		}
		
		if (newValue)
		{
			msenderFaction.msg("%s <i>has <g>granted %s <i>access to all claims.", msender.describeTo(msenderFaction), mplayer.describeTo(msenderFaction));
		}
		else
		{
			msenderFaction.msg("%s <i>has <b>revoked <i>all access for %s<i>.", msender.describeTo(msenderFaction), mplayer.describeTo(msenderFaction));
		}
	}
}