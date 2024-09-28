package com.massivecraft.factions;

public enum ChatMode
{
	FACTION(3, "faction chat"),
	ALLIANCE(2, "ally chat"),
	TRUCE(1, "truce chat"),
	PUBLIC(0, "public chat");
	
	public final int value;
	public final String nicename;
	
	ChatMode(int value, String nicename)
	{
		this.value = value;
		this.nicename = nicename;
	}
	
	public boolean isAtLeast(ChatMode role)
	{
		return this.value >= role.value;
	}
	
	public boolean isAtMost(ChatMode role)
	{
		return this.value <= role.value;
	}
	
	public String toString()
	{
		return this.nicename;
	}
	
	public ChatMode getNext()
	{
		return this == PUBLIC ? ALLIANCE : (this == ALLIANCE ? FACTION : PUBLIC);
	}
}