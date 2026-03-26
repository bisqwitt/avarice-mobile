package com.avaricious;

import com.avaricious.bosses.AbstractBoss;
import com.avaricious.components.progressbar.ScoreProgressBar;
import com.avaricious.upgrades.cards.AbstractCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundsManager {

    private static RoundsManager instance;

    public static RoundsManager I() {
        return instance == null ? (instance = new RoundsManager()) : instance;
    }

    private AbstractBoss boss;

    private RoundsManager() {
        currentRound = 1;
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

    private final List<AbstractCard> playedCardsThisRound = new ArrayList<>();
    private boolean defenceTypeCardsDisabled = false;

    public void nextRound() {
        currentRound++;
        currentTargetScore = targetScorePerRound.get(currentRound);
        ScoreProgressBar.I().setMaxValue(currentTargetScore);

        if (currentRound % 3 == 0) boss = AbstractBoss.getRandomBoss();
        else if (isBossRound()) boss = null;

        playedCardsThisRound.clear();
        defenceTypeCardsDisabled = false;
    }

    public boolean isBossRound() {
        return boss != null;
    }

    public AbstractBoss getBoss() {
        return boss;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public int getCurrentTargetScore() {
        return currentTargetScore;
    }

    public void disableDefenceTypeCards() {
        defenceTypeCardsDisabled = true;
    }

    public boolean defenceTypeCardsDisabled() {
        return defenceTypeCardsDisabled;
    }

    public void onCardPlayed(AbstractCard card) {
        playedCardsThisRound.add(card);
    }

    public List<AbstractCard> getPlayedCardsThisRound() {
        return playedCardsThisRound;
    }
}
