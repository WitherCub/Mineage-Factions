package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeSortMPlayer;
import com.massivecraft.factions.comparator.ComparatorMPlayerInactivity;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class CmdFactionsStatus extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsStatus()
	{
		// Parameters
		this.addAliases("s", "bal", "lowpower");
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(TypeSortMPlayer.get(), "sort", "time");
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
		Comparator<MPlayer> sortedBy = this.readArg(ComparatorMPlayerInactivity.get());

		// MPerm
		if ( ! MPerm.getPermStatus().has(msender, faction, true)) return;
		
		// Sort list
		final List<MPlayer> mplayers = faction.getMPlayers();
		Collections.sort(mplayers, sortedBy);
		
		// Pager Create
		String title = Txt.parse("<i>Status of %s<i>.", faction.describeTo(msender, true));
		final Pager<MPlayer> pager = new Pager<>(this, title, page, mplayers, new Stringifier<MPlayer>()
		{
			@Override
			public String toString(MPlayer mplayer, int index)
			{
				// Name
				String displayName = mplayer.getNameAndSomething(msender.getColorTo(mplayer).toString(), "") + Txt.parse("<gold>") + " |";
				String balance = Txt.parse("<silver>") + Money.format(Money.get(mplayer)) + Txt.parse("<gold>") + " |";
				
				// Power
				double currentPower = mplayer.getPower();
				double maxPower = mplayer.getPowerMax();
				String color;
				double percent = currentPower / maxPower;
				
				if (percent > 0.75)
				{
					color = "<green>";
				}
				else if (percent > 0.5)
				{
					color = "<yellow>";
				}
				else if (percent > 0.25)
				{
					color = "<rose>";
				}
				else
				{
					color = "<red>";
				}
				
				String power = Txt.parse("%s%.0f<gray>/<green>%.0f", Txt.parse(color), currentPower, maxPower) + Txt.parse("<gold>") + " |";
				
				// Time
				long lastActiveMillis = mplayer.getLastActivityMillis() - System.currentTimeMillis();
				LinkedHashMap<TimeUnit, Long> activeTimes = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(lastActiveMillis, TimeUnit.getAllButMillis()), 3);
				String lastActive = mplayer.isOnline(msender) ? Txt.parse("<lime>Online") : Txt.parse("<n>Last active: " + TimeDiffUtil.formatedMinimal(activeTimes, "<i>"));
				
				return Txt.parse("%s %s %s %s", displayName, balance, power, lastActive);
			}
		});
		
		
		// Pager Message
		pager.message();
	}
	
}
