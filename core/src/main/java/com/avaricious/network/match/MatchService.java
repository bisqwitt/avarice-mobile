package com.avaricious.network.match;

import com.avaricious.RoundsManager;
import com.avaricious.components.roundInfoPanel.PlayerHealths;
import com.avaricious.components.roundInfoPanel.PlayerScores;
import com.avaricious.components.roundInfoPanel.RoundTimer;
import com.badlogic.gdx.Gdx;

public class MatchService {

    public void onRoundEndWaiting() {
        Gdx.app.postRunnable(() -> {
            // visualize waiting on opponent to end round
        });
    }

    public void onBothPlayersEndedRound() {
        Gdx.app.postRunnable(() -> {
            PlayerScores playerScores = PlayerScores.I();
            PlayerHealths playerHealths = PlayerHealths.I();

            if(playerScores.getPlayerScore() > playerScores.getEnemyScore()) {
                playerHealths.setEnemyHealth((int) playerHealths.getEnemyHealth() - 20);
            } else {
                playerHealths.setPlayerHealth((int) playerHealths.getPlayerHealth() - 20);
            }

            playerScores.setPlayerScoreNumber(0);
            playerScores.setEnemyScoreNumber(0);

            RoundTimer.I().startTimer();
        });
    }

    public void onOpponentHealthChanged(int newHealth) {
        Gdx.app.postRunnable(() -> {
            RoundsManager.I().setOpponentHealth(newHealth);
        });
    }

    public void onOpponentScoreChanged(int newScore) {
        Gdx.app.postRunnable(() -> {
            PlayerScores.I().setEnemyScoreNumber(newScore);
        });
    }

}
