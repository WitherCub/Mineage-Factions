package com.massivecraft.factions.cmd;

import com.massivecraft.factions.engine.EngineFly;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanYes;

public class CmdFactionsFly extends FactionsCommand
{
	
	public CmdFactionsFly()
	{
		this.addParameter(TypeBooleanYes.get(), "on/off", "flip");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Boolean value = readArg(!EngineFly.get().playersWithFlyDisabled.contains(me.getUniqueId().toString()));
		
		if (value)
		{
			if (EngineFly.get().playersWithFlyDisabled.contains(me.getUniqueId().toString()))
			{
				msender.msg(MConf.get().alreadyOutOfFlyMsg);
			}
			else
			{
				msender.msg(MConf.get().flyNowOffMsg);
				EngineFly.get().playersWithFlyDisabled.add(me.getUniqueId().toString());
			}
		}
		else if (!EngineFly.get().playersWithFlyDisabled.contains(me.getUniqueId().toString()))
		{
			msender.msg(MConf.get().alreadyOutOfFlyMsg);
		}
		else
		{
			msender.msg(MConf.get().flyNowOnMsg);
			EngineFly.get().playersWithFlyDisabled.remove(me.getUniqueId().toString());
		}
	}
	
}