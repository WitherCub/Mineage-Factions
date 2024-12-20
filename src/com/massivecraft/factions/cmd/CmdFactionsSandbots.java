package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.engine.actions.ActionDespawnSandbot;
import com.massivecraft.factions.engine.actions.ActionSpawnSandbot;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.GuiConf;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.Sandbot;
import com.massivecraft.factions.util.Glow;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.SkullCreator;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CmdFactionsSandbots extends FactionsCommand {

    private static CmdFactionsSandbots i = new CmdFactionsSandbots();
    public static CmdFactionsSandbots get() { return i; }

    public Set<Integer> sandbotCenterGuiSlots = MUtil.set(11, 15, 20, 24, 29, 33, 38, 42); // make size of this # of sandbot upgrades available

    public CmdFactionsSandbots() {
        this.addParameter(TypeFaction.get(), "faction", "you");
        this.addRequirements(RequirementIsPlayer.get());
        this.addRequirements(ReqHasFaction.get());
    }

    @Override
    public void perform() throws MassiveException {
        Faction faction = this.readArg(msenderFaction);
        me.openInventory(getSandbotGui(faction).getInventory());
    }

    private ChestGui getSandbotGui(Faction faction) {
        Inventory inventory = Bukkit.createInventory(null, 54, Txt.parse(GuiConf.get().sandbotGuiName));

        ChestGui chestGui = ChestGui.getCreative(inventory);
        chestGui.setAutoclosing(false);
        chestGui.setAutoremoving(false);
        chestGui.setSoundOpen(null);
        chestGui.setSoundClose(null);

        if (GuiConf.get().sandbotGuiBorderGlassEnabled) {
            if (GuiConf.get().sandbotGuiBorderGlassGlowEnabled) {
                IntStream.range(0, chestGui.getInventory().getSize()).forEach(i -> chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(GuiConf.get().sandbotGuiBorderGlassDurabilityId).enchantment(Glow.getGlow())));
            } else {
                IntStream.range(0, chestGui.getInventory().getSize()).forEach(i -> chestGui.getInventory().setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).name(" ").durability(GuiConf.get().sandbotGuiBorderGlassDurabilityId)));
            }
        }

        chestGui.getInventory().setItem(GuiConf.get().sandbotGuiInfoItemSlot,
                new ItemBuilder(GuiConf.get().sandbotGuiInfoItemMaterial)
                        .durability(GuiConf.get().sandbotGuiInfoItemMaterialDurability)
                        .name(Txt.parse(GuiConf.get().sandbotGuiInfoItemName))
                        .setLore(GuiConf.get().sandbotGuiInfoItemLore.stream().map(Txt::parse).collect(Collectors.toList()))
        );

        int sandbotNumber = 1;

        for (int slot : sandbotCenterGuiSlots) {
            Sandbot sandbot = faction.getSandbots().get(slot);

            chestGui.getInventory().setItem(slot,
                    new ItemBuilder(SkullCreator.itemFromBase64(GuiConf.get().sandbotInfoButtonHeadData))
                            .name(Txt.parse(GuiConf.get().sandbotInfoButtonName.replace("%sandbotNumber%", String.valueOf(sandbotNumber))))
                            .setLore(sandbot != null && !sandbot.isDespawned() ? GuiConf.get().sandbotInfoButtonLoreActive.stream().map(Txt::parse).collect(Collectors.toList()) : GuiConf.get().sandbotInfoButtonLoreInactive.stream().map(Txt::parse).collect(Collectors.toList()))
            );

            chestGui.getInventory().setItem(slot - 1,
                    new ItemBuilder(SkullCreator.itemFromBase64(GuiConf.get().sandbotSpawnButtonHeadData))
                            .name(Txt.parse(GuiConf.get().sandbotSpawnButtonName.replace("%sandbotNumber%", String.valueOf(sandbotNumber))))
            );

            chestGui.setAction(slot - 1, new ActionSpawnSandbot(faction, slot, sandbotNumber));

            chestGui.getInventory().setItem(slot + 1,
                    new ItemBuilder(SkullCreator.itemFromBase64(GuiConf.get().sandbotDespawnButtonHeadData))
                            .name(Txt.parse(GuiConf.get().sandbotDespawnButtonName.replace("%sandbotNumber%", String.valueOf(sandbotNumber))))
            );

            chestGui.setAction(slot + 1, new ActionDespawnSandbot(faction, slot, sandbotNumber));

            sandbotNumber++;
        }

        return chestGui;
    }

}