package com.avaricious.network.match;

import com.avaricious.network.SocketClient;
import com.avaricious.network.SocketEvents;
import com.badlogic.gdx.Gdx;

public class MatchController {

    private final SocketClient socketClient;
    private final MatchService service = new MatchService();

    public MatchController(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public void registerListeners() {
        socketClient.on(SocketEvents.ROUND_END_WAITING, args -> {
            service.onRoundEndWaiting();
        });

        socketClient.onJson(SocketEvents.ROUND_END_RESULT, data -> {
            int opponentScore = data.getInt("opponentScore");
            service.onRoundEndResult(opponentScore);
        });

        socketClient.onJson(SocketEvents.OPPONENT_HEALTH_CHANGED, data -> {
            int opponentHealth = data.getInt("health");
            service.onOpponentHealthChanged(opponentHealth);
        });
    }

    public void sendRoundEndScore(int round, int score) {
        socketClient.emitJson(SocketEvents.ROUND_SCORE_SUBMIT, payload -> {
            payload.put("round", round);
            payload.put("score", score);
        });

        Gdx.app.log("MATCH", "Sent round end score: " + score);
    }

    public void onHealthChanged(int newHealth) {
        socketClient.emitJson(SocketEvents.HEALTH_UPDATE, payload -> {
            payload.put("newHealth", newHealth);
        });
    }

    public void onScoreChanged(int round, int score) {
        socketClient.emitJson(SocketEvents.SCORE_CHANGED, payload -> {
            payload.put("round", round);
            payload.put("score", score);
            payload.put("msSinceRoundStart", 0);
        });
    }

}
