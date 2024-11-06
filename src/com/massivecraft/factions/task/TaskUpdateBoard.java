package com.massivecraft.factions.task;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.ModuloRepeatTask;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TaskUpdateBoard extends ModuloRepeatTask {
    private static final TaskUpdateBoard i = new TaskUpdateBoard();
    public static TaskUpdateBoard get() { return i; }

    @Override
    public long getDelayMillis() {
        return MConf.get().updateBoardTimeMs;
    }

    @Override
    public boolean isSync() {
        return false;
    }

    public List<Board> boardsToUpdate = new ArrayList<>();

    @Override
    public void invoke(long l) {
        if(!boardsToUpdate.isEmpty()) {
            boardsToUpdate.forEach(Board::changed);
            Bukkit.getScheduler().runTask(Factions.get(), () -> {
                BoardColl.get().onTickFixed();
            });
            boardsToUpdate.clear();
            Factions.get().log("Updated all Faction Boards.");
        }
    }
}
