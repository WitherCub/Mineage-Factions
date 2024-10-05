package com.massivecraft.factions.integration.coreprotect;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.CmdFactionsInspect;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import net.coreprotect.database.Database;
import net.coreprotect.database.Lookup;
import net.coreprotect.model.Config;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EngineCoreProtect extends Engine
{
	
	private static EngineCoreProtect i = new EngineCoreProtect();
	private List<Material> interact_blocks =
		Arrays.asList(Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR,
			Material.DARK_OAK_DOOR, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DISPENSER, Material.NOTE_BLOCK, Material.CHEST,
			Material.FURNACE, Material.BURNING_FURNACE, Material.WOODEN_DOOR, Material.LEVER, Material.STONE_BUTTON, Material.DIODE_BLOCK_OFF,
			Material.DIODE_BLOCK_ON, Material.TRAP_DOOR, Material.FENCE_GATE, Material.BREWING_STAND, Material.WOOD_BUTTON, Material.ANVIL,
			Material.TRAPPED_CHEST, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.HOPPER, Material.DROPPER
		);
	
	public static EngineCoreProtect get()
	{
		return i;
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		MPlayer mPlayer = MPlayer.get(event.getPlayer());
		if (mPlayer.isInspecting()) mPlayer.setInspecting(false);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		MPlayer mPlayer = MPlayer.get(event.getPlayer());
		if (mPlayer.isInspecting()) mPlayer.setInspecting(false);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Block block = event.getClickedBlock();
		
		if (block == null || block.getType() == Material.AIR) return;
		
		MPlayer mPlayer = MPlayer.get(event.getPlayer());
		
		if (mPlayer.isInspecting())
		{
			event.setCancelled(true);
			
			if (mPlayer.getFaction() == null || mPlayer.getFaction().isSystemFaction())
			{
				mPlayer.setInspecting(false);
				mPlayer.msg("<i>Inspecting has been <b>disabled<i>.");
				return;
			}
			
			if ((System.currentTimeMillis() - mPlayer.getLastInspectionTime()) <= 3000L)
			{
				mPlayer.msg("<b>Please do not spam inspections.");
				return;
			}
			
			mPlayer.setLastInspectionTime(System.currentTimeMillis());
			
			Faction factionAt = BoardColl.get().getFactionAt(PS.valueOf(block));
			
			if (factionAt == null || factionAt.isSystemFaction() || !MPerm.getPermInspect().has(mPlayer, factionAt, false))
			{
				mPlayer.msg("<i>You can only inspect land where you are granted the permission!");
				return;
			}
			
			// Handle CoreProtect checks
			Location locationToCheck = block.getLocation();
			
			if (event.getPlayer().getItemInHand().getType().isBlock() && event.getPlayer().getItemInHand().getType() != Material.AIR)
			{
				locationToCheck = block.getRelative(event.getBlockFace()).getLocation();
			}
			
			handleInspection(mPlayer, locationToCheck);
		}
	}
	
	private boolean isReadyToInspect()
	{
		if (!IntegrationCoreProtect.get().isActive()) return false;
		if (Config.converter_running) return false;
		if (Config.purge_running) return false;
		return Database.getConnection(false) != null;
	}
	
	private void handleInspection(MPlayer mPlayer, Location clickedBlock)
	{
		if (!mPlayer.isInspecting()) return;
		
		if (!IntegrationCoreProtect.get().isActive())
		{
			mPlayer.msg("<b>This feature is currently disabled. Please contact an administrator if you believe this is a mistake.");
			return;
		}
		
		Rel rel = mPlayer.getRole();
		
		if (!rel.isAtLeast(Rel.OFFICER))
		{
			mPlayer.setInspecting(false);
			mPlayer.msg("<i>Inspecting has been <b>disabled<i>.");
			return;
		}
		
		if (clickedBlock == null || clickedBlock.getBlock().getType() == Material.AIR)
		{
			mPlayer.msg("<b>You can not inspect that block.");
			return;
		}
		
		
		if (!isReadyToInspect())
		{
			mPlayer.setInspecting(false);
			mPlayer.msg("<b>You may not use /f inspect at this time. Please try again later!");
			return;
		}
		
		Connection connection = Database.getConnection(false);
		
		if (connection == null)
		{
			mPlayer.setInspecting(false);
			mPlayer.msg("<b>You may not use /f inspect at this time. Please try again later!");
			return;
		}
		
		try
		{
			Statement statement = connection.createStatement();
			
			Block block = clickedBlock.getBlock();
			String blockdata;
			
			if (interact_blocks.contains(block.getType()))
			{
				blockdata = Lookup.chest_transactions(statement, clickedBlock, mPlayer.getPlayer().getName(), 1, MConf.get().coreprotectInspectLimit);
			}
			else
			{
				blockdata = Lookup.block_lookup(statement, block, mPlayer.getPlayer().getName(), 0, 1, MConf.get().coreprotectInspectLimit);
			}
			
			if (blockdata.contains("\n"))
			{
				mPlayer.setLastInspectedBlockdata(blockdata);
				
				final List<Mson> inspectedItems = new ArrayList<>();
				
				String[] arrayOfString;
				int j = (arrayOfString = blockdata.split("\n")).length;
				int index = 0;
				for (int i = 0; i < j; i++)
				{
					if (index == 0)
					{
						index++;
						continue;
					}
					
					index++;
					
					String b = arrayOfString[i].replace("CoreProtect", "")
								   .replace(ChatColor.translateAlternateColorCodes('&', "&3"), ChatColor.YELLOW + "")
								   .replace(ChatColor.translateAlternateColorCodes('&', "&f"), ChatColor.WHITE + "");
					
					if (b.contains("older data") || b.contains("page")) continue;
					if (b.contains(ChatColor.WHITE + "-----")) continue;
					
					String[] split = b.split("-");
					
					b = ChatColor.DARK_GRAY + "»" + split[1].replace(".", "") + ChatColor.DARK_GRAY + " - " + split[0];
					
					inspectedItems.add(Mson.mson(b));
				}
				
				final Pager<Mson> pager = new Pager<>(CmdFactionsInspect.get(), Txt.parse("<i>Inspect Log"), 1, inspectedItems, (Msonifier<Mson>) (mson, i) -> inspectedItems.get(i));
				pager.setSender(mPlayer.getSender());
				pager.message();
			}
			else
			{
				mPlayer.setLastInspectedBlockdata(Txt.parse("<i>No data was found for that block."));
				mPlayer.msg("<i>No data was found for that block.");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			
			mPlayer.setInspecting(false);
			mPlayer.msg("<b>You may not use /f inspect at this time. Please try again later. Please contact an administrator.");
		}
	}
	
}