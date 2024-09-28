package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerStats;
import com.massivecraft.factions.entity.objects.PlayerStats;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsStats extends FactionsCommand
{
	
	public CmdFactionsStats()
	{
		this.addParameter(TypeMPlayer.get(), "player", "you");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		MPlayer mplayer = this.readArg(msender);
		if (!mplayer.isPlayer()) return;
		
		// INFO: Title
		message(Txt.titleize(Txt.parse("<i>Statistics %s", mplayer.describeTo(msender))));
		
		PlayerStats playerStats = MPlayerStats.get().getPlayerStats(mplayer);
		
		msg("<a>Blocks Placed: <v>%s", playerStats.getBlocksPlaced());
		msg("<a>Blocks Broken: <v>%s", playerStats.getBlocksBroken());
		msg("<a>Deaths: <v>%s", playerStats.getDeaths());
		msg("<a>Fish Caught: <v>%s", playerStats.getFishCaught());
		msg("<a>Mobs Killed: <v>%s", playerStats.getMobsKilled());
		msg("<a>Players Killed: <v>%s", playerStats.getPlayersKilled());
		msg("<a>Time Played: <v>%s", TimeUtil.formatPlayTime(playerStats.getTimePlayed()));
	}
	
}