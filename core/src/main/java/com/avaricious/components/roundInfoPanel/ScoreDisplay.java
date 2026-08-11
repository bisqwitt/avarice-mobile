package com.avaricious.components.roundInfoPanel;

import com.avaricious.CreditNumber;
import com.avaricious.components.DigitalNumber;
import com.badlogic.gdx.math.Rectangle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ScoreDisplay {

    private static ScoreDisplay instance;

    public static ScoreDisplay I() {
        return instance == null ? instance = new ScoreDisplay() : instance;
    }

    private final float DIGIT_Y = 15f;

    private final DigitalNumber scoreNumber = new CreditNumber(0,
        new Rectangle(3f, DIGIT_Y + 1.75f, 7 / 9f, 11 / 9f), 0.9f);

    private final PropertyChangeSupport scoreChangeSupport = new PropertyChangeSupport(this);

    private ScoreDisplay() {
        scoreNumber.getIdleScaleEffect().setAllowed(false);
        setScoreNumber(300);
    }

    public void draw(float delta) {
        scoreNumber.draw(delta);
    }

    public void addToScore(float value) {
        setScoreNumber(getScoreNumber() + value);
    }

    public void removeFromScore(float value) {
        setScoreNumber(getScoreNumber() - value);
    }

    public void setScoreNumber(float value) {
        float oldScore = getScoreNumber();
        scoreNumber.setValue(value);
        updateScoreXLayout();

        scoreChangeSupport.firePropertyChange("score", oldScore, scoreNumber.getValue());
    }

    public float getScoreNumber() {
        return scoreNumber.getValue();
    }

    private void updateScoreXLayout() {
        float screenCenterX = 4.5f;
        scoreNumber.getFirstDigitBounds().x = screenCenterX - scoreNumber.getWidth() / 2f;
    }

    public boolean reachedRoundGoal() {
        return scoreNumber.getValue() >= RoundInfoPanel.I().getReachNumber().getValue();
    }

    public void addScoreChangeListener(PropertyChangeListener listener) {
        scoreChangeSupport.addPropertyChangeListener(listener);
    }

}
