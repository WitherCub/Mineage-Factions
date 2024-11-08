package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsLogins extends FactionsCommand
{
	
	public CmdFactionsLogins()
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
		
		Boolean value = readArg(!msender.recieveLoginNotifications());
		
		if (value)
		{
			if (msender.recieveLoginNotifications())
			{
				msender.msg(LangConf.get().alreadyEnabledLoginNotifsMsg);
			}
			else
			{
				msenderFaction.msg(LangConf.get().loginNotifsNowOnMsg);
				msender.setLoginNotifications(true);
			}
		}
		else if (!msender.recieveLoginNotifications())
		{
			msender.msg(LangConf.get().alreadyDisabledLoginNotifsMsg);
		}
		else
		{
			msenderFaction.msg(LangConf.get().loginNotifsOffMsg);
			msender.setLoginNotifications(false);
		}
	}
	
}