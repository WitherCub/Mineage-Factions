package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.Skulls;
import com.massivecraft.massivecore.ModuloRepeatTask;

import java.util.Calendar;

public class TaskResetKicks extends ModuloRepeatTask {

    private static TaskResetKicks i = new TaskResetKicks();

    public static TaskResetKicks get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 20000L;
    }

    @Override
    public void invoke(long l) {
        if (MConf.get().resetRosterKicksDailyAtMidnight && MConf.get().isRosterChangeable()) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(System.currentTimeMillis());

            Calendar lastUpdatedCalendar = Calendar.getInstance();
            lastUpdatedCalendar.setTimeInMillis(Skulls.get().getKicksLastResetMillis());

            if (currentCalendar.get(Calendar.DAY_OF_MONTH) != lastUpdatedCalendar.get(Calendar.DAY_OF_MONTH)) {
                for (Faction faction : FactionColl.get().getAll()) {
                    faction.msg(MConf.get().rosterKicksResetMsg);
                    faction.setRosterKicksRemaining(MConf.get().defaultRosterKicks);
                }
                Skulls.get().setKicksLastResetMillis(System.currentTimeMillis());
                Skulls.get().changed();
            }
        }
    }
}