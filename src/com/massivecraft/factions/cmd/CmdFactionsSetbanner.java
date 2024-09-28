package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;

public class CmdFactionsSetbanner extends FactionsCommand
{
	
	public CmdFactionsSetbanner()
	{
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		Faction faction = this.readArg(msenderFaction);
		
		if (!MPerm.getPermSetBanner().has(msender, faction, true)) return;
		
		if (me.getItemInHand().getType() != Material.BANNER)
		{
			msender.msg("<b>You must be holding a banner to set your faction banner.");
			return;
		}
		
		BannerMeta data = (BannerMeta) me.getItemInHand().getItemMeta();
		
		ArrayList<String> patternList = new ArrayList<>();
		String baseColor;
		if (data != null && data.getBaseColor() != null && data.getBaseColor().toString() != null)
		{
			baseColor = data.getBaseColor().toString();
		}
		else
		{
			baseColor = "BLACK";
		}
		
		patternList.add(baseColor);
		int x = data != null ? data.numberOfPatterns() : 0;
		
		for (int i = 0; i < x; ++i)
		{
			String pattern = data.getPattern(i).getColor().toString() + " " + data.getPattern(i).getPattern().toString();
			patternList.add(pattern);
		}
		
		faction.setBanner(patternList);
		
		faction.msg("%s <i>has updated your faction banner.", msender.describeTo(faction, true));
	}
	
}
