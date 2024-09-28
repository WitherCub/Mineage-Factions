package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;

public class CmdFactionsStealth extends FactionsCommand
{
	public CmdFactionsStealth()
	{
		this.addAliases("ninja");
		this.addParameter(TypeBooleanYes.get(), "on/off", "flip");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Boolean value = readArg(!msender.isStealth());
		
		if (value)
		{
			if (msender.isStealth())
			{
				msender.msg(MConf.get().alreadyInStealthMsg);
			}
			else
			{
				msender.msg(MConf.get().stealthNowOnMsg);
				msender.setStealth(true);
			}
		}
		else if (!msender.isStealth())
		{
			msender.msg(MConf.get().alreadyOutOfStealthMsg);
		}
		else
		{
			msender.msg(MConf.get().stealthNowOffMsg);
			msender.setStealth(false);
		}
	}
}