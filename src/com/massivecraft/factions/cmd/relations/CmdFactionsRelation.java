package com.massivecraft.factions.cmd.relations;

import com.massivecraft.factions.cmd.FactionsCommand;

public class CmdFactionsRelation extends FactionsCommand
{
	public CmdFactionsRelation() {
		this.addChild(CmdFactionsRelationSet.get());
		this.addChild(new CmdFactionsRelationList());
		this.addChild(new CmdFactionsRelationWishes());
	}
}
