package com.massivecraft.factions.entity.objects;

import org.bukkit.inventory.ItemStack;

public class ChestTransaction
{
	
	private String playerId;
	private long timeOfExchange;
	private ItemStack itemStack;
	
	public ChestTransaction(String playerId, ItemStack itemStack)
	{
		this.playerId = playerId;
		this.timeOfExchange = System.currentTimeMillis();
		this.itemStack = itemStack;
	}
	
	public String getPlayerId()
	{
		return playerId;
	}
	
	public long getTimeOfExchange()
	{
		return timeOfExchange;
	}
	
	public ItemStack getItemStack()
	{
		return itemStack;
	}
}
