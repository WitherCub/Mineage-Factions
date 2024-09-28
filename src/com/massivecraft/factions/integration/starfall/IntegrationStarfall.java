package com.massivecraft.factions.integration.starfall;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationStarfall extends Integration
{
	private static IntegrationStarfall i = new IntegrationStarfall();
	
	private IntegrationStarfall()
	{
		this.setPluginName("Starfall");
	}
	
	public static IntegrationStarfall get()
	{
		return i;
	}
	
	@Override
	public Engine getEngine()
	{
		return EngineStarfall.get();
	}
}
