package com.avaricious;

import com.avaricious.bosses.AbstractBoss;
import com.avaricious.components.progressbar.ScoreProgressBar;
import com.avaricious.network.NetworkController;
import com.avaricious.utility.GameStateLogger;
import com.avaricious.utility.Observable;

import java.util.HashMap;
import java.util.Map;

public class RoundsManager extends Observable<Integer> {

    private static RoundsManager instance;

    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private RoundsManager() {
        setCurrentRound(1);
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

    private int playerHealth = 1000;
    private int opponentHealth = 1000;

    public void nextRound() {
        setCurrentRound(currentRound + 1);
    }

    public boolean isShopRound() {
        return currentRound % 3 == 1 && currentRound != 1;
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
//        isPlayerCombatRound = ;

        GameStateLogger.I().onNewRound();

        notifyChanged(snapshot());
    }

    public AbstractBoss getBoss() {
        return null;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
        NetworkController.I().match().onHealthChanged(playerHealth);
    }

    public int getOpponentHealth() {
        return opponentHealth;
    }

    public void setOpponentHealth(int opponentHealth) {
        this.opponentHealth = opponentHealth;
    }

    @Override
    protected Integer snapshot() {
        return currentRound;
    }
}
