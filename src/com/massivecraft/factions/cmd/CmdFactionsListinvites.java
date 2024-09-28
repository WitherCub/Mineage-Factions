package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.Invitation;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.comparator.ComparatorSmart;
import com.massivecraft.massivecore.mixin.MixinDisplayName;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class CmdFactionsListinvites extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsListinvites()
	{
		this.addAliases("showinvites");
		
		// Parameters
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //	
	
	@Override
	public void perform() throws MassiveException
	{
		// Args	
		int page = this.readArg();
		
		Faction faction = this.readArg(msenderFaction);
		
		if (faction != msenderFaction && !Perm.LISTINVITES_OTHER.has(sender, true)) return;
		
		// MPerm
		if (!MPerm.getPermInvite().has(msender, msenderFaction, true)) return;
		
		// Pager Create
		final List<Entry<String, Invitation>> invitations = new MassiveList<>(faction.getInvitations().entrySet());
		
		Collections.sort(invitations, new Comparator<Entry<String, Invitation>>()
		{
			@Override
			public int compare(Entry<String, Invitation> i1, Entry<String, Invitation> i2)
			{
				return ComparatorSmart.get().compare(i2.getValue().getCreationMillis(), i1.getValue().getCreationMillis());
			}
		});
		
		Set<Entry<String, Invitation>> entriesToRemove = new HashSet<>();
		
		for (Entry<String, Invitation> entry : invitations)
		{
			if (entry.getValue().isAlt())
			{
				entriesToRemove.add(entry);
			}
		}
		
		invitations.removeAll(entriesToRemove);
		
		final long now = System.currentTimeMillis();
		
		final Pager<Entry<String, Invitation>> pager = new Pager<>(this, "Invited Players List", page, invitations, (Stringifier<Entry<String, Invitation>>) (entry, index) -> {
			String inviteeId = entry.getKey();
			String inviterId = entry.getValue().getInviterId();

			String inviteeDisplayName = MixinDisplayName.get().getDisplayName(inviteeId, sender);
			String inviterDisplayName = inviterId != null ? MixinDisplayName.get().getDisplayName(inviterId, sender) : Txt.parse("<silver>unknown");

			String ageDesc = "";
			if (entry.getValue().getCreationMillis() != null)
			{
				long millis = now - entry.getValue().getCreationMillis();
				LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(millis, TimeUnit.getAllButMillis()), 2);
				ageDesc = TimeDiffUtil.formatedMinimal(ageUnitcounts, "<i>");
				ageDesc = " " + ageDesc + Txt.parse(" ago");
			}

			return Txt.parse("%s<i> was invited by %s<reset>%s<i>.", inviteeDisplayName, inviterDisplayName, ageDesc);
		});
		
		// Pager Message
		pager.message();
	}
	
}
