package com.massivecraft.factions.engine;

import com.massivecraft.factions.ChatMode;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;

public class EngineFactionChat extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineFactionChat i = new EngineFactionChat();
	
	public static EngineFactionChat get()
	{
		return i;
	}
	
	// -------------------------------------------- //
	// FACTIONS CHAT
	// -------------------------------------------- //
	
	// this is for handling slashless command usage and faction/alliance chat, set at lowest priority so Factions gets to them first
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerEarlyChat(AsyncPlayerChatEvent event)
	{
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		MPlayer me = MPlayer.get(talkingPlayer);
		ChatMode chat = me.getChatMode();
		Faction myFaction = me.getFaction();
		
		if (myFaction.isNone())
		{
			if (chat != ChatMode.PUBLIC)
			{
				me.setChatMode(ChatMode.PUBLIC);
				MixinMessage.get().msgOne(me, "<i>Public chat mode.");
			}
			
			return;
		}
		
		// Is it a faction chat message?
		if (chat == ChatMode.FACTION)
		{
			String message = String.format(MConf.factionChatFormat, me.describeTo(myFaction), msg);
			
			for (MPlayer mPlayer : MPlayerColl.get().getAllOnline())
			{
				if (mPlayer.getFaction().isNone()) continue;
				
				if (myFaction.getId().equals(mPlayer.getFaction().getId()))
				{
					if (!mPlayer.hasPlayerIgnored(me.getPlayer()))
					{
						mPlayer.msg(message);
					}
				}
			}
			
			Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("FactionChat " + myFaction.getName() + ": " + message));
			
			//Send to any players who are spying chat
			for (MPlayer mPlayer : MPlayerColl.get().getAllOnline())
			{
				if (mPlayer.isSpyingChat() && mPlayer.getFaction() != myFaction && me != mPlayer)
				{
					mPlayer.msg(ChatColor.GREEN + "[Spy] " + myFaction.getName() + ": " + message);
				}
			}
			
			event.setCancelled(true);
		}
		else if (chat == ChatMode.ALLIANCE)
		{
			String message = String.format(MConf.allianceChatFormat, ChatColor.stripColor(me.getNameAndFactionName()), msg);
			
			//Send message to our own faction
			for (MPlayer mPlayer : MPlayerColl.get().getAllOnline())
			{
				if (mPlayer.getFaction().isNone()) continue;
				
				if (myFaction.getId().equals(mPlayer.getFaction().getId()))
				{
					if (!mPlayer.hasPlayerIgnored(me.getPlayer()))
					{
						mPlayer.msg(message);
					}
				}
			}
			
			//Send to all our allies
			for (MPlayer mPlayer : MPlayerColl.get().getAllOnline())
			{
				if (myFaction.getRelationTo(mPlayer) == Rel.ALLY)
				{
					if (!mPlayer.hasPlayerIgnored(me.getPlayer()))
					{
						mPlayer.msg(message);
					}
				}
				else if (mPlayer.isSpyingChat() && me != mPlayer)
				{
					mPlayer.msg(ChatColor.DARK_PURPLE + "[Spy]: " + message);
				}
			}
			
			Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("AllianceChat: " + message));
			
			event.setCancelled(true);
		}
		else if (chat == ChatMode.TRUCE)
		{
			String message = String.format(MConf.truceChatFormat, ChatColor.stripColor(me.getNameAndFactionName()), msg);
			
			//Send message to our own faction
			for (MPlayer mPlayer : MPlayerColl.get().getAllOnline())
			{
				if (mPlayer.getFaction().isNone()) continue;
				
				if (myFaction.getId().equals(mPlayer.getFaction().getId()))
				{
					if (!mPlayer.hasPlayerIgnored(me.getPlayer()))
					{
						mPlayer.msg(message);
					}
				}
			}
			
			//Send to all our truces
			for (MPlayer mPlayer : MPlayerColl.get().getAllOnline())
			{
				if (myFaction.getRelationTo(mPlayer) == Rel.TRUCE)
				{
					if (!mPlayer.hasPlayerIgnored(me.getPlayer()))
					{
						mPlayer.msg(message);
					}
				}
				else if (mPlayer.isSpyingChat() && mPlayer != me)
				{
					mPlayer.msg(ChatColor.LIGHT_PURPLE + "[Spy]: " + message);
				}
			}
			
			Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("TruceChat: " + message));
			event.setCancelled(true);
		}
	}
	
}