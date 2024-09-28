package com.massivecraft.factions.cmd.pperm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsPpermUnset extends FactionsCommand
{
	
	public CmdFactionsPpermUnset()
	{
		this.addParameter(TypeMPerm.get(), "perm");
		this.addParameter(TypeMPlayer.get(), "player");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MPerm perm = this.readArg();
		MPlayer mPlayer = this.readArg();

		if (!MPerm.getPermPerms().has(msender, mPlayer.getFaction(), true)) return;
		
		if (!msender.isOverriding() && !perm.isEditable())
		{
			msg("<b>The perm <h>%s <b>is not editable.", perm.getName());
			return;
		}
		
		if (mPlayer.getPlayerPermValue(perm) == null)
		{
			msg("<i>The <v>%s <i>permission is not set for %s<i>.", perm.getDesc(true, false), mPlayer.describeTo(msender));
			return;
		}
		
		mPlayer.removePlayerPerm(perm);
		msg("<i>Removed <v>%s <i>permission override for <g>%s<i>.", perm.getDesc(true, false), mPlayer.describeTo(msender));
	}
	
}