package com.avaricious.components.progressbar;

import com.avaricious.RoundsManager;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.FontDrawing;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ScoreProgressBar {

    private static ScoreProgressBar instance;

    public static ScoreProgressBar I() {
        return instance == null ? instance = new ScoreProgressBar() : instance;
    }

    private static final float BAR_X = 0.5f;
    private static final float BAR_Y = 13.9f;
    private static final float BAR_WIDTH = 8f;
    private static final float BAR_HEIGHT = 0.25f;

    // Higher = faster animation
    private static final float ANIMATION_SPEED = 10f;

    private final TextureRegion white = Assets.I().get(AssetKey.WHITE_PIXEL);
    private final TextureRegion yellow = Assets.I().get(AssetKey.YELLOW_PIXEL);
    private final TextureRegion darkerYellow = Assets.I().get(AssetKey.PROGRESSBAR_DARKER_YELLOW_PIXEL);

    private final GlyphLayout targetTxt = new GlyphLayout();

    private float maxValue = 1f;

    // Target values
    private float currentValue;
    private float optionalValue;

    // Animated/displayed values
    private float displayedCurrentValue;
    private float displayedOptionalValue;

    private ScoreProgressBar() {
    }

    public void update() {
        targetTxt.setText(Assets.I().getSmallFont(), (int) displayedOptionalValue + " / " + RoundsManager.I().getCurrentTargetScore());

        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);

        displayedCurrentValue = animateTowards(
            displayedCurrentValue,
            clamp(currentValue, 0f, maxValue),
            delta
        );

        displayedOptionalValue = animateTowards(
            displayedOptionalValue,
            clamp(optionalValue, 0f, maxValue),
            delta
        );
    }

    public void draw() {
        update();

        float currentValueWidth = maxValue <= 0f ? 0f : (displayedCurrentValue / maxValue) * BAR_WIDTH;
        float optionalValueWidth = maxValue <= 0f ? 0f : (displayedOptionalValue / maxValue) * BAR_WIDTH;

        Pencil.I().addDrawing(new TextureDrawing(white,
            new Rectangle(BAR_X, BAR_Y - 0.05f, BAR_WIDTH, 0.05f), ZIndex.SCORE_DISPLAY));
        Pencil.I().addDrawing(new TextureDrawing(white,
            new Rectangle(BAR_X, BAR_Y + BAR_HEIGHT, BAR_WIDTH, 0.05f), ZIndex.SCORE_DISPLAY));
        Pencil.I().addDrawing(new TextureDrawing(white,
            new Rectangle(BAR_X - 0.05f, BAR_Y - 0.05f, 0.05f, 0.35f), ZIndex.SCORE_DISPLAY));
        Pencil.I().addDrawing(new TextureDrawing(white,
            new Rectangle(BAR_X + BAR_WIDTH, BAR_Y - 0.05f, 0.05f, 0.35f), ZIndex.SCORE_DISPLAY));

        Pencil.I().addDrawing(new TextureDrawing(darkerYellow,
            new Rectangle(BAR_X, BAR_Y, optionalValueWidth, BAR_HEIGHT), ZIndex.SCORE_DISPLAY));
        Pencil.I().addDrawing(new TextureDrawing(yellow,
            new Rectangle(BAR_X, BAR_Y, currentValueWidth, BAR_HEIGHT), ZIndex.SCORE_DISPLAY));

        Pencil.I().addDrawing(new FontDrawing(Assets.I().getSmallFont(), targetTxt,
            new Vector2(((BAR_X + BAR_WIDTH) * 100 - (targetTxt.width)), (BAR_Y - 0.25f) * 100), ZIndex.SCORE_DISPLAY));
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = Math.max(0.0001f, maxValue);
        this.currentValue = clamp(currentValue, 0f, this.maxValue);
        this.optionalValue = clamp(optionalValue, 0f, this.maxValue);
        this.displayedCurrentValue = clamp(displayedCurrentValue, 0f, this.maxValue);
        this.displayedOptionalValue = clamp(displayedOptionalValue, 0f, this.maxValue);
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
    }

    public void setOptionalValue(float optionalValue) {
        this.optionalValue = currentValue + optionalValue;
    }

    private float animateTowards(float current, float target, float delta) {
        float alpha = 1f - (float) Math.exp(-ANIMATION_SPEED * delta);
        return Interpolation.smooth.apply(current, target, alpha);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
