package com.avaricious.components.displays;

import com.avaricious.RoundsManager;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
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
            new Rectangle(1.5f, 16f, 0.32f * 1.8f, 0.56f * 1.8f), 0.8f);
    }

    public void draw(SpriteBatch batch, float delta) {
//        progressBar.draw(batch);
//        batch.draw(backgroundBox, 4.5f, 6.3f, 273 / 40f, 88 / 40f);

//        batch.draw(whiteTexture, 2.25f, 6.8f, 11.25f, 0.1f);
//        batch.draw(darkGreenTexture, 2.6f, 7.2f, 10.75f, 1.5f);

        batch.draw(slateTexture, 0f, 14.5f, 9f, 3f);
        batch.draw(slateTexture, 0f, 0f, 9f, 8f);
        batch.draw(darkSlateTexture, 0f, 2.25f, 9f, 3.9f);

        batch.draw(brightSlateTexture, 0f, 14.4f, 9f, 0.1f);
        batch.draw(brightSlateTexture, 0f, 7.9f, 9f, 0.1f);
        batch.draw(brightSlateTexture, 0f, 2.25f, 9f, 0.1f);
        batch.draw(brightSlateTexture, 0f, 2.25f + 3.9f - 0.1f, 9f, 0.1f);
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
