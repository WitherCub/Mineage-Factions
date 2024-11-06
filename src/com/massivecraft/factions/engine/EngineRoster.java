package com.massivecraft.factions.engine;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.engine.actions.ActionRosterKick;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.RosterScrollerInventory;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.Txt;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EngineRoster extends Engine {

    private static EngineRoster i = new EngineRoster();

    public static EngineRoster get() {
        return i;
    }

    @EventHandler
    public void onFactionCreate(EventFactionsCreate event) {
        if (!MConf.get().rosterEnabled) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Faction factionData = FactionColl.get().get(event.getFactionId());
                if (factionData != null) {
                    factionData.addToRoster(event.getMPlayer().getUuid(), Rel.COLEADER);
                }
            }
        }.runTaskLater(Factions.get(), 2L);
    }

    @EventHandler
    public void onRoleChange(EventFactionsMembershipChange event) {
        if (!MConf.get().rosterEnabled) {
            return;
        }

        if (event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.ALT || event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.DISBAND || event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.CREATE || event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.KICK || event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.LEAVE || event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.RANK || event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.LEADER) {
            return;
        }

        MPlayer mPlayer = event.getMPlayer();
        Faction faction = event.getNewFaction();

        if (event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.KICK) {
            faction = mPlayer.getFaction();
        }

        if (faction.isNone()) return;

        if (mPlayer.getPlayer().hasPermission(MConf.get().rosterBypassJoinPermission)) return;

        if (faction.isInvitedAlt(mPlayer)) return;

        if (faction.isAltsOpen() && event.isAltJoin()) {
            return;
        }

        if (!faction.getRoster().containsKey(mPlayer.getUuid())) {
            event.setCancelled(true);
            mPlayer.msg(LangConf.get().notOnFactionRosterMsg.replace("%faction%", faction.describeTo(mPlayer, true)));
        }

        final Faction fac = faction;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (fac.getRoster().containsKey(mPlayer.getUuid())) {
                    mPlayer.setRole(fac.getRoster().get(mPlayer.getUuid()));
                }
            }
        }.runTaskLater(Factions.get(), 2L);
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!MConf.get().rosterEnabled) {
            return;
        }

        String message = event.getMessage();

        if (!StringUtils.startsWithIgnoreCase(message, "/f join")) {
            return;
        }

        String[] args = message.split(" ");

        if (args.length < 3) {
            return;
        }

        String factionToJoin = args[2];

        Faction faction = FactionColl.get().getByName(factionToJoin);

        if (faction == null || faction.isSystemFaction()) {
            MPlayer mPlayer = MPlayerColl.get().getByName(factionToJoin);
            if (mPlayer == null) return;
            faction = mPlayer.getFaction();
            if (faction == null || faction.isSystemFaction()) {
                return;
            }
        }

        Player player = event.getPlayer();

        if (player.hasPermission(MConf.get().rosterBypassJoinPermission)) return;

        MPlayer mPlayer = MPlayer.get(player);

        if (faction.isInvitedAlt(mPlayer)) return;

        if (!faction.getRoster().containsKey(mPlayer.getUuid())) {
            return;
        }

        boolean isInvited = faction.isInvited(mPlayer);

        if (!isInvited) {
            Invitation invitation = new Invitation(mPlayer.getId(), System.currentTimeMillis(), false);
            faction.invite(mPlayer.getId(), invitation);
            faction.changed();
        }

        if (!isFactionFull(faction)) {
            return;
        }

        List<MPlayer> offlinePlayers = faction.getMPlayersWhereOnline(false);

        if (offlinePlayers.size() == 0) {
            return;
        }

        MPlayer mplayerToKick = null;

        for (MPlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.isAlt()) {
                continue;
            }
            if (offlinePlayer.getRole().getValue() == Rel.LEADER.getValue()) {
                continue;
            }
            if (mplayerToKick == null) {
                mplayerToKick = offlinePlayer;
            }
            if (offlinePlayer.getRole().isLessThan(mplayerToKick.getRole())) {
                mplayerToKick = offlinePlayer;
            }
        }

        if (mplayerToKick != null && mplayerToKick.getFaction().getId().equals(faction.getId())) {
            Factions.get().log(mplayerToKick.getName() + " has been rotated out of " + mplayerToKick.getFaction().getName());
            mplayerToKick.getFaction().uninvite(mplayerToKick);
            mplayerToKick.resetFactionData();
        }
    }

    public boolean isFactionFull(Faction faction) {
        return com.massivecraft.factions.entity.MConf.get().factionMemberLimit <= faction.getMPlayers().size();
    }

    public Inventory getRosterGui(Faction faction, Player player) {
        if (faction.isSystemFaction()) {
            return null;
        }

        RosterScrollerInventory scrollerInventory = new RosterScrollerInventory();
        ChestGui chestGui = scrollerInventory.getBlankPage(Txt.parse(GuiConf.get().factionRosterGuiTitle), 54, player);
        scrollerInventory.fillSidesWithItem(chestGui.getInventory(), new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(GuiConf.get().factionRosterGuiStainedGlassBorderColorId));

        List<String> lore = new ArrayList<>();

        String rosterKicksRemaining;

        if (MConf.get().unlimitedRosterKicks) {
            rosterKicksRemaining = GuiConf.get().factionRosterGuiLoreUnlimitedKicksPlaceholderRet;
        } else if (MConf.get().isRosterChangeable()) {
            rosterKicksRemaining = String.valueOf(faction.getRosterKicksRemaining());
        } else {
            rosterKicksRemaining = "0";
        }

        for (UUID rosterPlayerId : faction.getRoster().keySet()) {
            Integer slot = scrollerInventory.getEmptyNonSideSlots(chestGui.getInventory()).stream().findFirst().orElse(null);

            if (slot == null) {
                chestGui = scrollerInventory.getBlankPage(Txt.parse(GuiConf.get().factionRosterGuiTitle), 54, player);
                scrollerInventory.fillSidesWithItem(chestGui.getInventory(), new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(GuiConf.get().factionRosterGuiStainedGlassBorderColorId));
                slot = scrollerInventory.getEmptyNonSideSlots(chestGui.getInventory()).stream().findFirst().orElse(null);
                if (slot == null) {
                    break;
                }
            }

            MPlayer mPlayer = MPlayer.get(rosterPlayerId);
            if (mPlayer == null) continue;
            if (mPlayer.isAlt()) continue;

            lore.clear();

            GuiConf.get().factionRosterGuiLore.stream().map(line -> Txt.parse(line.replace("%kicks%", rosterKicksRemaining).replace("%rel%", Txt.getNicedEnum(faction.getRoster().get(rosterPlayerId))))).forEach(lore::add);
            String entryName = faction.getId().equals(mPlayer.getFactionId()) ? GuiConf.get().factionRosterGuiMemberNameEntry.replace("%playerDesc%", mPlayer.describeTo(faction, true)) : GuiConf.get().factionRosterGuiNotMemberNameEntry.replace("%playerDesc%", mPlayer.describeTo(faction, true));

            ItemStack skull = new ItemBuilder(EngineSkull.get().getSkullItem(rosterPlayerId, mPlayer.getName())).
                    name(Txt.parse(entryName))
                    .setLore(lore);

            chestGui.getInventory().setItem(slot, skull);
            chestGui.setAction(slot, new ActionRosterKick(mPlayer));
        }

        return chestGui.getInventory();
    }

}