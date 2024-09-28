package com.massivecraft.factions.cmd.chest;

import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.objects.ChestTransaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MissionUpgradeConf;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdFactionsChestLog extends FactionsCommand
{
	
	public CmdFactionsChestLog()
	{
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		int page = this.readArg();
		
		Faction faction = this.readArg(msenderFaction);
		
		if (!(faction.getLevel(MissionUpgradeConf.get().factionChestUpgrade.getUpgradeName()) > 0))
		{
			msender.msg("<b>In order to use your virtual chest storage, you must unlock it using /f upgrade.");
			return;
		}
		
		if (MPerm.getPermChest().has(msender, faction, true))
		{
			// Pager Create
			final List<Mson> exchangeList = new ArrayList<Mson>();
			
			for (ChestTransaction chestTransaction : faction.getChestTransactions())
			{
				String transactionTime = DateFormatUtils.format(chestTransaction.getTimeOfExchange(), "dd MMM HH:mm:ss");
				
				long timeRemaining = System.currentTimeMillis() - chestTransaction.getTimeOfExchange();
				String exchangedAgo = Txt.parse("<a>%s", TimeUtil.formatTime(timeRemaining));
				
				Mson mson = mson(
					Txt.parse(
						"<n>%s %s %s x%s %s",
						transactionTime,
						MPlayer.get(chestTransaction.getPlayerId()).getName(),
						chestTransaction.getItemStack().getAmount() < 0 ? "took" : "put",
						Math.abs(chestTransaction.getItemStack().getAmount()),
						Txt.getItemName(chestTransaction.getItemStack())
					)
				);
				
				mson = mson.tooltip(Txt.parse("<a>%sago", exchangedAgo));
				exchangeList.add(mson);
			}
			
			Collections.reverse(exchangeList);
			
			final Pager<Mson> pager = new Pager<>(this, Txt.parse("<i>Faction Chest Log"), page, exchangeList, new Msonifier<Mson>()
			{
				@Override
				public Mson toMson(Mson mson, int i)
				{
					return exchangeList.get(i);
				}
			});
			
			// Pager Message
			pager.message();
		}
	}
	
}
