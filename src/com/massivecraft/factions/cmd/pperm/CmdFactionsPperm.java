package com.massivecraft.factions.cmd.pperm;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.util.ScrollerInventory;
import com.massivecraft.massivecore.MassiveException;
import org.bukkit.ChatColor;

public class CmdFactionsPperm extends FactionsCommand
{
	public CmdFactionsPperm() {
		this.addChild(new CmdFactionsPpermSet());
		this.addChild(new CmdFactionsPpermUnset());
		this.addChild(new CmdFactionsPpermClear());
		this.addChild(new CmdFactionsPpermShow());
		this.addChild(CmdFactionsPpermGui.get());
	}

	@Override
	public void perform() throws MassiveException
	{
		this.getHelpCommand().execute(this.sender, this.getArgs());
		msender.msg("<i>Note: Player permissions take priority over the regular relationship permissions when checking if a player can or cannot do something.");
		//		cmdFactionsPpermGui.execute(sender, Collections.emptyList());
		new ScrollerInventory(ChatColor.BLUE + "Select a player...", msender, msenderFaction);
	}
	
}
