package com.avaricious.utility;

import com.avaricious.RoundsManager;
import com.avaricious.components.roundInfoPanel.RoundTimer;

import java.util.UUID;

public class RunManager {

    private static RunManager instance;

    public static RunManager I() {
        return instance == null ? instance = new RunManager() : instance;
    }

    private final RoundsManager roundsManager = new RoundsManager();
    private final RoundTimer roundTimer = new RoundTimer();

    private String runId;

    private RunManager() {
    }

    public void newRun() {
        runId = UUID.randomUUID().toString();

        roundTimer.startTimer();
        roundsManager.nextRound();
    }

    public RoundsManager getRoundsManager() {
        return roundsManager;
    }

    public RoundTimer getRoundTimer() {
        return roundTimer;
    }

    public String getRunId() {
        return runId;
    }
}
