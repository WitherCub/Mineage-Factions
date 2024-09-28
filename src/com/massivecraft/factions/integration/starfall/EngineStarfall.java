package com.massivecraft.factions.integration.starfall;

import com.massivecraft.massivecore.Engine;

public class EngineStarfall extends Engine
{
	private static EngineStarfall i = new EngineStarfall();
	
	public static EngineStarfall get()
	{
		return i;
	}
	
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
	}
}