package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFactionNameStrict;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.NameReserves;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CmdFactionsReserve extends FactionsCommand
{
	
	public CmdFactionsReserve()
	{
		this.addRequirements(RequirementHasPerm.get(Perm.RESERVE));
		this.addParameter(TypeFactionNameStrict.get(), "name");
		this.addParameter(TypeString.get(), "player");
		this.setVisibility(Visibility.SECRET);
	}
	
	@Override
	public void perform() throws MassiveException
	{
		String factionName = this.readArg();
		String playerName = this.readArg();
		
		if (NameReserves.get().isNameReserved(factionName))
		{
			msender.msg("&cThe faction name %s is already reserved for %s.", factionName, NameReserves.get().getNameReservedForWho(factionName));
			return;
		}
		
		Player player = Bukkit.getPlayer(playerName);
		
		if (player == null)
		{
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
			
			if (offlinePlayer == null)
			{
				msender.msg("&cCould not resolve offline player %s.", playerName);
				return;
			}
			
			NameReserves.get().reserveName(StringUtils.lowerCase(factionName), offlinePlayer.getUniqueId());
			msender.msg("&aSuccessfully reserved %s for %s.", factionName, offlinePlayer.getName());
		}
		else
		{
			NameReserves.get().reserveName(StringUtils.lowerCase(factionName), player.getUniqueId());
			msender.msg("&aSuccessfully reserved %s for %s.", factionName, player.getName());
		}
		
		if (MConf.get().logFactionReserve)
		{
			Factions.get().log(msender.getName() + " reserved the faction name " + factionName + " for " + playerName);
		}
	}
	
}
