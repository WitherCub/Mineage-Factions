package com.massivecraft.factions.adapter;

import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonDeserializer;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import com.massivecraft.massivecore.xlib.gson.JsonParseException;
import com.massivecraft.massivecore.xlib.gson.JsonSerializationContext;
import com.massivecraft.massivecore.xlib.gson.JsonSerializer;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public class TerritoryAccessAdapter implements JsonDeserializer<TerritoryAccess>, JsonSerializer<TerritoryAccess>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final String HOST_FACTION_ID = "hostFactionId";
	public static final String HOST_FACTION_ALLOWED = "hostFactionAllowed";
	public static final String FACTION_IDS = "factionIds";
	public static final String PLAYER_IDS = "playerIds";
	public static final String CLAIMTIME = "claimTime";
	public static final String CLAIMEDBY = "claimedBy";
	
	public static final Type SET_OF_STRING_TYPE = new TypeToken<Set<String>>(){}.getType();
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TerritoryAccessAdapter i = new TerritoryAccessAdapter();
	public static TerritoryAccessAdapter get() { return i; }
	
	//----------------------------------------------//
	// OVERRIDE
	//----------------------------------------------//
	
	@Override
	public TerritoryAccess deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		// isDefault <=> simple hostFactionId string
//		if (json.isJsonPrimitive())
//		{
//			String hostFactionId = json.getAsString();
//			return TerritoryAccess.valueOf(hostFactionId);
//		}
		
		// Otherwise object
		JsonObject obj = json.getAsJsonObject();
		
		// Prepare variables
		String hostFactionId = null;
		Boolean hostFactionAllowed = null;
		Set<String> factionIds = null;
		Set<String> playerIds = null;
		Long claimTime = null;
		UUID claimedBy = null;
		
		// Read variables (test old values first)
		JsonElement element = null;
		
		element = obj.get("ID");
		if (element == null) element = obj.get(HOST_FACTION_ID);
		hostFactionId = element.getAsString();
		
		element = obj.get("claimTime");
		if (element == null) element = obj.get(CLAIMTIME);
		claimTime = element.getAsLong();
		
		element = obj.get("open");
		if (element == null) element = obj.get(HOST_FACTION_ALLOWED);
		if (element != null) hostFactionAllowed = element.getAsBoolean();
		
		element = obj.get("factions");
		if (element == null) element = obj.get(FACTION_IDS);
		if (element != null) factionIds = context.deserialize(element, SET_OF_STRING_TYPE);
		
		element = obj.get("fplayers");
		if (element == null) element = obj.get(PLAYER_IDS);
		if (element != null) playerIds = context.deserialize(element, SET_OF_STRING_TYPE);
		
		element = obj.get("claimedBy");
		if (element == null) element = obj.get(CLAIMEDBY);
		if (element != null) claimedBy = UUID.fromString(element.getAsString());
		
		return TerritoryAccess.valueOf(hostFactionId, hostFactionAllowed, factionIds, playerIds, claimTime, claimedBy);
	}
	
	@Override
	public JsonElement serialize(TerritoryAccess src, Type typeOfSrc, JsonSerializationContext context)
	{
		if (src == null) return null;
		
		// isDefault <=> simple hostFactionId string
//		if (src.isDefault())
//		{
//			return new JsonPrimitive(src.getHostFactionId());
//		}
		
		// Otherwise object
		JsonObject obj = new JsonObject();
		
		obj.addProperty(HOST_FACTION_ID, src.getHostFactionId());
		obj.addProperty(CLAIMTIME, src.getClaimTime());
		
		if (src.getClaimedBy() != null)
		{
			obj.addProperty(CLAIMEDBY, src.getClaimedBy().toString());
		}
		
		if (!src.isHostFactionAllowed())
		{
			obj.addProperty(HOST_FACTION_ALLOWED, src.isHostFactionAllowed());
		}
		
		if (!src.getFactionIds().isEmpty())
		{
			obj.add(FACTION_IDS, context.serialize(src.getFactionIds(), SET_OF_STRING_TYPE));
		}
		
		if (!src.getPlayerIds().isEmpty())
		{
			obj.add(PLAYER_IDS, context.serialize(src.getPlayerIds(), SET_OF_STRING_TYPE));
		}
		
		return obj;
	}
	
}