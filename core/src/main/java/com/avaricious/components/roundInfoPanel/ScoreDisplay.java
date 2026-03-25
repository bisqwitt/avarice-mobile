package com.avaricious.components.roundInfoPanel;

import com.avaricious.RainbowProgressBar;
import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ScoreDisplay {

    private static ScoreDisplay instance;

    public static ScoreDisplay I() {
        return instance == null ? instance = new ScoreDisplay() : instance;
    }

    private ScoreDisplay() {
    }

    private final TextureRegion xSymbol = Assets.I().get(AssetKey.MULT_SYMBOL);

    private final float DIGIT_Y = 15.25f;
    private final float DIGIT_WIDTH = 7 / 15f;
    private final float DIGIT_HEIGHT = 11 / 15f;
    private final float DIGIT_OFFSET = 0.7f;

    private final DigitalNumber pointsNumber = new DigitalNumber(0, Assets.I().blue(), 3,
        new Rectangle(0.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber multiNumber = new DigitalNumber(0, Assets.I().red(), 3,
        new Rectangle(3.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber streakNumber = new DigitalNumber(0, Assets.I().red(), 2,
        new Rectangle(6.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    public void draw(float delta, float unfoldAmount) {
        float t = MathUtils.clamp(unfoldAmount, 0f, 1f);
        float smoothT = Interpolation.smoother.apply(t);

        ZIndex zIndex = t == 0f
            ? ZIndex.PATTERN_DISPLAY
            : ZIndex.ROUND_INFO_PANEL_UNFOLDED;

        pointsNumber.setZIndex(zIndex);
        multiNumber.setZIndex(zIndex);
        streakNumber.setZIndex(zIndex);

        float digitY = MathUtils.lerp(DIGIT_Y, DIGIT_Y - 1.1f, smoothT);

        pointsNumber.getBounds().y = digitY;
        multiNumber.getBounds().y = digitY;
        streakNumber.getBounds().y = digitY;

        pointsNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            xSymbol,
            new Rectangle(pointsNumber.getBounds().x + 2.25f, digitY, 11f / 25f, 11f / 25f),
            zIndex
        ));

        multiNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            xSymbol,
            new Rectangle(multiNumber.getBounds().x + 2.25f, digitY, 11f / 25f, 11f / 25f),
            zIndex
        ));

        streakNumber.draw(delta);
    }

    public void addTo(Type type, float amount) {
        setValueOf(type, getValueOf(type) + amount);
        RainbowProgressBar.I().setValue(getValueOf(Type.POINTS) * getValueOf(Type.MULTI) * getValueOf(Type.STREAK));
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
        return Math.round(getValueOf(ScoreDisplay.Type.POINTS) * getValueOf(Type.MULTI) * getValueOf(Type.STREAK));
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
