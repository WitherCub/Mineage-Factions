package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

public class CmdFactionsSetdiscord extends FactionsCommand
{
	
	public CmdFactionsSetdiscord()
	{
		this.addParameter(TypeString.get(), "discord", "");
		
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		String discord = this.readArg(null);
		
		if (discord == null) {
			msenderFaction.setFactionDiscord(null);
			msender.msg(LangConf.get().discordUrlRemoved);
			return;
		}
		
		if (discord.length() < MConf.get().minFactionDiscordLength || discord.length() > MConf.get().maxFactionDiscordLength)
		{
			msender.msg(LangConf.get().factionDiscordInvalidLengthMsg.replace("%minCharacters%", String.valueOf(MConf.get().minFactionDiscordLength)).replace("%maxCharacters%", String.valueOf(MConf.get().maxFactionDiscordLength)));
			return;
		}
		
		if (!isValidString(discord))
		{
			msender.msg(LangConf.get().notValidDiscordMsg);
			return;
		}
		
		msenderFaction.setFactionDiscord(discord);
		msender.msg(LangConf.get().discordUrlUpdated);
	}
	
	private boolean isValidString(String string)
	{
		for (char c : string.toCharArray())
		{
			if (!MiscUtil.substanceChars.contains(String.valueOf(c)) && !String.valueOf(c).equalsIgnoreCase("/") && !String.valueOf(c).equalsIgnoreCase("."))
			{
				return false;
			}
		}
		return true;
	}
	
}
