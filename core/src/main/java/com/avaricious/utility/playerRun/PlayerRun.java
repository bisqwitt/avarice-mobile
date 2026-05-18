package com.avaricious.utility.playerRun;

import com.avaricious.utility.gameState.GameState;

import java.util.ArrayList;
import java.util.List;

public class PlayerRun {

    public int playerHealth = 1000;
    public List<PlayerRoundEndScore> roundEndScores = new ArrayList<>();
    public List<GameState> gameStates = new ArrayList<>();

    public PlayerRun() {
    }

    public int getLastRoundEndScore() {
        if (roundEndScores.isEmpty()) return 0;
        return roundEndScores.get(roundEndScores.size() - 1).score;
    }

}
