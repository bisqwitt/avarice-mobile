package com.avaricious;

import com.avaricious.bosses.AbstractBoss;
import com.avaricious.components.progressbar.ScoreProgressBar;
import com.avaricious.utility.GameStateLogger;
import com.avaricious.utility.Observable;

import java.util.HashMap;
import java.util.Map;

public class RoundsManager extends Observable<Integer> {

    private static RoundsManager instance;

    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private AbstractBoss boss;

    private RoundsManager() {
        setCurrentRound(3);
        currentTargetScore = targetScorePerRound.get(currentRound);
        ScoreProgressBar.I().setMaxValue(currentTargetScore);
    }

    private final Map<Integer, Integer> targetScorePerRound = new HashMap<Integer, Integer>() {{
        put(1, 150);
        put(2, 225);
        put(3, 300);

        put(4, 500);
        put(5, 750);
        put(6, 1000);

        put(7, 1400);
        put(8, 2100);
        put(9, 2800);

        put(10, 5000);
        put(11, 7500);
        put(12, 10000);
    }};

    private Integer currentRound;
    private int currentTargetScore;
    private boolean isPlayerCombatRound = false;

    public void nextRound() {
        setCurrentRound(currentRound + 1);
    }

    public boolean isBossRound() {
        return boss != null;
    }

    public AbstractBoss getBoss() {
        return boss;
    }

    public boolean isPlayerCombatRound() {
        return isPlayerCombatRound;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public int getCurrentTargetScore() {
        return currentTargetScore;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;

        currentTargetScore = targetScorePerRound.get(currentRound);
        ScoreProgressBar.I().setMaxValue(currentTargetScore);

//        if (currentRound % 3 == 0) boss = AbstractBoss.getRandomBoss();
//        else if (isBossRound()) boss = null;
        isPlayerCombatRound = currentRound % 3 == 0;

        GameStateLogger.I().onNewRound();

        notifyChanged(snapshot());
    }


    @Override
    protected Integer snapshot() {
        return currentRound;
    }
}
