package com.massivecraft.factions.engine.actions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.GuiConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.Glow;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ActionStickBuy extends ChestActionAbstract
{
	
	@Override
	public boolean onClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		MPlayer mPlayer = MPlayer.get(player);
		Faction faction = mPlayer.getFaction();
		
		if (faction.getCredits() >= 50)
		{
			ItemStack itemStack = new ItemBuilder(Material.STICK).name(Txt.parse(GuiConf.get().tntStickName)).lore(Txt.parse("&7Right click a chest to transfer TNT")).enchantment(Glow.getGlow());
			
			if (player != null)
			{
				if (player.getInventory().firstEmpty() == -1)
				{
					player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
				}
				else
				{
					player.getInventory().addItem(itemStack);
				}
				
				MixinMessage.get().msgOne(player, "<g>You have purchased a tnt stick.");
			}
			
			faction.takeCredits(50);
			
			return true;
		}
		else
		{
			mPlayer.msg("<g>Your faction <b>does not have enough credits.");
			player.closeInventory();
		}
		
		return false;
	}
}