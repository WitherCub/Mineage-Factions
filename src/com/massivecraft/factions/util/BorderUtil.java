package com.massivecraft.factions.util;

import org.bukkit.Location;

public class BorderUtil {

    private static final BorderUtil i = new BorderUtil();

    public static BorderUtil get() {
        return i;
    }

    public boolean isOutsideOfBorder(Location loc) {
        final double size = loc.getWorld().getWorldBorder().getSize() / 2;

        double x = loc.getX();
        double z = loc.getZ();

        return (x > size - 1) || (-x > size) || (z > size - 1) || (-z > size);
    }

}