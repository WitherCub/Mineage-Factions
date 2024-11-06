package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.integration.coreprotect.IntegrationCoreProtect;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsInspect extends FactionsCommand
{
	private static CmdFactionsInspect i = new CmdFactionsInspect();
	public static CmdFactionsInspect get() { return i; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsInspect()
	{
		this.addRequirements(RequirementIsPlayer.get());
		this.addParameter(Parameter.getPage());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Integer page = this.readArg(0);
		
		if (page != 0)
		{
			if (msender.getLastInspectedBlockdata().contains("\n"))
			{
				final List<Mson> inspectedItems = new ArrayList<>();
				
				String[] arrayOfString;
				int j = (arrayOfString = msender.getLastInspectedBlockdata().split("\n")).length;
				int index = 0;
				for (int i = 0; i < j; i++)
				{
					if (index == 0)
					{
						index++;
						continue;
					}
					
					index++;
					
					String b = arrayOfString[i].replace("CoreProtect", "")
								   .replace(ChatColor.translateAlternateColorCodes('&', "&3"), ChatColor.YELLOW + "")
								   .replace(ChatColor.translateAlternateColorCodes('&', "&f"), ChatColor.WHITE + "");
					
					if (b.contains("older data") || b.contains("page")) continue;
					if (b.contains(ChatColor.WHITE + "-----")) continue;
					
					String[] split = b.split("-");
					
					b = ChatColor.DARK_GRAY + "»" + split[1].replace(".", "") + ChatColor.DARK_GRAY + " - " + split[0];
					
					inspectedItems.add(Mson.mson(b));
				}
				
				final Pager<Mson> pager = new Pager<>(i, Txt.parse("<i>Inspect Log"), page, inspectedItems, (Msonifier<Mson>) (mson, i) -> inspectedItems.get(i));
				pager.message();
			}
			else
			{
				msender.msg("<i>No data was found for that block.");
			}
			
			return;
		}
		
		// dont allow toggling of inspection if coreprotect isn't enabled
		if (!IntegrationCoreProtect.get().isIntegrationActive())
		{
			msender.msg("<b>Inspecting is currently not available at this time. Please contact an administrator.");
			return;
		}
		
		if (!MPerm.getPermInspect().has(msender, BoardColl.get().getFactionAt(PS.valueOf(msender.getPlayer().getLocation())), true))
			return;
		
		msender.setInspecting(!msender.isInspecting());
		msender.msg(msender.isInspecting() ? "<i>Inspecting has been <g>enabled<i>." : "<i>Inspecting has been <b>disabled<i>.");
	}
	
}