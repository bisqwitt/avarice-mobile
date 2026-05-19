package com.avaricious.network.matchmaking;

import com.badlogic.gdx.Gdx;

public class MatchmakingService {

    public void onQueueJoined() {
        Gdx.app.postRunnable(() -> {
            Gdx.app.log("MATCHMAKING", "Queue joined");

            // Waiting for opponent Screen
        });
    }

    public void onMatchFound(String roomId, String playerId, String opponentId) {
        Gdx.app.postRunnable(() -> {
            Gdx.app.log("MATCHMAKING", "Start match");
            Gdx.app.log("MATCHMAKING", "Room: " + roomId);
            Gdx.app.log("MATCHMAKING", "Me: " + playerId);
            Gdx.app.log("MATCHMAKING", "Opponent: " + opponentId);

            // switch screen / update state here
        });
    }

    public void onOpponentLeft(String roomId) {
        Gdx.app.postRunnable(() -> {
            Gdx.app.log("GAME", "Opponent left match");

            // show message / return to menu
        });
    }

}
