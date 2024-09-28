package com.massivecraft.factions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.collections.MassiveSet;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class TerritoryAccess
{
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	
	// no default value, can't be null
	private final String hostFactionId;
	public String getHostFactionId() { return this.hostFactionId; }
	
	// no default value, can't be null
	private final Long claimTime;
	public Long getClaimTime() { return this.claimTime; }
	
	// default value is null, if null == claimed by console
	private final UUID claimedBy;
	public UUID getClaimedBy() { return this.claimedBy; }
	
	// default is true
	private final boolean hostFactionAllowed;
	public boolean isHostFactionAllowed() { return this.hostFactionAllowed; }
	
	// default is empty
	private final Set<String> factionIds;
	public Set<String> getFactionIds() { return this.factionIds; }
	
	// default is empty
	private final Set<String> playerIds;
	public Set<String> getPlayerIds() { return this.playerIds; }
	
	// -------------------------------------------- //
	// FIELDS: DELTA
	// -------------------------------------------- //
	
	// The simple ones
	public TerritoryAccess withHostFactionId(String hostFactionId) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimedBy); }
	public TerritoryAccess withHostFactionAllowed(Boolean hostFactionAllowed) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimedBy); }
	public TerritoryAccess withFactionIds(Collection<String> factionIds) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimedBy); }
	public TerritoryAccess withPlayerIds(Collection<String> playerIds) { return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimedBy); }
	
	// The intermediate ones
	public TerritoryAccess withFactionId(String factionId, boolean with)
	{
		if (this.getHostFactionId().equals(factionId))
		{
			return valueOf(hostFactionId, with, factionIds, playerIds, claimedBy);
		}
		
		Set<String> factionIds = new MassiveSet<>(this.getFactionIds());
		if (with)
		{
			factionIds.add(factionId);
		}
		else
		{
			factionIds.remove(factionId);
		}
		return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimedBy);
	}
	
	public TerritoryAccess withPlayerId(String playerId, boolean with)
	{
		playerId = playerId.toLowerCase();
		Set<String> playerIds = new MassiveSet<>(this.getPlayerIds());
		if (with)
		{
			playerIds.add(playerId);
		}
		else
		{
			playerIds.remove(playerId);
		}
		return valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimedBy);
	}
	
	// -------------------------------------------- //
	// FIELDS: DIRECT
	// -------------------------------------------- //
	
	// This method intentionally returns null if the Faction no longer exists.
	// In Board we don't even return this TerritoryAccess if that is the case.
	public Faction getHostFaction()
	{
		return Faction.get(this.getHostFactionId());
	}
	
	public Set<MPlayer> getGrantedMPlayers()
	{
		// Create
		Set<MPlayer> ret = new MassiveSet<>();
		
		// Fill
		for (String playerId : this.getPlayerIds())
		{
			ret.add(MPlayer.get(playerId));
		}
		
		// Return
		return ret;
	}
	
	public Set<Faction> getGrantedFactions()
	{
		// Create
		Set<Faction> ret = new MassiveSet<>();
		
		// Fill
		for (String factionId : this.getFactionIds())
		{
			Faction faction = Faction.get(factionId);
			if (faction == null) continue;
			ret.add(faction);
		}
		
		// Return
		return ret;
	}
	
	// -------------------------------------------- //
	// PRIVATE CONSTRUCTOR
	// -------------------------------------------- //
	
	private TerritoryAccess(String hostFactionId, Boolean hostFactionAllowed, Collection<String> factionIds, Collection<String> playerIds, Long claimTime, UUID claimedBy)
	{
		if (hostFactionId == null) throw new IllegalArgumentException("hostFactionId was null");
		this.hostFactionId = hostFactionId;
		
		if (claimTime == null) throw new IllegalArgumentException("claimTime was null");
		this.claimTime = claimTime;
		
		this.claimedBy = claimedBy;
		
		Set<String> factionIdsInner = new MassiveSet<>();
		if (factionIds != null)
		{
			factionIdsInner.addAll(factionIds);
			if (factionIdsInner.remove(hostFactionId))
			{
				hostFactionAllowed = true;
			}
		}
		this.factionIds = Collections.unmodifiableSet(factionIdsInner);
		
		Set<String> playerIdsInner = new MassiveSet<>();
		if (playerIds != null)
		{
			for (String playerId : playerIds)
			{
				playerIdsInner.add(playerId.toLowerCase());
			}
		}
		this.playerIds = Collections.unmodifiableSet(playerIdsInner);
		
		this.hostFactionAllowed = (hostFactionAllowed == null || hostFactionAllowed);
	}
	
	// -------------------------------------------- //
	// FACTORY: VALUE OF
	// -------------------------------------------- //
	
	public static TerritoryAccess valueOf(String hostFactionId, Boolean hostFactionAllowed, Collection<String> factionIds, Collection<String> playerIds, UUID claimedBy)
	{
		return new TerritoryAccess(hostFactionId, hostFactionAllowed, factionIds, playerIds, System.currentTimeMillis(), claimedBy);
	}
	
	public static TerritoryAccess valueOf(String hostFactionId)
	{
		return valueOf(hostFactionId, null, null, null, System.currentTimeMillis(), null);
	}
	
	public static TerritoryAccess valueOf(String hostFactionId, Player player)
	{
		return valueOf(hostFactionId, null, null, null, System.currentTimeMillis(), player.getUniqueId());
	}
	
	public static TerritoryAccess valueOf(String hostFactionId, Boolean hostFactionAllowed, Collection<String> factionIds, Collection<String> playerIds, Long claimTime, UUID claimedBy)
	{
		return new TerritoryAccess(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimTime, claimedBy);
	}
	
	// -------------------------------------------- //
	// INSTANCE METHODS
	// -------------------------------------------- //
	
	public boolean isFactionGranted(Faction faction)
	{
		String factionId = faction.getId();
		
		if (this.getHostFactionId().equals(factionId))
		{
			return this.isHostFactionAllowed();
		}
		
		return this.getFactionIds().contains(factionId);
	}
	
	// Note that the player can have access without being specifically granted.
	// The player could for example be a member of a granted faction.
	public boolean isMPlayerGranted(MPlayer mplayer)
	{
		String mplayerId = mplayer.getId();
		return this.getPlayerIds().contains(mplayerId);
	}
	
	// A "default" TerritoryAccess could be serialized as a simple string only.
	// The host faction is still allowed (default) and no faction or player has been granted explicit access (default).
	public boolean isDefault()
	{
		return this.isHostFactionAllowed() && this.getFactionIds().isEmpty() && this.getPlayerIds().isEmpty();
	}
	
	// -------------------------------------------- //
	// ACCESS STATUS
	// -------------------------------------------- //
	
	public AccessStatus getTerritoryAccess(MPlayer mplayer)
	{
		if (this.isMPlayerGranted(mplayer)) return AccessStatus.ELEVATED;
		
		String factionId = mplayer.getFaction().getId();
		if (this.getFactionIds().contains(factionId)) return AccessStatus.ELEVATED;
		
		if (this.getHostFactionId().equals(factionId) && !this.isHostFactionAllowed()) return AccessStatus.DECREASED;
		
		return AccessStatus.STANDARD;
	}
	
	@Deprecated
	public Boolean hasTerritoryAccess(MPlayer mplayer)
	{
		return this.getTerritoryAccess(mplayer).hasAccess();
	}
	
}