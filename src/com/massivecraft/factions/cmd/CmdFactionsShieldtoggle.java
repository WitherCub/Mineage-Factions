package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsShieldtoggle extends FactionsCommand
{
	
	public CmdFactionsShieldtoggle()
	{
		this.setVisibility(Visibility.SECRET);
		this.addRequirements(RequirementHasPerm.get(Perm.SHIELDTOGGLE));
	}
	
	@Override
	public void perform() throws MassiveException
	{
		MConf.get().setShieldTimesChangeable(!MConf.get().isShieldTimesChangeable());
		msender.msg("&eAbility to change shield times has been %s&e.", MConf.get().isShieldTimesChangeable() ? Txt.parse("&aENABLED") : Txt.parse("&cDISABLED"));
	}
	
}