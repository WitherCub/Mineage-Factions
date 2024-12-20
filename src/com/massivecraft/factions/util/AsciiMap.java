package com.massivecraft.factions.util;

import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.massivecraft.massivecore.mson.Mson.EMPTY;
import static com.massivecraft.massivecore.mson.Mson.SPACE;
import static com.massivecraft.massivecore.mson.Mson.mson;

public class AsciiMap
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	private static final char[] FACTION_KEY_CHARS = "\\/#?ç¬£$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZÄÖÜÆØÅ1234567890abcdeghjmnopqrsuvwxyÿzäöüæøåâêîûô".toCharArray();
	private static final int KEY_SIZE = FACTION_KEY_CHARS.length;
	
	// Map Heights & Widths
	private static final int WIDTH = 49;
	private static final int WIDTH_HALF = WIDTH / 2;
	private static final int HEIGHT = 8;
	private static final int HEIGHT_HALF = HEIGHT / 2;
	private static final int HEIGHT_EXTRA = 17;
	private static final int HEIGHT_EXTRA_HALF = HEIGHT_EXTRA / 2;
	
	private static final String TITLE_FORMAT = "(%d,%d) %s";
	private static final Mson KEY_BORDER = mson("-").color(ChatColor.DARK_GRAY);
	private static final Mson KEY_MIDDLE = mson("+").color(ChatColor.AQUA);
	private static final Mson KEY_WILDERNESS = mson("-").color(ChatColor.GRAY).tooltip();
	private static final Mson KEY_OVERFLOW = mson("-").style(ChatColor.MAGIC).add(mson("").style(ChatColor.RESET));
	private static final Mson OVERFLOW_MESSAGE = Mson.format("%s: Too Many Factions (>%d) on this Map.", KEY_OVERFLOW.toPlain(true), FACTION_KEY_CHARS.length);
	private static final Mson LEGEND_SEPARATOR = mson(": ");
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final RelationParticipator relationParticipator;
	private final double angle;
	private final PS center;
	private final PS topLeft;
	private final Board board;
	private final Map<Faction, Mson> factionChars = new HashMap<>();
	private final int height;
	private final int heightHalf;
	private boolean overflown = false;
	
	public AsciiMap(RelationParticipator relationParticipator, Player player, boolean extraHeight)
	{
		this.relationParticipator = relationParticipator;
		Location location = player.getLocation();
		this.angle = location.getYaw();
		this.center = PS.valueOf(location).getChunk(true);
		this.height = extraHeight ? HEIGHT_EXTRA : HEIGHT;
		this.heightHalf = extraHeight ? HEIGHT_EXTRA_HALF : HEIGHT_HALF;
		this.topLeft = this.center.plusChunkCoords(-WIDTH_HALF, -this.heightHalf);
		this.board = BoardColl.get().get(this.center);
	}
	
	public RelationParticipator getRelationParticipator()
	{
		return this.relationParticipator;
	}
	
	public double getAngle()
	{
		return this.angle;
	}
	
	public PS getCenter()
	{
		return this.center;
	}
	
	public PS getTopLeft()
	{
		return this.topLeft;
	}
	
	public Board getBoard()
	{
		return this.board;
	}
	
	public Map<Faction, Mson> getFactionChars()
	{
		return this.factionChars;
	}
	
	private int getHeight()
	{
		return this.height;
	}
	
	private int getHeightHalf()
	{
		return this.heightHalf;
	}
	
	public boolean isOverflown()
	{
		return this.overflown;
	}
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public void setOverflown(boolean overflown)
	{
		this.overflown = overflown;
	}
	
	// -------------------------------------------- //
	// RENDER
	// -------------------------------------------- //
	
	public List<Mson> render()
	{
		// Create
		List<Mson> ret = new ArrayList<>();
		
		// Fill
		ret.add(this.getTitle());
		ret.addAll(this.getLines());
		ret.add(this.getFactionLegend());
		
		// Return
		return ret;
	}
	
	private Mson getTitle()
	{
		// Prepare
		PS chunk = this.getCenter();
		Faction faction = this.getBoard().getFactionAt(chunk);
		int chunkX = chunk.getChunkX();
		int chunkZ = chunk.getChunkZ();
		String factionName = faction.getName(this.getRelationParticipator());
		
		// Titleize
		return Txt.titleize(String.format(TITLE_FORMAT, chunkX, chunkZ, factionName));
	}
	
	private List<Mson> getLines()
	{
		// Create
		List<Mson> ret = new MassiveList<>();
		List<String> asciiCompass = AsciiCompass.getAsciiCompass(this.getAngle());
		
		// Fill
		for (int deltaZ = 0; deltaZ < this.getHeight(); deltaZ++)
		{
			ret.add(this.getLine(deltaZ, asciiCompass));
		}
		
		// Return
		return ret;
	}
	
	private Mson getLine(int deltaZ, List<String> asciiCompass)
	{
		// Create
		boolean isCompassLine = deltaZ < asciiCompass.size();
		int startX = isCompassLine ? 3 : 0;
		
		Mson ret = isCompassLine ? mson(asciiCompass.get(deltaZ)) : EMPTY;
		
		// Fill
		for (int deltaX = startX; deltaX < WIDTH; ++deltaX)
		{
			boolean isMiddle = deltaX == WIDTH_HALF && deltaZ == this.getHeightHalf();
			PS herePs = this.getTopLeft().plusChunkCoords(deltaX, deltaZ);
			Board board = BoardColl.get().get(herePs);
			Faction hereFaction = board.getFactionAt(herePs);
			Mson factionChar = isMiddle ? KEY_MIDDLE : this.getCharFaction(hereFaction);
			
			if (isMiddle)
			{
				ret = ret.add(factionChar);
			}
			else if (Board.get(herePs).isChunkOutsideBorder(herePs))
			{
				ret = ret.add(KEY_BORDER);
			}
			else
			{
				MPlayer mplayer = MPlayer.get(getRelationParticipator());
				ChunkPos chunkPos = new ChunkPos(mplayer.getPlayer().getWorld().getName(), herePs);
				
				if (!mplayer.isMapClickToClaim() || mplayer.getFaction().isNone())
				{
					if (!hereFaction.isNone() && hereFaction.getSpawnerChunks().contains(chunkPos))
					{
						ret = ret.add(factionChar.color(ChatColor.BLUE).tooltip("Spawner chunk (" + ChatColor.stripColor(hereFaction.getName()) + ")"));
					}
					if (!hereFaction.isNone() && hereFaction.getBaseRegionPs().contains(chunkPos.getPS()))
					{
						ret = ret.add(factionChar.color(ChatColor.YELLOW).tooltip("Base chunk (" + ChatColor.stripColor(hereFaction.getName()) + ")"));
					}
					else
					{
						ret = ret.add(factionChar.tooltip("Claimed by " + ChatColor.stripColor(hereFaction.getName())));
					}
				}
				else if (!hereFaction.isNone() && getRaidclaim(hereFaction, herePs) != null)
				{
					ret = ret.add(factionChar.color(ChatColor.BLUE).tooltip("Raid claim (" + ChatColor.stripColor(hereFaction.getName()) + ")", "Click to unclaim " + herePs.getChunkX() + ":" + herePs.getChunkZ())).command("/f unclaim one " + herePs.getChunkX() + " " + herePs.getChunkZ() + " true");
				}
				else if (!hereFaction.isNone() && hereFaction.getSpawnerChunks().contains(chunkPos))
				{
					ret = ret.add(factionChar.color(ChatColor.BLUE).tooltip("Spawner chunk (" + ChatColor.stripColor(hereFaction.getName()) + ")"));
				}
				else if (!hereFaction.isNone() && hereFaction.getBaseRegionPs().contains(chunkPos.getPS()))
				{
					ret = ret.add(factionChar.color(ChatColor.YELLOW).tooltip("Base chunk (" + ChatColor.stripColor(hereFaction.getName()) + ")"));
				}
				else if (mplayer.getFaction() == hereFaction)
				{
					ret = ret.add(factionChar.tooltip("Click to unclaim " + herePs.getChunkX() + ":" + herePs.getChunkZ()).command("/f unclaim one " + herePs.getChunkX() + " " + herePs.getChunkZ() + " true"));
				}
				else if (hereFaction.isNone())
				{
					ret = ret.add(KEY_WILDERNESS.tooltip("Click to claim " + herePs.getChunkX() + ":" + herePs.getChunkZ()).command("/f claim one " + mplayer.getFaction().getName() + " " + herePs.getChunkX() + " " + herePs.getChunkZ() + " true"));
				}
//				else if (hereFaction.getLandCount() > hereFaction.getPower())
//				{
//					ret = ret.add(factionChar.tooltip("Claimed by " + ChatColor.stripColor(hereFaction.getName()), "Click to overclaim " + herePs.getChunkX() + ":" + herePs.getChunkZ()).command("/f claim one " + mplayer.getFaction().getName() + " " + herePs.getChunkX() + " " + herePs.getChunkZ() + " true"));
//				}
				else
				{
					ret = ret.add(factionChar.tooltip("Claimed by " + ChatColor.stripColor(hereFaction.getName())));
				}
			}
		}
		
		// Return
		return ret;
	}
	
	private Mson getCharFaction(Faction hereFaction)
	{
		// Calculate overflow
		int index = this.getFactionChars().size();
		if (!this.isOverflown() && index >= KEY_SIZE) this.setOverflown(true);
		
		Mson factionChar = this.getFactionChars().get(hereFaction);
		
		// Is Wilderness or known?
		if (hereFaction.isNone()) return KEY_WILDERNESS;
		if (factionChar != null) return factionChar;
		
		// Create descriptions
		ChatColor color = hereFaction.getColorTo(this.getRelationParticipator());
		String name = hereFaction.getName(this.getRelationParticipator());
		String tooltip = color.toString() + name;
		
		// Is overflown?
		if (this.isOverflown()) return KEY_OVERFLOW.tooltip(tooltip);
		
		// Create new one
		factionChar = mson(String.valueOf(FACTION_KEY_CHARS[index])).color(color);
		factionChar = factionChar.tooltip(tooltip);
		
		// Store for later use
		this.getFactionChars().put(hereFaction, factionChar);
		
		// Return
		return factionChar;
	}
	
	private Mson getFactionLegend()
	{
		// Create
		List<Mson> ret = new MassiveList<>();
		
		// Fill
		for (Entry<Faction, Mson> entry : this.getFactionChars().entrySet())
		{
			Faction here = entry.getKey();
			Mson factionChar = entry.getValue();
			ChatColor color = here.getColorTo(this.getRelationParticipator());
			
			ret.add(mson(factionChar, LEGEND_SEPARATOR, here.getName()).color(color));
		}
		
		// Add overflown message if needed
		if (this.isOverflown()) ret.add(OVERFLOW_MESSAGE);
		
		// Return
		return Mson.implode(ret, SPACE);
	}

	public Integer getRaidclaim(Faction hereFaction, PS herePs) {
		Integer raidclaim = null;

		int bound = MConf.get().raidClaimsAvailable + 1;
		for (int i1 = 1; i1 < bound; i1++) {
			if (!hereFaction.isRaidClaimAvailable(i1) && hereFaction.getRaidClaims().get(i1).contains(herePs)) {
				raidclaim = i1;
			}
		}

		return raidclaim;
	}
	
}