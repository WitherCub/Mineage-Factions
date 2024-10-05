package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.LangConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.money.Money;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.NumberFormat;

public class ActionDrain extends ChestActionAbstract
{
	
	private MPlayer initiatedDrainPlayer;
	private Faction faction;
	private Integer amountToDrain;
	private NumberFormat numberFormat;
	
	public ActionDrain(MPlayer initiatedDrainPlayer, Faction faction, Integer amountToDrain, NumberFormat numberFormat)
	{
		this.initiatedDrainPlayer = initiatedDrainPlayer;
		this.faction = faction;
		this.amountToDrain = amountToDrain;
		this.numberFormat = numberFormat;
	}
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		int amountDrained = 0;
		int membersDrained = 0;
		
		for (MPlayer mplayer : faction.getMPlayers())
		{
			if (!mplayer.isDrain()) continue;
			
			if (Money.get(mplayer) >= amountToDrain)
			{
				Money.despawn(mplayer, null, amountToDrain);
				amountDrained += amountToDrain;
				membersDrained++;
				mplayer.setLastDrainAmountTaken(amountToDrain);
			}
		}
		
		faction.setLastDrainMillis(System.currentTimeMillis());
		Money.spawn(faction, null, amountDrained);
		initiatedDrainPlayer.msg(LangConf.get().drainedMsg.replace("%membersDrained%", numberFormat.format(membersDrained)).replace("%totalDrained%", numberFormat.format(amountDrained)));
		return true;
	}
	
}