package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFactionNameStrict;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.NameReserves;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import org.apache.commons.lang.StringUtils;

public class CmdFactionsUnreserve extends FactionsCommand
{
	
	public CmdFactionsUnreserve()
	{
		this.addRequirements(RequirementHasPerm.get(Perm.UNRESERVE));
		this.addParameter(TypeFactionNameStrict.get(), "name");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		String factionName = this.readArg();
		
		if (!NameReserves.get().isNameReserved(factionName))
		{
			msender.msg("&cThe faction name %s is not reserved for anybody.", factionName);
			return;
		}
		
		NameReserves.get().unreserveName(StringUtils.lowerCase(factionName));
		msender.msg("&aSuccessfully unreserved the faction name %s.", factionName);
		
		if (MConf.get().logFactionReserve)
		{
			Factions.get().log(msender.getName() + " unreserved the faction name " + factionName + ".");
		}
	}
	
}