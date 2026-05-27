package com.avaricious.network.match;

import com.avaricious.components.roundInfoPanel.PlayerHealths;
import com.avaricious.components.roundInfoPanel.PlayerScores;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.RunManager;
import com.badlogic.gdx.Gdx;

public class MatchService {

    public void onRoundEndWaiting() {
        Gdx.app.postRunnable(() -> {
            ScreenManager.I().getScreen(SlotScreen.class).showWaitingForOpponentText();
        });
    }

    public void onBothPlayersEndedRound() {
        Gdx.app.postRunnable(() -> {
            PlayerScores playerScores = PlayerScores.I();
            PlayerHealths playerHealths = PlayerHealths.I();

            if (playerScores.getPlayerScore() > playerScores.getEnemyScore()) {
                playerHealths.setEnemyHealth((int) playerHealths.getEnemyHealth() - 20);
            } else {
                playerHealths.setPlayerHealth((int) playerHealths.getPlayerHealth() - 20);
            }

            playerScores.setPlayerScoreNumber(0);
            playerScores.setEnemyScoreNumber(0);

            RunManager.I().getRoundTimer().startTimer();
            ScreenManager.I().getScreen(SlotScreen.class).onSpinButtonPressed();
        });
    }

    public void onOpponentHealthChanged(int newHealth) {
        Gdx.app.postRunnable(() -> {

        });
    }

    public void onOpponentScoreChanged(int newScore) {
        Gdx.app.postRunnable(() -> {
            PlayerScores.I().setEnemyScoreNumber(newScore);
        });
    }

}
