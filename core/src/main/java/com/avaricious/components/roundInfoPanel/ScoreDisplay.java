package com.avaricious.components.roundInfoPanel;

import com.avaricious.RoundsManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.progressbar.ScoreProgressBar;
import com.avaricious.components.slot.Body;
import com.avaricious.network.NetworkController;
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
import com.badlogic.gdx.math.Vector2;

public class ScoreDisplay extends Observable<ScoreState> {

    private static ScoreDisplay instance;

    public static ScoreDisplay I() {
        return instance == null ? instance = new ScoreDisplay() : instance;
    }

    private final ScoreProgressBar scoreProgressBar = ScoreProgressBar.I();

    private final TextureRegion scoreDisplaySlot = Assets.I().get(AssetKey.SCORE_DISPLAY_SLOT);
    private final TextureRegion multiplySymbol = Assets.I().get(AssetKey.MULT_SYMBOL);
    private final TextureRegion multiplySymbolShadow = Assets.I().get(AssetKey.MULT_SYMBOL_SHADOW);
    private final float multiplySymbolSize = 11f / 19f;

    private final float DIGIT_Y = 15.5f;
    private final float DIGIT_WIDTH = 7 / 11f;
    private final float DIGIT_HEIGHT = 11 / 11f;
    private final float DIGIT_OFFSET = 0.7f;

    private final DigitalNumber pointsNumber = new DigitalNumber(0, Assets.I().blue(), 3,
        new Rectangle(0.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber multiNumber = new DigitalNumber(0, Assets.I().red(), 3,
        new Rectangle(3.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber streakNumber = new DigitalNumber(1, Assets.I().red(), 2,
        new Rectangle(6.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET).setAsDecimal();

    private final DigitalNumber currentScoreNumber = new DigitalNumber(0, Assets.I().lightColor(), 1,
        new Rectangle(0.25f, DIGIT_Y - 1f, 7 / 14f, 11 / 14f), 0.55f);
    private final DigitalNumber currentEnemyScoreNumber = new DigitalNumber(0, Assets.I().lightColor(), 1,
        new Rectangle(6, DIGIT_Y - 1f, 7 / 14f, 11 / 14f), 0.55f);

    float multiSymbol1X = 0f;
    float multiSymbol2X = 0f;

    private final Body multiBody1 = new Body(new Vector2(0, 0));
    private final Body multiBody2 = new Body(new Vector2(0, 0));

    private ScoreDisplay() {
        clearPotentialScore();

        pointsNumber.getIdleScaleEffect().setAllowed(false);
        multiNumber.getIdleScaleEffect().setAllowed(false);
        streakNumber.getIdleScaleEffect().setAllowed(false);
        currentScoreNumber.getIdleScaleEffect().setAllowed(false);
        currentEnemyScoreNumber.getIdleScaleEffect().setAllowed(false);
    }

    public void draw(float delta, float unfoldAmount) {
        float t = MathUtils.clamp(unfoldAmount, 0f, 1f);
        float smoothT = Interpolation.smoother.apply(t);
        multiBody1.update(delta);
        multiBody2.update(delta);

        ZIndex zIndex = t == 0f
            ? ZIndex.PATTERN_DISPLAY
            : ZIndex.ROUND_INFO_PANEL_UNFOLDED;

        pointsNumber.setZIndex(zIndex);
        multiNumber.setZIndex(zIndex);
        streakNumber.setZIndex(zIndex);
        currentScoreNumber.setZIndex(zIndex);
        currentEnemyScoreNumber.setZIndex(zIndex);

        float digitY = MathUtils.lerp(DIGIT_Y, DIGIT_Y - 1.1f, smoothT);

        pointsNumber.getFirstDigitBounds().y = digitY;
        multiNumber.getFirstDigitBounds().y = digitY;
        streakNumber.getFirstDigitBounds().y = digitY;

//        Pencil.I().addDrawing(new TextureDrawing(
//            scoreDisplaySlot,
//            0.25f, 14.9f, 8.5f, 2.8f,
//            ZIndex.SCORE_DISPLAY, Assets.I().shadowColor()
//        ));

        pointsNumber.draw(delta);

        float multi1Y = digitY + multiBody1.getIdleFloatYOffset();
        float multi1Sway = multiBody1.getIdleSwayEffect().getValue();
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbolShadow,
            multiSymbol1X, multi1Y, multiplySymbolSize, multiplySymbolSize,
            1f, multi1Sway, zIndex, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbol,
            multiSymbol1X, multi1Y + 0.1f, multiplySymbolSize, multiplySymbolSize,
            1f, multi1Sway, zIndex
        ));

        multiNumber.draw(delta);

        float multi2Y = digitY + multiBody2.getIdleFloatYOffset();
        float multi2Sway = multiBody2.getIdleSwayEffect().getValue();
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbolShadow,
            multiSymbol2X, multi2Y, multiplySymbolSize, multiplySymbolSize,
            1f, multi2Sway, zIndex, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbol,
            multiSymbol2X, multi2Y + 0.1f, multiplySymbolSize, multiplySymbolSize,
            1, multi2Sway, zIndex
        ));

        streakNumber.draw(delta);

        currentScoreNumber.draw(delta);
        currentEnemyScoreNumber.draw(delta);
//        scoreProgressBar.draw();
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

        updateCurrentScoreNumber();
        NetworkController.I().match().onScoreChanged(RoundsManager.I().getCurrentRound(), calcPotentialValue());
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

    public float getScore() {
        return scoreProgressBar.getCurrentValue();
    }

    private DigitalNumber getNumberOf(Type type) {
        return type == Type.POINTS ? pointsNumber :
            type == Type.MULTI ? multiNumber : streakNumber;
    }

    private void updatePotentialScoreXLayout() {
        float centerX = 4.5f; // center of the score display area
        float gap = 0.5f;

        float pointsWidth = pointsNumber.getWidth();
        float multiWidth = multiNumber.getWidth();
        float streakWidth = streakNumber.getWidth();

        float symbolWidth = multiplySymbolSize;

        float totalWidth =
            pointsWidth +
                gap + symbolWidth +
                gap + multiWidth +
                gap + symbolWidth +
                gap + streakWidth;

        float x = centerX - totalWidth / 2f;

        pointsNumber.getFirstDigitBounds().x = x;
        x += pointsWidth + gap;

        multiSymbol1X = x;
        x += symbolWidth + gap;

        multiNumber.getFirstDigitBounds().x = x;
        x += multiWidth + gap;

        multiSymbol2X = x;
        x += symbolWidth + gap;

        streakNumber.getFirstDigitBounds().x = x;
    }

    private void updateCurrentScoreNumber() {
        currentScoreNumber.setValue(calcPotentialValue());
    }

    public void setCurrentEnemyScoreNumber(int value) {
        currentEnemyScoreNumber.setValue(value);
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
