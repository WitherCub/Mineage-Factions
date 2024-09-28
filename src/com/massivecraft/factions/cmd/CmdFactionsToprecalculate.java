package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.task.TaskFactionTopCalculate;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsToprecalculate extends FactionsCommand
{
	
	public CmdFactionsToprecalculate()
	{
		this.setVisibility(Visibility.SECRET);
		this.addRequirements(RequirementHasPerm.get(MConf.get().ftopRecalculatePerm));
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (!msender.isPlayer())
		{
			if (TaskFactionTopCalculate.get().isRunning())
			{
				msender.msg(MConf.get().ftopAlreadyRecalculatingMsg);
				return;
			}
			TaskFactionTopCalculate.get().updateFactionTopValues();
			return;
		}
		
		if (msender.isPlayer() && msender.getPlayer().hasPermission(MConf.get().ftopRecalculatePerm))
		{
			if (TaskFactionTopCalculate.get().isRunning())
			{
				msender.msg(MConf.get().ftopAlreadyRecalculatingMsg);
				return;
			}
			TaskFactionTopCalculate.get().updateFactionTopValues();
		}
		else
		{
			msender.msg(MConf.get().ftopRecalculateNoPermMsg);
		}
	}
}