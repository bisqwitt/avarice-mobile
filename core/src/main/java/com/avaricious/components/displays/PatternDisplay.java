package com.avaricious.components.displays;

import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class PatternDisplay {

    private static PatternDisplay instance;

    public static PatternDisplay I() {
        return instance == null ? instance = new PatternDisplay() : instance;
    }

    private PatternDisplay() {
    }

    private final TextureRegion xSymbol = Assets.I().get(AssetKey.MULT_SYMBOL);

    private final float DIGIT_Y = 14.9f;
    private final float DIGIT_WIDTH = 8 / 15f;
    private final float DIGIT_HEIGHT = 14 / 15f;
    private final float DIGIT_OFFSET = 0.7f;

    private final DigitalNumber pointsNumber = new DigitalNumber(0, Assets.I().blue(), 3,
        new Rectangle(0.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber multiNumber = new DigitalNumber(0, Assets.I().red(), 3,
        new Rectangle(3.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber streakNumber = new DigitalNumber(0, Assets.I().red(), 2,
        new Rectangle(6.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    public void draw(SpriteBatch batch, float delta) {
        float xSymbolSize = 11 / 20f;

        pointsNumber.draw(batch, delta);
        batch.draw(xSymbol, pointsNumber.getBounds().x + 2.25f, DIGIT_Y, xSymbolSize, xSymbolSize);
        multiNumber.draw(batch, delta);
        batch.draw(xSymbol, multiNumber.getBounds().x + 2.25f, DIGIT_Y, xSymbolSize, xSymbolSize);
        streakNumber.draw(batch, delta);
    }

    public void addTo(Type type, float amount) {
        setValueOf(type, getValueOf(type) + amount);
    }

    public void setValueOf(Type type, float value) {
        getNumberOf(type).setScore((int) value);
    }

    public float getValueOf(Type type) {
        return getNumberOf(type).getScore();
    }

    public void clearNumbers() {
        setValueOf(Type.POINTS, 0);
        setValueOf(Type.MULTI, 0);
        setValueOf(Type.STREAK, 0);
    }

    public boolean isClear() {
        return getValueOf(Type.POINTS) == 0 && getValueOf(Type.MULTI) == 0 && getValueOf(Type.STREAK) == 0;
    }

    public int calcScore() {
        return Math.round(getValueOf(PatternDisplay.Type.POINTS) * getValueOf(Type.MULTI) * getValueOf(Type.STREAK));
    }

    private DigitalNumber getNumberOf(Type type) {
        return type == Type.POINTS ? pointsNumber :
            type == Type.MULTI ? multiNumber : streakNumber;
    }

    public enum Type {
        POINTS,
        MULTI,
        STREAK
    }

}
