package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.ChatColor;

public class CmdFactionsKick extends FactionsCommand
{
	private static CmdFactionsKick i = new CmdFactionsKick();
	public static CmdFactionsKick get() { return i; }
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsKick()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Arg
		MPlayer mplayer = this.readArg();
		
		// Validate
		if (msender == mplayer)
		{
			msg("<b>You can't kick yourself.");
			message(mson(mson("You might want to: ").color(ChatColor.YELLOW), CmdFactionsLeave.get().getTemplate(false)));
			return;
		}
		
		if (mplayer.getRole() == Rel.LEADER && !msender.isOverriding())
		{
			throw new MassiveException().addMsg("<b>The leader cannot be kicked.");
		}
		
		if (mplayer.getRole().isMoreThan(msender.getRole()) && ! msender.isOverriding())
		{
			throw new MassiveException().addMsg("<b>You can't kick people of higher rank than yourself.");
		}
		
		if (mplayer.getRole() == msender.getRole() && ! msender.isOverriding())
		{
			throw new MassiveException().addMsg("<b>You can't kick people of the same rank as yourself.");
		}

		if ( ! MConf.get().canLeaveWithNegativePower && mplayer.getPower() < 0 && ! msender.isOverriding())
		{
			msg("<b>You can't kick that person until their power is positive.");
			return;
		}
		
		// MPerm
		Faction mplayerFaction = mplayer.getFaction();
		if ( ! MPerm.getPermKick().has(msender, mplayerFaction, true)) return;

		// Event
		EventFactionsMembershipChange event = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.KICK, false);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		mplayerFaction.msg("%s<i> kicked %s<i> from the faction! :O", msender.describeTo(mplayerFaction, true), mplayer.describeTo(mplayerFaction, true));
		mplayer.msg("%s<i> kicked you from %s<i>! :O", msender.describeTo(mplayer, true), mplayerFaction.describeTo(mplayer));
		if (mplayerFaction != msenderFaction)
		{
			msender.msg("<i>You kicked %s<i> from the faction %s<i>!", mplayer.describeTo(msender), mplayerFaction.describeTo(msender));
		}

		if (MConf.get().logFactionKick)
		{
			Factions.get().log(msender.getDisplayName(IdUtil.getConsole()) + " kicked " + mplayer.getName() + " from the faction " + mplayerFaction.getName());
		}

		// Apply
		if (mplayer.getRole() == Rel.LEADER)
		{
			mplayerFaction.promoteNewLeader();
		}
		mplayerFaction.uninvite(mplayer);
		mplayer.resetFactionData();

		for (PS ps : BoardColl.get().getChunks(msenderFaction))
		{
			TerritoryAccess ta = BoardColl.get().getTerritoryAccessAt(ps);
			ta = ta.withPlayerId(mplayer.getId(), false);
			BoardColl.get().setTerritoryAccessAt(ps, ta);
		}
	}
	
}
