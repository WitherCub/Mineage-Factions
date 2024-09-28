package com.massivecraft.factions.cmd.pperm;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPpermSet extends FactionsCommand
{
	
	public CmdFactionsPpermSet()
	{
		this.addParameter(TypeMPerm.get(), "perm");
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeBooleanYes.get(), "yes/no");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MPerm perm = this.readArg();
		MPlayer mPlayer = this.readArg();
		Boolean value = this.readArg();

		if (!MPerm.getPermPerms().has(msender, mPlayer.getFaction(), true)) return;
		
		if (!msender.isOverriding() && !perm.isEditable())
		{
			msg("<b>The perm <h>%s <b>is not editable.", perm.getName());
			return;
		}
		
		if (mPlayer.isAlt())
		{
			msg("<b>Pperms are not editable for <g>alts<b>.");
			return;
		}
		
		if (mPlayer.getRelationTo(msenderFaction) == Rel.LEADER)
		{
			msg("<b>The perm <v>%s <b>is not editable for <g>your faction leader<b>.", perm.getName());
			return;
		}
		
		if (mPlayer.getPlayerPermValue(perm) == value)
		{
			msg("%s <i>already has %s <i>set to %s<i>.", mPlayer.describeTo(msender, true), perm.getDesc(true, false), Txt.parse(value ? "<g>YES" : "<b>NOO"));
			return;
		}
		
		mPlayer.setPlayerPerm(perm, value);
		msg("<i>Set <v>%s <i>permission override for <g>%s <i>to %s<i>.", perm.getDesc(true, false), mPlayer.describeTo(msender), Txt.parse(value ? "<g>YES" : "<b>NOO"));
	}
	
}