package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CmdFactionsListclaims extends FactionsCommand
{
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsListclaims()
	{
		this.addAliases("showclaims");
		
		// Parameters
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		int page = this.readArg();
		
		Faction faction = this.readArg(msenderFaction);
		
		if (MPerm.getPermListclaims().has(msender, faction, true))
		{
			// Pager Create
			Set<PS> claims = BoardColl.get().getChunks(faction);
			
			final List<Mson> claimList = new ArrayList<Mson>();
			
			for (PS claim : claims)
			{
				UUID uuid = BoardColl.get().getTerritoryAccessAt(claim).getClaimedBy();
				
				long claimedMillis = BoardColl.get().getTerritoryAccessAt(claim).getClaimTime();
				String claimedAt = DateFormatUtils.format(claimedMillis, "dd MMM HH:mm:ss");
				
				Mson mson = mson(Txt.parse("<k>" + claim.getChunkCoords().getChunkX(true) + ", " + claim.getChunkCoords().getChunkZ(true) + " in " + claim.getWorld(true) + "<i> by " + (uuid == null ? "<gold>Console" : MPlayer.get(uuid).describeTo(msender, true)) + " <i>on<v> " + claimedAt + "<i>."));
				mson = mson.tooltip(Txt.parse("<i>Block coordinates: <h>x:" + claim.getChunkCoords().getChunkX() * 16 + ", z:" + claim.getChunkCoords().getChunkZ() * 16));
				mson = mson.suggest("/f unclaim one " + claim.getChunkCoords().getChunkX() + " " + claim.getChunkCoords().getChunkZ() + " " + claim.getWorld());
				claimList.add(mson);
			}
			
			final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Claims for %s", faction.getName(msender)), page, claimList, (Msonifier<Mson>) (mson, i) -> claimList.get(i));
			
			// Pager Message
			pager.message();
			msender.msg(Txt.parse("<n>Click any claim to remotely unclaim it (confirmation required)"));
		}
	}
	
}