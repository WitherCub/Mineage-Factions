package com.massivecraft.factions.integration.mobextras;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationMobExtras extends Integration
{
	private static IntegrationMobExtras i = new IntegrationMobExtras();
	
	private IntegrationMobExtras()
	{
		this.setPluginName("MobExtras");
	}
	
	public static IntegrationMobExtras get()
	{
		return i;
	}
	
	@Override
	public Engine getEngine()
	{
		return EngineMobExtras.get();
	}
}