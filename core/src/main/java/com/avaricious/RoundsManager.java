package com.avaricious;

import java.util.HashMap;
import java.util.Map;

public class RoundsManager {

    private static RoundsManager instance;

    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private RoundsManager() {
        currentRound = 1;
        currentTargetScore = targetScorePerRound.get(currentRound);
    }

    private final Map<Integer, Integer> targetScorePerRound = new HashMap<Integer, Integer>() {{
        put(1, 300);
        put(2, 450);
        put(3, 600);

        put(4, 800);
        put(5, 1200);
        put(6, 1600);

        put(7, 2000);
        put(8, 3000);
        put(9, 4000);

        put(10, 5000);
        put(11, 7500);
        put(12, 10000);
    }};

    private Integer currentRound;
    private int currentTargetScore;

    private Integer handsLeft;
    private Integer spinsLeft;

    public void nextRound() {
        currentRound++;
        currentTargetScore = targetScorePerRound.get(currentRound);
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public int getCurrentTargetScore() {
        return currentTargetScore;
    }

    public void minusOneHand() {
        handsLeft--;
    }

    public void minusOneSpin() {
        spinsLeft--;
    }

    public Integer getAppliesLeft() {
        return handsLeft;
    }

    public Integer getSpinsLeft() {
        return spinsLeft;
    }
}
