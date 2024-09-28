package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CmdFactionsSetLine extends CmdFactionsSetX {
	
	public static final BlockFace[] axis = new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};
	
	public CmdFactionsSetLine(boolean claim) {
		// Super
		super(claim);
		
		// Aliases
		this.addAliases("line");
		
		// Parameters
		addParameter(TypeInteger.get(), "amount");
		addParameter(null, TypeString.get(), "direction");
		if (claim) {
			addParameter(TypeFaction.get(), "faction", "you");
			setFactionArgIndex(2);
		}
	}
	
	public Integer getRadius() throws MassiveException {
		int radius = readArgAt(0);
		if(radius < 1) {
			msg("<b>If you specify an amount, it must be at least 1.");
			return null;
		} else if(radius > MConf.get().setLineMax && !msender.isOverriding()) {
			msg("<b>The maximum amount allowed is <h>%s<b>.", MConf.get().setLineMax);
			return null;
		} else {
			return radius;
		}
	}
	
	private BlockFace getDirection() throws MassiveException {
		String direction = readArgAt(1);
		BlockFace blockFace;
		if(direction == null) {
			blockFace = axis[Math.round(this.me.getLocation().getYaw() / 90.0F) & 3];
		} else if(direction.equalsIgnoreCase("north")) {
			blockFace = BlockFace.NORTH;
		} else if(direction.equalsIgnoreCase("east")) {
			blockFace = BlockFace.EAST;
		} else if(direction.equalsIgnoreCase("south")) {
			blockFace = BlockFace.SOUTH;
		} else {
			if(!direction.equalsIgnoreCase("west")) {
				msender.msg(ChatColor.LIGHT_PURPLE + direction + ChatColor.RED + " is not a valid direction!");
				return null;
			}
			blockFace = BlockFace.WEST;
		}
		return blockFace;
	}
	
	public Set<PS> getChunks() throws MassiveException {
		if(getDirection() != null && getRadius() != null) {
			PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
			LinkedHashSet<PS> chunks = new LinkedHashSet<>();
			chunks.add(chunk);
			Location location = me.getLocation();
			
			for(int i = 0; i < getRadius() - 1; ++i) {
				location = location.add((double)(getDirection().getModX() * 16), 0.0D, (double)(getDirection().getModZ() * 16));
				chunks.add(PS.valueOf(location).getChunk(true));
			}
			return chunks;
		} else {
			return Collections.emptySet();
		}
	}
	
}