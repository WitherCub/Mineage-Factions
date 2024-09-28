package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.store.EntityInternal;
import org.bukkit.Material;

public class ConfMission extends EntityInternal<ConfMission>
{
	
	private String missionNameDontChange;
	private String missionDisplayName;
	private String missionDescription;
	private Double missionRequirementComplete;
	private Integer creditsReward;
	private Material missionGuiIconMaterial;
	private Integer missionGuiIconDurability;
	
	public ConfMission(String missionNameDontChange, String missionDisplayName, String missionDescription, Double missionRequirementComplete, Integer creditsReward, Material missionGuiIconMaterial, Integer missionGuiIconDurability)
	{
		this.missionNameDontChange = missionNameDontChange;
		this.missionDisplayName = missionDisplayName;
		this.missionDescription = missionDescription;
		this.missionRequirementComplete = missionRequirementComplete;
		this.creditsReward = creditsReward;
		this.missionGuiIconMaterial = missionGuiIconMaterial;
		this.missionGuiIconDurability = missionGuiIconDurability;
	}
	
	public String getMissionDisplayName()
	{
		return missionDisplayName;
	}
	
	public String getMissionDescription()
	{
		return missionDescription;
	}
	
	public Double getMissionRequirementComplete()
	{
		return missionRequirementComplete;
	}
	
	public Integer getCreditsReward()
	{
		return creditsReward;
	}
	
	public Material getMissionGuiIconMaterial()
	{
		return missionGuiIconMaterial;
	}
	
	public Integer getMissionGuiIconDurability()
	{
		return missionGuiIconDurability;
	}
	
	public String getMissionNameDontChange()
	{
		return missionNameDontChange;
	}
}