package com.massivecraft.factions.task;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.ModuloRepeatTask;

import java.util.ArrayList;
import java.util.List;

public class TaskUpdateBoard extends ModuloRepeatTask {
    private static final TaskUpdateBoard i = new TaskUpdateBoard();
    public static TaskUpdateBoard get() { return i; }

    @Override
    public long getDelayMillis() {
        return MConf.get().updateBoardTimeMs;
    }

    public List<Board> boardsToUpdate = new ArrayList<>();

    @Override
    public void invoke(long l) {
        if(!boardsToUpdate.isEmpty()) {
            boardsToUpdate.forEach(Board::changed);
            boardsToUpdate.clear();
            Factions.get().log("Updated all Faction Boards.");
        }
    }
}
