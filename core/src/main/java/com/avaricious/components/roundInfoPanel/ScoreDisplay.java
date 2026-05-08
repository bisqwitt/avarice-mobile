package com.avaricious.components.roundInfoPanel;

import com.avaricious.RoundsManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.progressbar.ScoreProgressBar;
import com.avaricious.screens.ScreenManager;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Observable;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ScoreDisplay extends Observable<ScoreState> {

    private static ScoreDisplay instance;

    public static ScoreDisplay I() {
        return instance == null ? instance = new ScoreDisplay() : instance;
    }

    private ScoreDisplay() {
        clearPotentialScore();
    }

    private final ScoreProgressBar scoreProgressBar = ScoreProgressBar.I();

    private final TextureRegion multiplySymbol = Assets.I().get(AssetKey.MULT_SYMBOL);
    private final float multiplySymbolSize = 11f / 25f;

    private final float DIGIT_Y = 15.65f;
    private final float DIGIT_WIDTH = 7 / 13.5f;
    private final float DIGIT_HEIGHT = 11 / 13.5f;
    private final float DIGIT_OFFSET = 0.7f;

    private final DigitalNumber pointsNumber = new DigitalNumber(0, Assets.I().blue(), 3,
        new Rectangle(0.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber multiNumber = new DigitalNumber(0, Assets.I().red(), 3,
        new Rectangle(3.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber streakNumber = new DigitalNumber(1, Assets.I().red(), 2,
        new Rectangle(6.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET).setAsDecimal();

    float multiplySymbol1X = 0f;
    float multiplySymbol2X = 0f;

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

        pointsNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbol,
            multiplySymbol1X, digitY + 0.1f, multiplySymbolSize, multiplySymbolSize,
            zIndex
        ));

        multiNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbol,
            multiplySymbol2X, digitY + 0.1f, multiplySymbolSize, multiplySymbolSize,
            zIndex
        ));

        streakNumber.draw(delta);
        scoreProgressBar.draw();
    }

    public void addScore(int amount) {
        setScore(scoreProgressBar.getCurrentValue() + amount);
    }

    public void setScore(float score) {
        scoreProgressBar.setCurrentValue(score);

        notifyChanged(snapshot());
    }

    public void addPotentialValue(Type type, float amount) {
        setPotentialValue(type, getPotentialValueOf(type) + amount);
    }

    public void setPotentialValue(Type type, float value) {
        getNumberOf(type).setValue(value);
        scoreProgressBar.setPotentialValue(getPotentialValueOf(Type.POINTS) * Math.max(getPotentialValueOf(Type.MULTI), 1) * getPotentialValueOf(Type.STREAK));

        updatePotentialScoreXLayout();

        notifyChanged(snapshot());
    }

    public float getPotentialValueOf(Type type) {
        return getNumberOf(type).getValue();
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
        return scoreProgressBar.getCurrentValue() >= RoundsManager.I().getCurrentTargetScore();
    }

    private DigitalNumber getNumberOf(Type type) {
        return type == Type.POINTS ? pointsNumber :
            type == Type.MULTI ? multiNumber : streakNumber;
    }

    private void updatePotentialScoreXLayout() {
        float width = pointsNumber.getWidth() + 1 + multiNumber.getWidth() + 1 + streakNumber.getWidth();
        float pos = ScreenManager.getViewport().getWorldWidth() / 2f - width / 2f;

        pointsNumber.getFirstDigitBounds().x = pos;
        pos += pointsNumber.getWidth() + 0.4f;

        multiplySymbol1X = pos;
        pos += multiplySymbolSize + 0.35f;

        multiNumber.getFirstDigitBounds().x = pos;
        pos += multiNumber.getWidth() + 0.4f;

        multiplySymbol2X = pos;
        pos += multiplySymbolSize + 0.35f;

        streakNumber.getFirstDigitBounds().x = pos;
    }

    public void setScoreState(ScoreState scoreState) {
        setScore(scoreState.currentScore);
        setPotentialValue(Type.POINTS, scoreState.points);
        setPotentialValue(Type.MULTI, scoreState.multi);
        setPotentialValue(Type.STREAK, scoreState.streak);
    }

    @Override
    protected ScoreState snapshot() {
        return new ScoreState(
            scoreProgressBar.getCurrentValue(),
            pointsNumber.getValue(),
            multiNumber.getValue(),
            streakNumber.getValue());
    }

    public enum Type {
        POINTS,
        MULTI,
        STREAK
    }

}
