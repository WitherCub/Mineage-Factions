package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.Txt;

import java.util.List;

public class CmdFactionsDrainlist extends FactionsCommand
{
	
	public CmdFactionsDrainlist()
	{
		this.addRequirements(RequirementIsPlayer.get());
		this.addRequirements(ReqHasFaction.get());
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		int page = this.readArg();
		Faction faction = this.readArg(msenderFaction);
		
		if (faction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You can't check drain status of system factions!"));
			return;
		}
		
		final List<MPlayer> mplayers = faction.getMPlayers();
		
		String title = Txt.parse("<i>Drain status of %s<i>.", faction.describeTo(msender, true));
		
		final Pager<MPlayer> pager = new Pager<>(this, title, page, mplayers, (Stringifier<MPlayer>) (mplayer, index) -> {
			String displayName = mplayer.getNameAndSomething(msender.getColorTo(mplayer).toString(), "") + Txt.parse("<gold>") + " |";
			String status = Txt.parse(mplayer.isDrain() ? "&aALLOWED" : "&cDISALLOWED");
			return Txt.parse("%s %s", displayName, status);
		});
		
		pager.message();
	}
	
}