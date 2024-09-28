package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.claim.CmdFactionsClaim;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.engine.actions.ActionRaidclaimUnclaim;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.util.BorderUtil;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.chestgui.ChestGui;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import gg.halcyon.EngineShield;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CmdFactionsRaidclaim extends FactionsCommand {
    private static CmdFactionsRaidclaim i = new CmdFactionsRaidclaim();
    public static CmdFactionsRaidclaim get() { return i; }

    public CmdFactionsRaidclaim() {
        this.addParameter(TypeInteger.get(), "radius", "");
        this.addRequirements(RequirementIsPlayer.get());
        this.addRequirements(ReqHasFaction.get());
    }

    @Override
    public void perform() throws MassiveException {
        Integer radius = this.readArg(null);

        if (radius == null) {
            me.openInventory(getRaidclaimGui(msenderFaction));
        } else {
            if (radius > MConf.get().maxRaidclaimRadius) {
                MixinMessage.get().msgOne(me, MConf.get().maxRaidclaimRadiusMsg.replace("%maxRadius%", String.valueOf(MConf.get().maxRaidclaimRadius)));
                return;
            }

            if (IntStream.range(1, MConf.get().raidClaimsAvailable + 1).noneMatch(i -> msenderFaction.isRaidClaimAvailable(i))) {
                MixinMessage.get().msgOne(me, MConf.get().noRaidClaimsAvailableMsg);
                return;
            }

            // attempt actually claiming chunks
            CmdFactionsClaim.get().cmdFactionsClaimSquare.execute(sender, args);

            Location location = me.getLocation();

            Set<PS> chunksToSave = new HashSet<>();

            // save center chunk as the first element
            PS centerPs = PS.valueOf(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ());
            chunksToSave.add(centerPs);

            Faction faction = BoardColl.get().getFactionAt(centerPs);

            for (int x = (location.getChunk().getX() - radius); x <= (location.getChunk().getX() + radius); x++) {
                for (int z = (location.getChunk().getZ() - radius); z <= (location.getChunk().getZ() + radius); z++) {
                    PS chunk = PS.valueOf(location.getWorld().getName(), x, z);

                    if (BorderUtil.get().isOutsideOfBorder(new Location(chunk.asBukkitWorld(), (chunk.getChunkX() * 16), 1, (chunk.getChunkZ() * 16)))) {
                        continue;
                    }

                    if (EngineShield.get().isPsInsideBaseRegion(faction, chunk.getWorld(), chunk.getChunkX(), chunk.getChunkZ())) {
                        continue;
                    }

                    Faction factionAt = BoardColl.get().getFactionAt(chunk);

                    if (faction.isNone() || !faction.equals(factionAt)) {
                        continue;
                    }

                    chunksToSave.add(chunk);
                }
            }

            int bound = MConf.get().raidClaimsAvailable + 1;
            for (int i = 1; i < bound; i++) {
                if (msenderFaction.isRaidClaimAvailable(i)) {
                    msenderFaction.getRaidClaims().put(i, chunksToSave);
                    msenderFaction.changed();
                    break;
                }
            }

            MixinMessage.get().msgOne(me, MConf.get().raidClaimSetMsg);
        }
    }

    public Inventory getRaidclaimGui(Faction faction) {
        if (faction.isSystemFaction()) {
            return null;
        }

        Inventory inventory;

        if (MConf.get().raidClaimGuiHopper) {
            inventory = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', MConf.get().raidClaimGuiInventoryTitle));
        } else {
            inventory = Bukkit.createInventory(null, MConf.get().raidClaimGuiSize, ChatColor.translateAlternateColorCodes('&', MConf.get().raidClaimGuiInventoryTitle));
        }

        ChestGui chestGui = ChestGui.getCreative(inventory);
        chestGui.setAutoclosing(false);
        chestGui.setAutoremoving(true);
        chestGui.setSoundOpen(null);
        chestGui.setSoundClose(null);

        chestGui.getInventory().setItem(MConf.get().raidClaimGuiHelpButtonSlot,
                new ItemBuilder(MConf.get().raidClaimGuiHelpButtonMaterial)
                        .name(ChatColor.translateAlternateColorCodes('&', MConf.get().raidClaimGuiHelpButtonName))
                        .setLore(MConf.get().raidClaimGuiHelpButtonLore.stream().map(Txt::parse).collect(Collectors.toList()))
        );

        IntStream.range(1, MConf.get().raidClaimsAvailable + 1).forEach(i -> {
            List<String> raidclaimLore;

            if (faction.isRaidClaimAvailable(i)) {
                raidclaimLore = MConf.get().raidClaimGuiRaidclaimButtonLoreUnclaimed.stream().map(Txt::parse).collect(Collectors.toList());
            } else {
                PS centerPs = faction.getRaidClaims().get(i).stream().findFirst().orElse(null);

                if (centerPs == null) {
                    raidclaimLore = MConf.get().raidClaimGuiRaidclaimButtonLoreUnclaimed.stream().map(Txt::parse).collect(Collectors.toList());
                } else {
                    raidclaimLore = MConf.get().raidClaimGuiRaidclaimButtonLoreClaimed.stream().map(s -> Txt.parse(s.replace("%worldName%", centerPs.getWorld(true)).replace("%z%", Factions.get().getPriceFormat().format(centerPs.getBlockZ(true))).replace("%x%", Factions.get().getPriceFormat().format(centerPs.getBlockX(true))))).collect(Collectors.toList());
                    chestGui.setAction(MConf.get().raidClaimNumberAndGuiSlot.get(i), new ActionRaidclaimUnclaim(faction, i));
                }
            }

            chestGui.getInventory().setItem(MConf.get().raidClaimNumberAndGuiSlot.get(i),
                    new ItemBuilder(MConf.get().raidClaimGuiRaidclaimButtonMaterial)
                            .name(ChatColor.translateAlternateColorCodes('&', MConf.get().raidClaimGuiRaidclaimButtonName.replace("%number%", String.valueOf(i))))
                            .setLore(raidclaimLore)
            );
        });

        return chestGui.getInventory();
    }

}