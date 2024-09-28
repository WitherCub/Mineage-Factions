package com.massivecraft.factions.cmd.pperm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsPpermClear extends FactionsCommand
{
	
	public CmdFactionsPpermClear()
	{
		this.addParameter(TypeMPlayer.get(), "player");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MPlayer mPlayer = this.readArg();

		if (!MPerm.getPermPerms().has(msender, mPlayer.getFaction(), true)) return;
		
		if (mPlayer.isPpermsEmpty())
		{
			msg("<i>No player permissions have been set for %s<i>.", mPlayer.describeTo(msender));
			return;
		}
		
		msg("<i>Removed all permission overrides for %s<i>.", mPlayer.describeTo(msender));
		mPlayer.clearPlayerPerms();
	}
}