package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.CmdFactionsMap;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.TypeWorld;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanTrue;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Set;

public class CmdFactionsSetOne extends CmdFactionsSetXSimple
{
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetOne(boolean claim)
	{
		// Super
		super(claim);
		
		// Aliases
		this.addAliases("one");
		
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
		Perm perm = claim ? Perm.CLAIM_ONE : Perm.UNCLAIM_ONE;
		this.addRequirements(RequirementHasPerm.get(perm));
		
		// Parameters
		this.addParameter(TypeInteger.get(), "chunkX", "your X");
		this.addParameter(TypeInteger.get(), "chunkZ", "your Z");
		this.addParameter(TypeWorld.get(), "world", "your world");
		this.addParameter(TypeBooleanTrue.get(), "sendmap", "false");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks() throws MassiveException
	{
		int chunkX = this.readArg(me.getLocation().getChunk().getX());
		int chunkZ = this.readArg(me.getLocation().getChunk().getZ());
		int abX = Math.abs(chunkX);
		int abZ = Math.abs(chunkZ);
		int acAbX = Math.abs(me.getLocation().getChunk().getX());
		int acAbZ = Math.abs(me.getLocation().getChunk().getZ());
		int difX = Math.abs(acAbX - abX);
		int difZ = Math.abs(acAbZ - abZ);
		World world = this.readArg(me.getLocation().getWorld());
		
		if (this.isClaim() && me.getLocation().getWorld() != world) {
			msg(ChatColor.RED + "You're too far from the intended location to manage claims.");
			return Collections.emptySet();
		}
		
		if (this.isClaim() && (!(difX <= MConf.get().claimChunkMaxDis && difZ <= MConf.get().claimChunkMaxDis)))
		{
			msg(ChatColor.RED + "You're too far from the intended location to manage claims.");
			return Collections.emptySet();
		}
		else
		{
			final PS chunk = PS.valueOf(chunkX, chunkZ).withWorld(world.getName()).getChunk(true);
			
			final boolean sendMap = this.readArg(false);
			
			if (sendMap)
			{
				new BukkitRunnable()
				{
					final CommandSender commandSender = sender;
					
					@Override
					public void run()
					{
						CmdFactionsMap.get().execute(commandSender, Collections.<String>emptyList());
					}
				}.runTaskLater(Factions.get(), 1L);
			}
			
			return Collections.singleton(chunk);
		}
	}
	
}
