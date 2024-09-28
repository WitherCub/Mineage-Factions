package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.enumeration.TypeMaterial;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Material;

public class CmdFactionsSetwarpitem extends FactionsCommand
{
	
	public CmdFactionsSetwarpitem()
	{
		// Parameters
		this.addParameter(TypeString.get(), "warp");
		this.addParameter(TypeMaterial.get(), "material", "hand");
		
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		// MPerm
		if (!MPerm.getPermSetwarp().has(msender, msenderFaction, true))
			return;
		
		String warp = this.readArgAt(0, null);
		Material material = this.readArgAt(1, me.getItemInHand().getType());
		
		if (material != null && material != Material.AIR)
		{
			msenderFaction.setWarpItem(warp, material);
			msender.msg("<i>Set warp <v>%s <i>item to <v>%s<i>.", warp, Txt.getMaterialName(material));
		}
	}
	
}