package com.massivecraft.factions.cmd.alt;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.List;

public class CmdFactionsAltsList extends FactionsCommand
{
	
	public CmdFactionsAltsList()
	{
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		if (msenderFaction.isSystemFaction())
		{
			msender.msg(Txt.parse("<b>You must be in a faction to use this command!"));
			return;
		}
		
		// Args
		int page = this.readArg();
		
		Faction faction = this.readArg(msenderFaction);
		
		if (MConf.get().hideOtherFacAlts && !msender.isOverriding() && faction != msenderFaction)
		{
			msender.msg("<b>You don't have permission to list other factions alts.");
			return;
		}
		
		List<MPlayer> mPlayers = msenderFaction.getMPlayers();
		
		final List<Mson> mplayerList = new ArrayList<>();
		
		for (MPlayer mPlayer : mPlayers)
		{
			if (!mPlayer.isAlt()) continue;
			
			Mson mson = mson(Txt.parse("<i>%s", mPlayer.describeTo(msender, true)));
			
			mplayerList.add(mson);
		}
		
		final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Alts in %s", faction.getName(msender)), page, mplayerList, (Msonifier<Mson>) (mson, i) -> mplayerList.get(i));
		
		pager.message();
	}
	
}
