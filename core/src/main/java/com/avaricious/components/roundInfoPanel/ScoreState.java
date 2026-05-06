package com.avaricious.components.roundInfoPanel;

public class ScoreState {

    public float currentScore;
    public float points;
    public float multi;
    public float streak;

    public ScoreState() {
    }

    public ScoreState(float currentScore, float points, float multi, float streak) {
        this.currentScore = currentScore;
        this.points = points;
        this.multi = multi;
        this.streak = streak;
    }

}
