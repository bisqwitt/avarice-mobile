package com.avaricious.network;

public class ScoreEntry {
    public String playerName;
    public int round;
    public int score;

    public ScoreEntry() {
    }

    public ScoreEntry(String playerName, int round, int score) {
        this.playerName = playerName;
        this.round = round;
        this.score = score;
    }
}
