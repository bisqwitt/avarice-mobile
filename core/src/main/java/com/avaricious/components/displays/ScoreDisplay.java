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
    private final DigitalNumber digitalNumber;

    private final TextureRegion slateTexture = Assets.I().get(AssetKey.SLATE_PIXEL);
    private final TextureRegion darkSlateTexture = Assets.I().get(AssetKey.DARK_SLATE_PIXEL);
    private final TextureRegion brightSlateTexture = Assets.I().get(AssetKey.BRIGHT_SLATE_PIXEL);

    public ScoreDisplay() {
        digitalNumber = new DigitalNumber(RoundsManager.I().getCurrentTargetScore(), Assets.I().lightColor(), 8,
            new Rectangle(1f, 16.4f, 8 / 13f, 14 / 13f), 0.9f);
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
        digitalNumber.draw(batch, delta);
    }

    public void addToScore(int amount) {
        AudioManager.I().startPayout();
        digitalNumber.setScore(Math.max(digitalNumber.getScore() - amount, 0));
    }

    public void resetScore() {
        digitalNumber.setScore(RoundsManager.I().getCurrentTargetScore());
    }

    public boolean targetScoreReached() {
        return digitalNumber.getScore() == 0;
    }

    public void setOnInternalScoreDisplayed(Runnable onInternalScoreDisplayed) {
        digitalNumber.setOnInternalScoreDisplayed(onInternalScoreDisplayed);
    }

}
