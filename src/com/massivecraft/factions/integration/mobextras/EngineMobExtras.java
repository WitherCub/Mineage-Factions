package com.massivecraft.factions.integration.mobextras;

import com.massivecraft.massivecore.Engine;

public class EngineMobExtras extends Engine
{
	private static EngineMobExtras i = new EngineMobExtras();
	
	public static EngineMobExtras get()
	{
		return i;
	}
	
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
	}
}