package com.avaricious.network.match;

import com.avaricious.screens.PlayerCombatScreen;
import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.Gdx;

public class MatchService {

    public void onRoundEndWaiting() {
        Gdx.app.postRunnable(() -> {
            Gdx.app.log("MATCH", "Waiting for Opponent to finish turn");
            ScreenManager.I().getScreen(PlayerCombatScreen.class).onWaitingOnOpponent();
        });
    }

    public void onRoundEndResult(int opponentScore) {
        Gdx.app.postRunnable(() -> {
            Gdx.app.log("MATCH", "Both players finished their round");
            ScreenManager.I().getScreen(PlayerCombatScreen.class).onOpponentFinishedRound(opponentScore);
        });
    }

}
