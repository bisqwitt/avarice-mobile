package com.avaricious.components.displays;

import com.avaricious.RoundsManager;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ScoreDisplay {

    private final float DIGIT_WIDTH = 7 / 13f;
    private final float DIGIT_HEIGHT = 11 / 13f;

    private final float ROUND_TXT_X = 1f;
    private final float CURRENT_ROUND_X = ROUND_TXT_X + 3.25f; // 4.25f
    private final float COLON_X = CURRENT_ROUND_X + 0.5f; // 4.75
    private final float TARGET_SCORE_X = COLON_X + 1f; // 5.5

    private final DigitalNumber currentRound;
    private final DigitalNumber targetScore;

    private final TextureRegion roundTxt = Assets.I().get(AssetKey.ROUND_TXT);
    private final TextureRegion roundTxtShadow = Assets.I().get(AssetKey.ROUND_TXT_SHADOW);
    private final TextureRegion colon = Assets.I().get(AssetKey.COLON);
    private final TextureRegion slateTexture = Assets.I().get(AssetKey.SLATE_PIXEL);
    private final TextureRegion darkSlateTexture = Assets.I().get(AssetKey.DARK_SLATE_PIXEL);
    private final TextureRegion brightSlateTexture = Assets.I().get(AssetKey.BRIGHT_SLATE_PIXEL);

    public ScoreDisplay() {
        currentRound = new DigitalNumber(RoundsManager.I().getCurrentRound(), Assets.I().lightColor(),
            new Rectangle(CURRENT_ROUND_X, 16.4f, DIGIT_WIDTH, DIGIT_HEIGHT), 0.9f);
        targetScore = new DigitalNumber(RoundsManager.I().getCurrentTargetScore(), Assets.I().lightColor(),
            new Rectangle(TARGET_SCORE_X, 16.4f, DIGIT_WIDTH, DIGIT_HEIGHT), 0.75f);
    }

    public void draw(SpriteBatch batch, float delta) {
//        progressBar.draw(batch);
//        batch.draw(backgroundBox, 4.5f, 6.3f, 273 / 40f, 88 / 40f);

//        batch.draw(whiteTexture, 2.25f, 6.8f, 11.25f, 0.1f);
//        batch.draw(darkGreenTexture, 2.6f, 7.2f, 10.75f, 1.5f);

        Pencil.I().addDrawing(new TextureDrawing(slateTexture,
            new Rectangle(-3f, 14.5f, 15f, 6f),
            1));
        Pencil.I().addDrawing(new TextureDrawing(slateTexture,
            new Rectangle(-3f, -3f, 15f, 11.75f),
            1));
        Pencil.I().addDrawing(new TextureDrawing(darkSlateTexture,
            new Rectangle(-3f, 3f, 15f, 3.85f),
            1));

        Pencil.I().addDrawing(new TextureDrawing(brightSlateTexture,
            new Rectangle(-3f, 14.4f, 15f, 0.1f),
            1));
        Pencil.I().addDrawing(new TextureDrawing(brightSlateTexture,
            new Rectangle(-3f, 8.65f, 15f, 0.1f),
            1));
        Pencil.I().addDrawing(new TextureDrawing(brightSlateTexture,
            new Rectangle(-3f, 2.85f, 15f, 0.1f),
            1));
        Pencil.I().addDrawing(new TextureDrawing(brightSlateTexture,
            new Rectangle(-3f, 6.8f, 15f, 0.1f),
            1));

        Pencil.I().addDrawing(new TextureDrawing(roundTxtShadow,
            new Rectangle(ROUND_TXT_X, currentRound.calcHoverY() -0.1f, 37 / 13f, DIGIT_HEIGHT), 1, Assets.I().shadowColor()));
        Pencil.I().addDrawing(new TextureDrawing(roundTxt,
            new Rectangle(ROUND_TXT_X, currentRound.calcHoverY(), 37 / 13f, DIGIT_HEIGHT), 1));
        currentRound.draw(batch, delta);
        Pencil.I().addDrawing(new TextureDrawing(colon,
            new Rectangle(COLON_X, 16.4f, DIGIT_WIDTH, DIGIT_HEIGHT), 1));
        targetScore.draw(batch, delta);
    }

    public void addToScore(int amount) {
        AudioManager.I().startPayout();
        targetScore.setScore(Math.max(targetScore.getScore() - amount, 0));
    }

    public void resetScore() {
        targetScore.setScore(RoundsManager.I().getCurrentTargetScore());
        currentRound.setScore(RoundsManager.I().getCurrentRound());
    }

    public boolean targetScoreReached() {
        return targetScore.getScore() == 0;
    }

    public void setOnInternalScoreDisplayed(Runnable onInternalScoreDisplayed) {
        targetScore.setOnInternalScoreDisplayed(onInternalScoreDisplayed);
    }

}
