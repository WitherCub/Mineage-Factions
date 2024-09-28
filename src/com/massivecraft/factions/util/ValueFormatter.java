package com.massivecraft.factions.util;

import java.text.DecimalFormat;

public class ValueFormatter {

    private static String[] suffix = new String[]{"", "K", "M", "B", "T"};
    private static int MAX_LENGTH = 6;

    public static String format(long number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

}