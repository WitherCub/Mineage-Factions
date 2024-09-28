package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

public class CmdFactionsSetpaypal extends FactionsCommand
{
	
	public CmdFactionsSetpaypal()
	{
		this.addParameter(TypeString.get(), "paypal");
		
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(RequirementIsPlayer.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.LEADER));
	}
	
	@Override
	public void perform() throws MassiveException
	{
		
		String paypal = this.readArg();
		
		if (paypal.length() < MConf.get().minFactionPaypalLength || paypal.length() > MConf.get().maxFactionPaypalLength)
		{
			msender.msg(MConf.get().factionPaypalInvalidLengthMsg.replace("%minCharacters%", String.valueOf(MConf.get().minFactionPaypalLength)).replace("%maxCharacters%", String.valueOf(MConf.get().maxFactionPaypalLength)));
			return;
		}
		
		if (!isValidString(paypal))
		{
			msender.msg(MConf.get().notValidPaypalMsg);
			return;
		}
		
		msenderFaction.setFactionPayPal(paypal);
		msender.msg(MConf.get().paypalUpdatedPlayerMsg.replace("%paypal%", paypal));
		msenderFaction.msg(MConf.get().paypalUpdatedFactionMsg.replace("%player%", msender.getName()));
	}
	
	private boolean isValidString(String string)
	{
		for (char c : string.toCharArray())
		{
			if (!MiscUtil.substanceChars.contains(String.valueOf(c)) && !String.valueOf(c).equalsIgnoreCase("@") && !String.valueOf(c).equalsIgnoreCase("."))
			{
				return false;
			}
		}
		return true;
	}
	
}