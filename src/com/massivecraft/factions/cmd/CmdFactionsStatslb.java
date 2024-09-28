package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeStatistic;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerStats;
import com.massivecraft.factions.entity.objects.PlayerStats;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdFactionsStatslb extends FactionsCommand
{
	
	public CmdFactionsStatslb()
	{
		this.addParameter(TypeStatistic.get(), "statistic");
		this.addParameter(TypeFaction.get(), "faction", "all");
		this.addParameter(Parameter.getPage());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		String statistic = this.readArg();
		Faction faction = this.readArg(null);
		int page = this.readArg();
		
		// Pager Create
		final List<Mson> statsList = new ArrayList<Mson>();
		
		List<PlayerStats> sortedPlayerStats = new ArrayList<>(MPlayerStats.get().playerStats.values());
		
		if (faction != null)
		{
			Set<PlayerStats> playerStatsToRemove = new HashSet<>();
			
			// if sorting by faction check players factions before sorting list and remove irrelevant players
			for (PlayerStats playerStats : sortedPlayerStats)
			{
				MPlayer mPlayer = MPlayer.get(playerStats.getPlayerId());
				if (mPlayer.getFaction() != faction) playerStatsToRemove.add(playerStats);
			}
			
			sortedPlayerStats.removeAll(playerStatsToRemove);
		}
		
		switch (statistic)
		{
			case "blocksBroken":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getBlocksBroken(), o1.getBlocksBroken()));
				break;
			case "blocksPlaced":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getBlocksPlaced(), o1.getBlocksPlaced()));
				break;
			case "playersKilled":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getPlayersKilled(), o1.getPlayersKilled()));
				break;
			case "mobsKilled":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getMobsKilled(), o1.getMobsKilled()));
				break;
			case "deaths":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getDeaths(), o1.getDeaths()));
				break;
			case "fishCaught":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getFishCaught(), o1.getFishCaught()));
				break;
			case "timePlayed":
				sortedPlayerStats.sort((o1, o2) -> Long.compare(o2.getTimePlayed(), o1.getTimePlayed()));
				break;
		}
		
		for (PlayerStats playerStats : sortedPlayerStats)
		{
			MPlayer mPlayer = MPlayer.get(playerStats.getPlayerId());
			
			String value = "0";
			
			switch (statistic)
			{
				case "blocksBroken":
					value = playerStats.getBlocksBroken().toString();
					break;
				case "blocksPlaced":
					value = playerStats.getBlocksPlaced().toString();
					break;
				case "playersKilled":
					value = playerStats.getPlayersKilled().toString();
					break;
				case "mobsKilled":
					value = playerStats.getMobsKilled().toString();
					break;
				case "deaths":
					value = playerStats.getDeaths().toString();
					break;
				case "fishCaught":
					value = playerStats.getFishCaught().toString();
					break;
				case "timePlayed":
					value = TimeUtil.formatPlayTime(playerStats.getTimePlayed());
					break;
			}
			
			Mson mson = mson(Txt.parse("%s <reset>- <i>%s", mPlayer.describeTo(msender.getFaction(), true), value));
			statsList.add(mson);
		}
		
		String printableStatistic;
		printableStatistic = StringUtils.capitalize(statistic);
		printableStatistic = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(printableStatistic.replaceAll("\\d+", "")), " ");
		
		final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Top %s <reset>(<a>%s<reset>)", printableStatistic, faction == null ? "all" : msender.getRelationTo(faction).getColor() + faction.getName()), page, statsList, (Msonifier<Mson>) (item, index) -> mson(Txt.parse("<a>%s. ", (index + 1))).add(statsList.get(index)));
		
		// Pager Message
		pager.message();
	}
	
}
