package com.massivecraft.factions.util;

import com.massivecraft.factions.engine.EngineSkull;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.chestgui.ChestActionAbstract;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RosterScrollerInventory {

    public static HashMap<UUID, RosterScrollerInventory> users = new HashMap<>();
    private final ItemStack nextPageButton = new ItemBuilder(EngineSkull.get().getSkullItem(MConf.get().factionRosterGuiNextPageButtonTexture)).name(Txt.parse(MConf.get().factionRosterGuiNextPageName));
    private final ItemStack previousPageButton = new ItemBuilder(EngineSkull.get().getSkullItem(MConf.get().factionRosterGuiPreviousPageButtonTexture)).name(Txt.parse(MConf.get().factionRosterGuiPreviousPageName));
    public ArrayList<ChestGui> pages = new ArrayList<>();
    public UUID id;
    public int currentPage = 0;
    private final List<Integer> sideSlots = new ArrayList<>();

    public ChestGui getBlankPage(String name, int size, Player player) {
        this.id = UUID.randomUUID();

        Inventory page = Bukkit.createInventory(null, size, name);

        ChestGui chestGui = ChestGui.getCreative(page);
        chestGui.setAutoclosing(false);
        chestGui.setAutoremoving(false);
        chestGui.setSoundOpen(null);
        chestGui.setSoundClose(null);

        page.setItem(MConf.get().factionRosterGuiNextPageSlot, nextPageButton);
        chestGui.setAction(MConf.get().factionRosterGuiNextPageSlot, new ActionSwitchPage());
        page.setItem(MConf.get().factionRosterGuiPreviousPageSlot, previousPageButton);
        chestGui.setAction(MConf.get().factionRosterGuiPreviousPageSlot, new ActionSwitchPage());

        pages.add(chestGui);
        users.put(player.getUniqueId(), this);

        return chestGui;
    }

    public void fillSidesWithItem(Inventory inv, ItemStack item) {
        int size = inv.getSize();
        int rows = size / 9;

        if (rows >= 3) {
            for (int i = 0; i <= 8; i++) {
                inv.setItem(i, item);
                sideSlots.add(i);
            }

            for (int s = 8; s < (inv.getSize() - 9); s += 9) {
                int lastSlot = s + 1;
                inv.setItem(s, item);
                inv.setItem(lastSlot, item);

                sideSlots.add(s);
                sideSlots.add(lastSlot);
            }

            for (int lr = (inv.getSize() - 9); lr < inv.getSize(); lr++) {
                inv.setItem(lr, item);

                sideSlots.add(lr);
            }
        }

        inv.setItem(MConf.get().factionRosterGuiNextPageSlot, nextPageButton);
        inv.setItem(MConf.get().factionRosterGuiPreviousPageSlot, previousPageButton);
    }

    public List<Integer> getNonSideSlots(Inventory inv) {
        List<Integer> availableSlots = new ArrayList<>();

        for (int i = 0; i < inv.getSize(); i++) {
            if (!this.sideSlots.contains(i)) {
                availableSlots.add(i);
            }
        }

        return availableSlots;
    }

    public List<Integer> getEmptyNonSideSlots(Inventory inv) {
        List<Integer> availableSlots = new ArrayList<>();

        for (int i = 0; i < inv.getSize(); i++) {
            if (!this.sideSlots.contains(i) && inv.getContents()[i] == null) {
                availableSlots.add(i);
            }
        }

        return availableSlots;
    }

    class ActionSwitchPage extends ChestActionAbstract {

        @Override
        public boolean onClick(InventoryClickEvent event, Player player) {
            if (!RosterScrollerInventory.users.containsKey(player.getUniqueId())) return false;

            RosterScrollerInventory inv = RosterScrollerInventory.users.get(player.getUniqueId());

            if (event.getCurrentItem() == null) return false;
            if (event.getCurrentItem().getItemMeta() == null) return false;
            if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return false;
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Txt.parse(MConf.get().factionRosterGuiNextPageName))) {
                if (inv.currentPage < inv.pages.size() - 1) {
                    inv.currentPage += 1;
                    player.openInventory(inv.pages.get(inv.currentPage).getInventory());
                }
                return true;
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Txt.parse(MConf.get().factionRosterGuiPreviousPageName))) {
                if (inv.currentPage > 0) {
                    inv.currentPage -= 1;
                    player.openInventory(inv.pages.get(inv.currentPage).getInventory());
                }
                return true;
            }

            return false;
        }

    }

}