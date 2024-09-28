package com.massivecraft.factions.cmd.powerboost;

import com.massivecraft.factions.cmd.type.TypeMPlayer;

public class CmdFactionsPowerboostPlayer extends CmdFactionsPowerboostAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPowerboostPlayer()
	{
		super(TypeMPlayer.get(), "player");
	}
	
}
