package com.avaricious.components.roundInfoPanel;

import com.avaricious.components.DigitalNumber;
import com.avaricious.components.slot.Body;
import com.avaricious.network.NetworkController;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Observable;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RunManager;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ScoreDisplay extends Observable<ScoreState> {

    private static ScoreDisplay instance;

    public static ScoreDisplay I() {
        return instance == null ? instance = new ScoreDisplay() : instance;
    }

    private final TextureRegion multiplySymbol = Assets.I().get(AssetKey.MULT_SYMBOL);
    private final TextureRegion multiplySymbolShadow = Assets.I().get(AssetKey.MULT_SYMBOL_SHADOW);
    private final float multiplySymbolSize = 11f / 19f;

    private final float DIGIT_Y = 14.5f;
    private final float DIGIT_WIDTH = 7 / 11f;
    private final float DIGIT_HEIGHT = 11 / 11f;
    private final float DIGIT_OFFSET = 0.7f;

    private final DigitalNumber pointsNumber = new DigitalNumber(0, Assets.I().blue(), 3,
        new Rectangle(0.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

    private final DigitalNumber multiNumber = new DigitalNumber(0, Assets.I().red(), 3,
        new Rectangle(3.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET);

//    private final DigitalNumber streakNumber = new DigitalNumber(1, Assets.I().red(), 2,
//        new Rectangle(6.85f, DIGIT_Y, DIGIT_WIDTH, DIGIT_HEIGHT), DIGIT_OFFSET).setAsDecimal();


    float multiSymbol1X = 0f;
//    float multiSymbol2X = 0f;

    private final Body multiBody1 = new Body(new Vector2(0, 0));
    private final Body multiBody2 = new Body(new Vector2(0, 0));

    private ScoreDisplay() {
        clearPotentialScore();

        pointsNumber.getIdleScaleEffect().setAllowed(false);
        multiNumber.getIdleScaleEffect().setAllowed(false);
//        streakNumber.getIdleScaleEffect().setAllowed(false);
    }

    public void draw(float delta) {
        multiBody1.update(delta);
        multiBody2.update(delta);

//        Pencil.I().addDrawing(new TextureDrawing(
//            scoreDisplaySlot,
//            0.25f, 14.9f, 8.5f, 2.8f,
//            ZIndex.SCORE_DISPLAY, Assets.I().shadowColor()
//        ));

        pointsNumber.draw(delta);

        float multi1Y = DIGIT_Y + multiBody1.getIdleFloatYOffset();
        float multi1Sway = multiBody1.getIdleSwayEffect().getValue();
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbolShadow,
            multiSymbol1X, multi1Y, multiplySymbolSize, multiplySymbolSize,
            1f, multi1Sway, ZIndex.PATTERN_DISPLAY, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            multiplySymbol,
            multiSymbol1X, multi1Y + 0.1f, multiplySymbolSize, multiplySymbolSize,
            1f, multi1Sway, ZIndex.PATTERN_DISPLAY
        ));

        multiNumber.draw(delta);

//        float multi2Y = DIGIT_Y + multiBody2.getIdleFloatYOffset();
//        float multi2Sway = multiBody2.getIdleSwayEffect().getValue();
//        Pencil.I().addDrawing(new TextureDrawing(
//            multiplySymbolShadow,
//            multiSymbol2X, multi2Y, multiplySymbolSize, multiplySymbolSize,
//            1f, multi2Sway, ZIndex.PATTERN_DISPLAY, Assets.I().shadowColor()
//        ));
//        Pencil.I().addDrawing(new TextureDrawing(
//            multiplySymbol,
//            multiSymbol2X, multi2Y + 0.1f, multiplySymbolSize, multiplySymbolSize,
//            1, multi2Sway, ZIndex.PATTERN_DISPLAY
//        ));
//
//        streakNumber.draw(delta);
    }

    public void addPotentialValue(Type type, float amount) {
        setPotentialValue(type, getPotentialValueOf(type) + amount);
    }

    public void setPotentialValue(Type type, float value) {
        getNumberOf(type).setValue(value);

        updatePotentialScoreXLayout();

        PlayerScores.I().setPlayerScoreNumber(calcPotentialValue());
        NetworkController.I().match().onScoreChanged(RunManager.I().getRoundsManager().getCurrentRound(), calcPotentialValue());
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

    private DigitalNumber getNumberOf(Type type) {
//        return type == Type.POINTS ? pointsNumber :
//            type == Type.MULTI ? multiNumber : streakNumber;
        return type == Type.POINTS ? pointsNumber : multiNumber;
    }

    private void updatePotentialScoreXLayout() {
        float centerX = 4.5f; // center of the score display area
        float gap = 0.5f;

        float pointsWidth = pointsNumber.getWidth();
        float multiWidth = multiNumber.getWidth();
//        float streakWidth = streakNumber.getWidth();

        float symbolWidth = multiplySymbolSize;

//        float totalWidth =
//            pointsWidth +
//                gap + symbolWidth +
//                gap + multiWidth +
//                gap + symbolWidth +
//                gap + streakWidth;

        float totalWidth =
            pointsWidth +
                gap + symbolWidth +
                gap + multiWidth;

        float x = centerX - totalWidth / 2f;

        pointsNumber.getFirstDigitBounds().x = x;
        x += pointsWidth + gap;

        multiSymbol1X = x;
        x += symbolWidth + gap;

        multiNumber.getFirstDigitBounds().x = x;
//        x += multiWidth + gap;

//        multiSymbol2X = x;
//        x += symbolWidth + gap;

//        streakNumber.getFirstDigitBounds().x = x;
    }

    public void setScoreState(ScoreState scoreState) {
        setPotentialValue(Type.POINTS, scoreState.points);
        setPotentialValue(Type.MULTI, scoreState.multi);
        setPotentialValue(Type.STREAK, scoreState.streak);
    }

    @Override
    protected ScoreState snapshot() {
        return new ScoreState(
            pointsNumber.getValue(),
            multiNumber.getValue(),
//            streakNumber.getValue(),
            1,
            calcPotentialValue());
    }

    public enum Type {
        POINTS,
        MULTI,
        STREAK
    }

}
