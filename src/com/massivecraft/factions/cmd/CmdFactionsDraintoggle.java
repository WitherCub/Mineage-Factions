package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsDraintoggle extends FactionsCommand
{
	
	public CmdFactionsDraintoggle()
	{
		this.addRequirements(RequirementIsPlayer.get());
		this.addRequirements(ReqHasFaction.get());
		this.addParameter(TypeBooleanYes.get(), "on/off", "flip");
	}
	
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You must be in a faction to use this command!"));
			return;
		}
		
		Boolean value = readArg(!msender.isDrain());
		
		if (value)
		{
			if (msender.isDrain())
			{
				msender.msg(MConf.get().drainAlreadyAllowedMsg);
			}
			else
			{
				msenderFaction.msg(MConf.get().drainStatusAllowedMsg.replace("%player%", msender.describeTo(msenderFaction, true)));
				msender.msg(MConf.get().drainingSetAllowedInformMsg);
				msender.setDrain(true);
			}
		}
		else if (!msender.isDrain())
		{
			msender.msg(MConf.get().drainAlreadyDisallowedMsg);
		}
		else
		{
			msenderFaction.msg(MConf.get().drainStatusDisallowedMsg.replace("%player%", msender.describeTo(msenderFaction, true)));
			msender.msg(MConf.get().drainingSetDisallowedInformMsg);
			msender.setDrain(false);
		}
	}
	
}