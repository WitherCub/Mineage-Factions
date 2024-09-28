package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.*;
import com.massivecraft.factions.entity.objects.FactionValue;
import com.massivecraft.factions.task.TaskFactionTopCalculate;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.ValueFormatter;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveMapDef;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdFactionsPtop extends FactionsCommand {

    public CmdFactionsPtop() {
        this.addParameter(Parameter.getPage());
    }

    @Override
    public void perform() throws MassiveException {
        int page = this.readArg();

        MassiveMapDef<String, FactionValue> data = FactionTopData.get().getFactionValuesPtop();

        if (data == null) {
            FactionTopData.get().sortPtop(true);
            data = FactionTopData.get().getFactionValuesPtop();
        }

        final List<Mson> topData = new ArrayList<>();

        int ftopPosition = 0;

        if (!data.isEmpty()) {
            for (String factionId : data.keySet()) {
                if (data.get(factionId) == null) continue;

                Faction faction = FactionColl.get().get(factionId);

                if (faction == null) continue;
                if (data.get(factionId).getTotalPotentialValue() <= 0) continue;

                HashMap<EntityType, Long> spawnerValues = new HashMap<>();

                FactionValue factionValue = data.get(factionId);

                if (data.get(factionId).getSpawnerValues() != null && !data.get(factionId).getSpawnerValues().isEmpty() && data.get(factionId).getSpawnerValues().size() != 0) {
                    for (FactionValueSpawnerTypeValue factionValueSpawnerTypeValue : data.get(factionId).getSpawnerValues()) {
                        if (!FactionTopValue.get().getSpawnerValues().containsKey(factionValueSpawnerTypeValue.getEntityType()))
                            continue;
                        if (factionValueSpawnerTypeValue.getAmountOfSpawners() <= 0) continue; // to be sure
                        spawnerValues.put(factionValueSpawnerTypeValue.getEntityType(), factionValueSpawnerTypeValue.getTotalSpawnerValues());
                    }
                }

                Long oldTotal = FactionTopData.get().getNearestTotal(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24L), faction);
                String dailyPercentageChange = "";
                String colorChange = "";
                double dailyValueChange = 0.0;
                if (oldTotal != null) {
                    dailyValueChange = factionValue.getTotalPotentialValue() - oldTotal;
                    colorChange = ChatColor.translateAlternateColorCodes('&', (dailyValueChange > 0.0) ? "&a" : ((dailyValueChange < 0.0) ? "&c" : "&e"));
                    double percent = Math.round(dailyValueChange / ((oldTotal == 0.0) ? 1.0 : oldTotal) * 100.0 * 10.0) / 10.0;
                    double clampedPercent = Math.max(-9999.0, Math.min(percent, 9999.0));
                    dailyPercentageChange = ChatColor.translateAlternateColorCodes('&', "&7(" + colorChange + ((clampedPercent < percent) ? ">" : ((clampedPercent < percent) ? "<" : "")) + String.valueOf(clampedPercent).replace(".0", "") + "%&7)");
                }

                ftopPosition++;

                List<String> lore = new ArrayList<>();

                for (String line : MConf.get().factionsTopLoreFormat) {
                    if (line.contains("%placedSpawnerValues%")) {
                        if (!spawnerValues.isEmpty()) {
                            spawnerValues.keySet().stream().map(entityType ->
                                    ChatColor.translateAlternateColorCodes('&',
                                            MConf.get().factionsTopGuiLoreSpawnerValueEntry
                                                    .replace("%spawnerType%", MConf.get().entityTypeDisplayNamesMap.getOrDefault(entityType, entityType.getName()))
                                                    .replace("%value%", "$" + ValueFormatter.format(spawnerValues.get(entityType)))
                                                    .replace("%amount%", String.valueOf(factionValue.getSpawnerValueEntityType(entityType).getAmountOfSpawners())))
                            ).forEach(lore::add);

                            FactionTopValue.get().getSpawnerValues().keySet().stream().filter(entityType -> !spawnerValues.containsKey(entityType)).map(entityType ->
                                    ChatColor.translateAlternateColorCodes('&',
                                            MConf.get().factionsTopGuiLoreSpawnerValueEntry
                                                    .replace("%spawnerType%", MConf.get().entityTypeDisplayNamesMap.getOrDefault(entityType, entityType.getName()))
                                                    .replace("%value%", "$0")
                                                    .replace("%amount%", "0"))
                            ).forEach(lore::add);
                        } else {
                            lore.add(
                                    ChatColor.translateAlternateColorCodes('&',
                                            MConf.get().factionsTopGuiLoreSpawnerValuesEmpty
                                    )
                            );
                        }
                    } else if (line.contains("%twentyFourHourChangeValue%") || line.contains("%twentyFourHourChangePercent%")) {
                        if (dailyValueChange != 0.0) {
                            lore.add(
                                    ChatColor.translateAlternateColorCodes('&',
                                            line.replace("%twentyFourHourChangeValue%", MessageFormat.format("{0}{1}{2}", colorChange, (dailyValueChange > 0.0) ? "▲ $" : ((dailyValueChange < 0.0) ? "▼ $" : ""), Factions.get().getPriceFormat().format(dailyValueChange)))
                                                    .replace("%twentyFourHourChangePercent%", dailyPercentageChange)
                                    )
                            );
                            lore.add(" ");
                        }
                    } else {
                        lore.add(
                                ChatColor.translateAlternateColorCodes('&',
                                        line
                                                .replace("%faction%", faction.getName())
                                                .replace("%position%", Factions.get().getPriceFormat().format(ftopPosition))
                                                .replace("%currentWorth%", "$" + ValueFormatter.format(factionValue.getTotalSpawnerValue()))
                                                .replace("%potentialWorth%", "$" + ValueFormatter.format(factionValue.getTotalPotentialValue()))
                                                .replace("%updatedAgoTime%", TaskFactionTopCalculate.get().getLastRunTime())
                                )
                        );
                    }
                }

                Mson mson = mson(RelationUtil.getColorOfThatToMe(faction, msender) + faction.getName()).add(Txt.parse(MConf.get().cmdFtopTotalPrefix.replace("%total%", Factions.get().getPriceFormat().format(data.get(factionId).getTotalPotentialValue())))).add((dailyPercentageChange.isEmpty() ? "" : (" " + dailyPercentageChange))).add(Txt.parse(MConf.get().cmdFtopAmountOnlinePrefix.replace("%online%", String.valueOf(faction.getOnlinePlayers().size())).replace("%total%", String.valueOf(faction.getMPlayers().size())))).tooltip(lore);
                topData.add(mson);
            }
        }

        final Pager<Mson> pager = new Pager<>(this, Txt.parse(MConf.get().ptopPagerName), page, topData, (Msonifier<Mson>) (item, index) -> mson(Txt.parse("<a>%s. ", (index + 1))).add(topData.get(index)));

        pager.message();

        MixinMessage.get().msgOne(me, MConf.get().ftopCommandLastUpdateFooterMsg.replace("%time%", TaskFactionTopCalculate.get().getLastRunTime()));
    }

}