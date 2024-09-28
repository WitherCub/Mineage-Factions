package com.massivecraft.factions.cmd.credits;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;

public class CmdFactionsCreditsGiveitem extends FactionsCommand implements Listener
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsCreditsGiveitem()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
		
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeInteger.get(), "credits");
		this.addParameter(TypeInteger.get(), "amount");
		this.addRequirements(RequirementHasPerm.get(Perm.CREDITS_GIVEITEM));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		MPlayer mPlayer = this.readArgAt(0);
		Integer credits = this.readArgAt(1);
		Integer amount = this.readArgAt(2);
		
		if (!(mPlayer.isPlayer()))
		{
			msender.msg("<b>You can only give credit items to players!");
			return;
		}
		
		msender.msg("%s <i>gave %s <a>x%s <i>credit items worth <a>%s <i>credits each.", msender.describeTo(msender, true), mPlayer.describeTo(msender, true), numberFormat.format(amount), numberFormat.format(credits));
		mPlayer.msg("%s <i>gave %s <a>x%s <i>credit items worth <a>%s <i>credits each.", msender.describeTo(mPlayer, true), mPlayer.describeTo(mPlayer, true), numberFormat.format(amount), numberFormat.format(credits));
		
		if (mPlayer.getPlayer().getInventory().firstEmpty() == -1)
		{
			mPlayer.getPlayer().getWorld().dropItemNaturally(mPlayer.getPlayer().getLocation(), createItem(credits, amount));
		}
		else
		{
			mPlayer.getPlayer().getInventory().addItem(createItem(credits, amount));
		}
	}
	
	private ItemStack createItem(Integer credits, Integer amount)
	{
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		return new ItemBuilder(Material.EMERALD).name(Txt.parse("<k>Faction Credits")).lore(Txt.parse("<i>Hold and right-click to receive:")).lore(Txt.parse("<n>- <v>%s Faction Credits", numberFormat.format(credits))).amount(amount);
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		ItemStack itemStack = event.getPlayer().getItemInHand();
		if (itemStack.getType() == Material.EMERALD)
		{
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				Player player = event.getPlayer();
				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(Txt.parse("<k>Faction Credits")))
				{
					event.setCancelled(true);
					
					NumberFormat numberFormat = NumberFormat.getInstance();
					numberFormat.setGroupingUsed(true);
					
					int credits = Integer.parseInt(ChatColor.stripColor(itemStack.getItemMeta().getLore().get(1).replaceAll(",", "").split(" ")[1]));
					MPlayer mPlayer = MPlayer.get(player);
					
					if (mPlayer.getFaction().isSystemFaction())
					{
						mPlayer.msg("&cYou can't redeem credits to a system faction!");
						return;
					}
					
					if (credits > 0)
					{
						mPlayer.getFaction().addCredits(credits);
						mPlayer.getFaction().msg("%s <i>added <a>%s <i>credits to %s <i>increasing the faction total to <a>%s <i>credits.", mPlayer.describeTo(mPlayer.getFaction(), true), numberFormat.format(credits), mPlayer.getFaction().describeTo(mPlayer.getFaction()), numberFormat.format(mPlayer.getFaction().getCredits()));
					}
					
					player.getInventory().removeItem(createItem(credits, 1));
				}
			}
		}
	}
}
