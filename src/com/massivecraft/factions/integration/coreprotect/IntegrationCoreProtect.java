package com.massivecraft.factions.integration.coreprotect;

import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.Integration;

public class IntegrationCoreProtect extends Integration {
	
	private IntegrationCoreProtect() {
		this.setPluginName("CoreProtect");
	}
	
	private static IntegrationCoreProtect i = new IntegrationCoreProtect();
	
	public static IntegrationCoreProtect get() {
		return i;
	}
	
	@Override
	public Engine getEngine() { return EngineCoreProtect.get(); }
	
}