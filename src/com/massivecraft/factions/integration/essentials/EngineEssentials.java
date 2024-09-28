package com.massivecraft.factions.integration.essentials;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinTeleport;
import com.massivecraft.massivecore.mixin.TeleporterException;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.teleport.Destination;
import com.massivecraft.massivecore.teleport.DestinationSimple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permissible;

public class EngineEssentials extends Engine {
    private static EngineEssentials i = new EngineEssentials();
    public static EngineEssentials get() { return i; }

    private Essentials essentials = null;

    @Override
    public void setActive(boolean active) {
        if(active) {
            try {
                essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            } catch (ClassCastException ignore) {
                active = false;
            }
        }
        super.setActive(active);
    }

    public void teleport(Player player, Destination destination, Permissible permissible) throws Exception {
        if(essentials != null) {
            User user = essentials.getUser(player);
            user.setLastLocation();

            DestinationSimple destinationSimple = (DestinationSimple) destination;
            Location location = destinationSimple.getPsInner().asBukkitLocation(true);

            user.getTeleport().teleport(location, null, PlayerTeleportEvent.TeleportCause.PLUGIN);

        } else MixinTeleport.get().teleport(player, destination, permissible);
    }

    public void teleport(Player player, Destination destination, int delay) throws Exception {
        if(essentials != null) {
            User user = essentials.getUser(player);
            user.setLastLocation();

            DestinationSimple destinationSimple = (DestinationSimple) destination;
            Location location = destinationSimple.getPsInner().asBukkitLocation(true);

            user.getTeleport().teleport(location, null, PlayerTeleportEvent.TeleportCause.PLUGIN);


        } else MixinTeleport.get().teleport(player, destination, delay);
    }
}
