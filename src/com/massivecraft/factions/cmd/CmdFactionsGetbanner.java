package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.money.MoneyMixinVault;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsGetbanner extends FactionsCommand
{
	
	public CmdFactionsGetbanner()
	{
		this.addAliases("banner");
		
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg(msenderFaction);
		
		if (!MPerm.getPermBanner().has(msender, faction, true)) return;
		
		if (!faction.hasBanner())
		{
			msender.msg(faction.describeTo(msender, true) + " <b>does not have a banner set.");
			return;
		}
		
		if (!new MoneyMixinVault().getEconomy().has(me.getName(), MConf.get().costForBanner))
		{
			msender.msg(Txt.parse("<b>You need $%s to get a banner.", MConf.get().costForBanner));
			return;
		}
		
		if (me.getInventory().firstEmpty() == -1)
		{
			msender.msg("<b>You must have an open inventory slot to receive a banner.");
			return;
		}
		
		new MoneyMixinVault().getEconomy().withdrawPlayer(me.getName(), MConf.get().costForBanner);
		me.getInventory().addItem(faction.getBanner());
		msender.msg("<i>You have received a faction banner.");
	}
	
}