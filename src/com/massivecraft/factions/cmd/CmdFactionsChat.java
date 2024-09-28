package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ChatMode;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

public class CmdFactionsChat extends FactionsCommand
{
	
	public CmdFactionsChat()
	{
		this.addAliases("c");
		this.addParameter(TypeString.get(), "mode", "next");
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	@Override
	public void perform() throws MassiveException
	{
		String modeString = this.readArgAt(0);
		
		ChatMode modeTarget = msender.getChatMode().getNext();
		
		if (modeString != null)
		{
			modeString = modeString.toLowerCase();
			if (modeString.startsWith("p"))
			{
				modeTarget = ChatMode.PUBLIC;
			}
			else if (modeString.startsWith("a"))
			{
				modeTarget = ChatMode.ALLIANCE;
			}
			else if (modeString.startsWith("f"))
			{
				modeTarget = ChatMode.FACTION;
			}
			else if (modeString.startsWith("t"))
			{
				modeTarget = ChatMode.TRUCE;
			}
			else if (modeString.startsWith("spy") && me.hasPermission("factions.chatspy"))
			{
				boolean value = !msender.isSpyingChat();
				
				if (value)
				{
					msender.msg("<i>Chat spy enabled!");
				}
				else
				{
					msender.msg("<i>Chat spy disabled!");
				}
				
				msender.setSpyingChat(value);
				
				return;
			}
			else
			{
				msender.msg("<b>Unknown chat mode! Please enter either \'f\', \'a\', \'t\', or \'p\'");
				return;
			}
		}
		
		msender.setChatMode(modeTarget);
		
		if (msender.getChatMode() == ChatMode.PUBLIC)
		{
			msender.msg("<i>Public chat mode.");
		}
		else if (msender.getChatMode() == ChatMode.ALLIANCE)
		{
			msender.msg("<i>Alliance only chat mode.");
		}
		else if (msender.getChatMode() == ChatMode.TRUCE)
		{
			msender.msg("<i>Truce only chat mode.");
		}
		else
		{
			msender.msg("<i>Faction only chat mode.");
		}
	}
	
}