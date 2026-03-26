package com.avaricious.components.roundInfoPanel;

import com.avaricious.RoundsManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.progressbar.ScoreProgressBar;
import com.avaricious.screens.ScreenManager;
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

    private final float DIGIT_Y = 16.25f;
    private final float DIGIT_WIDTH = 7 / 15f;
    private final float DIGIT_HEIGHT = 11 / 15f;
    private final float DIGIT_OFFSET = 0.7f;

    private final DigitalNumber scoreNumber = new DigitalNumber(0, Assets.I().lightColor(), 5,
        new Rectangle(2.75f, 17.5f, 7 / 10f, 11 / 10f), 0.9f);

    private final DigitalNumber pointsNumber = new DigitalNumber(0, Assets.I().blue(), 3,
        new Rectangle(0.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber multiNumber = new DigitalNumber(0, Assets.I().red(), 3,
        new Rectangle(3.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber streakNumber = new DigitalNumber(1, Assets.I().red(), 2,
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

        pointsNumber.getFirstDigitBounds().y = digitY;
        multiNumber.getFirstDigitBounds().y = digitY;
        streakNumber.getFirstDigitBounds().y = digitY;

        scoreNumber.getFirstDigitBounds().x = ScreenManager.getViewport().getWorldWidth() / 2f - scoreNumber.getWidth() / 2;
        scoreNumber.draw(delta);

        pointsNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            xSymbol,
            new Rectangle(pointsNumber.getFirstDigitBounds().x + 2.25f, digitY, 11f / 25f, 11f / 25f),
            zIndex
        ));

        multiNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            xSymbol,
            new Rectangle(multiNumber.getFirstDigitBounds().x + 2.25f, digitY, 11f / 25f, 11f / 25f),
            zIndex
        ));

        streakNumber.draw(delta);
    }

    public void addScore(int amount) {
        setScore(scoreNumber.getScore() + amount);
    }

    public void setScore(int score) {
        scoreNumber.setScore(score);
        ScoreProgressBar.I().setCurrentValue(score);
    }

    public void addPotentialValue(Type type, float amount) {
        setPotentialValue(type, getPotentialValueOf(type) + amount);
    }

    public void setPotentialValue(Type type, float value) {
        getNumberOf(type).setScore((int) value);
        ScoreProgressBar.I().setOptionalValue(getPotentialValueOf(Type.POINTS) * Math.max(getPotentialValueOf(Type.MULTI), 1) * getPotentialValueOf(Type.STREAK));
    }

    public float getPotentialValueOf(Type type) {
        return getNumberOf(type).getScore();
    }

    public void clearPotentialScore() {
        setPotentialValue(Type.POINTS, 0);
        setPotentialValue(Type.MULTI, 0);
        setPotentialValue(Type.STREAK, 1);
    }

    public boolean isClear() {
        return getPotentialValueOf(Type.POINTS) == 0 && getPotentialValueOf(Type.MULTI) == 0 && getPotentialValueOf(Type.STREAK) == 0;
    }

    public int calcPotentialValue() {
        return Math.round(getPotentialValueOf(ScoreDisplay.Type.POINTS) * getPotentialValueOf(Type.MULTI) * getPotentialValueOf(Type.STREAK));
    }

    public boolean targetScoreReached() {
        return scoreNumber.getScore() >= RoundsManager.I().getCurrentTargetScore();
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
