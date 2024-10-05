package gg.halcyon;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.CmdFactionsWarp;
import com.massivecraft.factions.cmd.perm.CmdFactionsPermGui;
import com.massivecraft.factions.cmd.pperm.CmdFactionsPpermGui;
import com.massivecraft.factions.coll.BoardColl;
import com.massivecraft.factions.coll.FactionColl;
import com.massivecraft.factions.coll.MPlayerColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import com.mysql.jdbc.StringUtils;
import gg.halcyon.upgrades.upgrades.TNTStorageUpgrade;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.NumberFormat;
import java.util.*;

public class EngineExtras extends Engine
{
	
	private static EngineExtras i = new EngineExtras();
	public HashMap<String, Location> assistFactions = new HashMap<>();
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	public ArrayList<Location> removeBanners = new ArrayList<>();
	
	public static EngineExtras get()
	{
		return i;
	}
	
	// -------------------------------------------- //
	// UPDATE LAST ACTIVITY IN FACTION LAND
	// -------------------------------------------- //

	@EventHandler
	public void onPlayerMembershipChange(EventFactionsMembershipChange event) {
		Player player = event.getMPlayer().getPlayer();
		if (player != null) {
			player.closeInventory();
		}
	}

	@EventHandler
	public void onFactionDisband(EventFactionsDisband event) {
		event.getFaction().getMPlayers().stream().filter(MPlayer::isOnline).forEach(mPlayer -> mPlayer.getPlayer().closeInventory());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		MPlayer mplayer = MPlayer.get(event.getPlayer());
		Faction faction = mplayer.getFaction();
		if (!faction.isNone())
		{
			for (Player p : faction.getOnlinePlayers())
			{
				MPlayer mp = MPlayer.get(p);
				
				if (!mp.recieveLoginNotifications()) continue;
				
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangConf.get().playerQuitMessage.replace("%PLAYER%", mplayer.getNameAndTitle(mplayer))));
			}
		}
		updateLastActivityInFactionLand(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		updateLastActivityInFactionLand(event.getPlayer());
	}
	
	private void updateLastActivityInFactionLand(CommandSender sender)
	{
		if (sender == null)
		{
			throw new RuntimeException("sender");
		}
		if (MUtil.isntSender(sender))
		{
			return;
		}
		MPlayer mplayer = MPlayer.get(sender);
		mplayer.setLastActivityMillisInLand();
	}
	
	// -------------------------------------------- //
	// FACTION HOME PATCH
	// -------------------------------------------- //
	
	@EventHandler
	public void onCommandPreprocessEvent(PlayerCommandPreprocessEvent event)
	{
		IEssentials essentials = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		String command = event.getMessage().toLowerCase();
		if (!command.startsWith("/home") && !command.startsWith("/hoMe") && !command.startsWith("/homE") && !command.startsWith("/hOme") && !command.startsWith("/Home") && !command.startsWith("/HOME") && !command.startsWith("/Home") && !command.startsWith("/hOME") && !command.startsWith("/homes") && !command.startsWith("/ehome") && !command.startsWith("/ehomes") && !command.startsWith("/essentials:home") && !command.startsWith("/essentials:ehome") && !command.startsWith("/essentials:ehomes"))
			return;
		if (command.split(" ").length <= 1)
			return;
		IUser user = essentials.getUser(event.getPlayer());
		Location to = null;
		try
		{
			to = user.getHome(command.split(" ")[1]);
		}
		catch (Exception ignored)
		{
		}
		if (to == null)
			return;
		Player player = event.getPlayer();
		MPlayer mplayer = MPlayer.get(player);
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(to));
		if (faction == FactionColl.get().getNone() || faction == FactionColl.get().getSafezone() || faction == FactionColl.get().getWarzone())
			return;
		if (!MPerm.getPermTphome().has(mplayer, faction, false))
		{
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', (LangConf.get().homePatchMessage.replace("%LOC%", ("(" + to.getBlockX() + "," + to.getBlockY() + "," + to.getBlockZ() + ")")))));
			event.setCancelled(true);
		}
	}
	
	// -------------------------------------------- //
	//  F CLAIM SQUARE
	// -------------------------------------------- //
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (StringUtils.startsWithIgnoreCase(event.getMessage(), "/f claim") || StringUtils.startsWithIgnoreCase(event.getMessage(), "/f unclaim"))
		{
			String[] args = event.getMessage().split(" ");
			
			if (args.length < 3) return;
			
			try
			{
				int radius = Integer.parseInt(args[2]);
				
				if (radius > 1)
				{
					event.setMessage(args[0] + " " + args[1] + " square " + radius);
				}
			}
			catch (NumberFormatException ignored)
			{
			}
		}
	}
	
	// -------------------------------------------- //
	//  AUTO REOPEN FPERM GUI
	// -------------------------------------------- //
	
	@EventHandler
	public void onPermGuiInventoryClose(InventoryCloseEvent event)
	{
		if (event.getInventory().getHolder() != null) return;
		if (!StringUtils.startsWithIgnoreCase(event.getInventory().getTitle(), ChatColor.RED + "Editing ")) return;
		
		boolean isPpermGui = false;
		
		if (event.getPlayer().getOpenInventory().getTopInventory().getType() == InventoryType.HOPPER)
		{
			isPpermGui = true;
		}
		
		boolean finalIsPpermGui = isPpermGui;
		
		String targetName = "";
		
		if (isPpermGui)
		{
			targetName = event.getPlayer().getOpenInventory().getTopInventory().getContents()[0].getItemMeta().getDisplayName().split(" ")[5];
		}
		
		String finalTargetName = targetName;
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (!StringUtils.startsWithIgnoreCase(event.getPlayer().getOpenInventory().getTitle(), ChatColor.RED + "Editing "))
				{
					if (finalIsPpermGui)
					{
						CmdFactionsPpermGui.get().execute(event.getPlayer(), MUtil.list(finalTargetName));
					}
					else
					{
						CmdFactionsPermGui.get().execute(event.getPlayer(), Collections.emptyList());
					}
				}
			}
		}.runTaskLater(Factions.get(), 2L);
	}
	
	// -------------------------------------------- //
	//  DISABLE INVIS POTS
	// -------------------------------------------- //
	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		if (!MConf.get().disableInvisPots) return;
		if (!event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
		if (!MConf.get().disableInvisPotsForAdmins && event.getPlayer().hasPermission(MConf.get().disableInvisPotsBypassPermission))
			return;
		
		event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
		MixinMessage.get().msgOne(event.getPlayer(), LangConf.get().removeInvisEffectMsg);
	}
	
	// -------------------------------------------- //
	//  DISABLE STUFF FOR F ALTS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (MConf.get().materialsAltsCanBuild.contains(event.getBlock().getType().name())) return;
		
		if (MPlayer.get(event.getPlayer()).isAlt())
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (MConf.get().materialsAltsCanBuild.contains(event.getBlock().getType().name())) return;
		
		if (MPlayer.get(event.getPlayer()).isAlt())
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player)) return;
		
		Player damagerPlayer = (Player) event.getDamager();
		
		if (damagerPlayer == null) return;
		
		if (MPlayer.get(damagerPlayer).isAlt())
		{
			event.setCancelled(true);
		}
	}
	
	// -------------------------------------------- //
	//  TNT TRANSFER STICK
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractChest(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		if (!(event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST))
			return;
		
		if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() == Material.AIR)
			return;
		
		if (event.getPlayer().getItemInHand().getType() != Material.STICK) return;
		
		if (!event.getPlayer().getItemInHand().hasItemMeta()) return;
		if (!event.getPlayer().getItemInHand().getItemMeta().hasDisplayName()) return;
		if (!event.getPlayer().getItemInHand().getItemMeta().hasLore()) return;
		
		if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.stripColor(event.getPlayer().getItemInHand().getItemMeta().getDisplayName())))
			return;
		
		if (!(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Txt.parse(GuiConf.get().tntStickName))))
			return;
		
		event.setCancelled(true);
		
		MPlayer mPlayer = MPlayer.get(event.getPlayer());
		
		Faction faction = mPlayer.getFaction();
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		Chest chest = (Chest) event.getClickedBlock().getState();
		
		if (!chest.getInventory().contains(Material.TNT)) return;
		
		int tntInChest = getAmountOfTNT(chest.getInventory());

		try {
			int max = (int) TNTStorageUpgrade.getUpgradeValue(faction.getLevel(MissionUpgradeConf.get().tntUpgrade.getUpgradeName()));
			if(faction.getTnt() >= max) {
				mPlayer.msg(Txt.parse("<rose>Could not add tnt due it bank being full."));
				return;
			}
		} catch (NumberFormatException | NullPointerException ignore) {}

		chest.getInventory().removeItem(new ItemStack(Material.TNT, tntInChest));
		chest.update();
		
		mPlayer.msg("%s<i> transferred <a>%s <i>tnt to %s<i> from a chest.", mPlayer.describeTo(mPlayer, true), numberFormat.format(tntInChest), faction.describeTo(mPlayer));
		faction.addTnt(tntInChest);
	}
	
	private int getAmountOfTNT(Inventory inventory)
	{
		int i = 0;
		for (ItemStack is : inventory.getContents())
		{
			if (is != null && !is.hasItemMeta()) {
				if (is.getType() == Material.TNT) {
					i += is.getAmount();
				}
			}
		}
		return i;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event)
	{
		MPlayer mPlayer = MPlayerColl.get().get(event.getPlayer());
		
		if (mPlayer.getCurrentWarp() == null || mPlayer.getCurrentWarp().isEmpty()) return;
		if (mPlayer.getCurrentWarp().equalsIgnoreCase("_NO_CURRENT_WARP_")) return;
		
		event.setCancelled(true);
		
		String warpName = mPlayer.getCurrentWarp();
		String warpPassword = mPlayer.getFaction().getWarpPassword(warpName);
		PS warpLocation = mPlayer.getFaction().getWarp(mPlayer.getCurrentWarp());
		
		mPlayer.setCurrentWarp("_NO_CURRENT_WARP_");
		
		if (warpLocation == null)
		{
			mPlayer.msg("&cThat warp is no longer a valid warp.");
			return;
		}
		
		if (warpPassword != null && !warpPassword.equalsIgnoreCase(ChatColor.stripColor(event.getMessage())))
		{
			mPlayer.msg("&cThat password is incorrect!");
			return;
		}

		CmdFactionsWarp.get().beginWarp(mPlayer.getPlayer(), warpName, warpLocation.asBukkitLocation(true));
	}

	public static Set<PS> corners;

	public static void loadCorners()
	{
		EngineExtras.corners = new HashSet<>();
		for (World world : Factions.get().getServer().getWorlds())
		{
			WorldBorder border = world.getWorldBorder();
			if (border != null)
			{
				int cornerCoord = (int) ((border.getSize() - 1.0) / 2.0);
				EngineExtras.corners.add(PS.valueOf(world.getName(), blockToChunk(cornerCoord), blockToChunk(cornerCoord)));
				EngineExtras.corners.add(PS.valueOf(world.getName(), blockToChunk(cornerCoord), blockToChunk(-cornerCoord)));
				EngineExtras.corners.add(PS.valueOf(world.getName(), blockToChunk(-cornerCoord), blockToChunk(cornerCoord)));
				EngineExtras.corners.add(PS.valueOf(world.getName(), blockToChunk(-cornerCoord), blockToChunk(-cornerCoord)));
			}
		}
	}

	// bit-shifting is used because it's much faster than standard division and multiplication
	public static int blockToChunk(int blockVal)
	{    // 1 chunk is 16x16 blocks
		return blockVal >> 4;   // ">> 4" == "/ 16"
	}

	private boolean isTntDeniedWorld(World world) {
		return MConf.get().tntAllowedWorldsDuringGrace.stream().noneMatch(world.getName()::equalsIgnoreCase);
	}

	@EventHandler
	public void explode(BlockExplodeEvent event) {
		if (!MConf.get().gracePeriod) return;
		if (isTntDeniedWorld(event.getBlock().getWorld())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void explosivePrime(ExplosionPrimeEvent event) {
		if (!MConf.get().gracePeriod) return;
		if (isTntDeniedWorld(event.getEntity().getWorld())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void entityExplode(EntityCombustEvent event) {
		if (!MConf.get().gracePeriod) return;
		if (isTntDeniedWorld(event.getEntity().getWorld())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void placeDetection(BlockPlaceEvent event) {
		if (!MConf.get().gracePeriod || event.getPlayer().hasPermission("grace.bypass")) return;
		if (isTntDeniedWorld(event.getBlock().getWorld())) {
			if (event.getBlockPlaced().getType().equals(Material.TNT)) {
				MixinMessage.get().msgOne(event.getPlayer(), "&cYou can't place this during graceperiod.");
				event.setCancelled(true);
			}
		}
	}


	//@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	//public void onPlayerInteract(PlayerInteractEvent event) {
	//	if (event.getClickedBlock() != null
	//			&& event.getClickedBlock().getType() == Material.JUKEBOX
	//			&& event.getAction() == Action.RIGHT_CLICK_BLOCK
	//	) {
	//		event.setCancelled(true);
	//	}
	//}

	@EventHandler
	public void interactDetection(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if (!MConf.get().gracePeriod) {
			return;
		}
		if (event.getItem() == null) {
			return;
		}
		if (event.getPlayer().hasPermission("grace.bypass")) {
			return;
		}

		if (event.getItem().getType() == Material.EXPLOSIVE_MINECART) {
			if (isTntDeniedWorld(event.getPlayer().getWorld())) {
				MixinMessage.get().msgOne(event.getPlayer(), "&cYou can't interact with this during grace period.");
				event.setCancelled(true);
			}
		}
	}
	
}
