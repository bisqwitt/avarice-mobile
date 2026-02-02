package com.avaricious.components.displays;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.RoundsManager;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.progressbar.ProgressBar;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ScoreDisplay {
    private final DigitalNumber digitalNumber;
    private final ProgressBar progressBar;

    private final TextureRegion darkGreenTexture = Assets.I().get(AssetKey.DARK_GREEN_PIXEL);
    private final TextureRegion greyTexture = Assets.I().get(AssetKey.GREY_PIXEL);

    public ScoreDisplay() {
        progressBar = new ProgressBar();
        progressBar.setMaxValue(RoundsManager.I().getCurrentTargetScore());
        digitalNumber = new DigitalNumber(0, Assets.I().lightColor(), 8,
            new Rectangle(1.5f, 14f, 0.32f * 1.8f, 0.56f * 1.8f), 0.8f);

        digitalNumber.setOnInternalScoreDisplayed(() -> AudioManager.I().endPayout());
    }

    public void draw(SpriteBatch batch, float delta) {
//        progressBar.draw(batch);
//        batch.draw(backgroundBox, 4.5f, 6.3f, 273 / 40f, 88 / 40f);

//        batch.draw(whiteTexture, 2.25f, 6.8f, 11.25f, 0.1f);
//        batch.draw(darkGreenTexture, 2.6f, 7.2f, 10.75f, 1.5f);

        batch.draw(greyTexture, 0f, 12.5f, 9f, 3f);
        batch.draw(greyTexture, 0f, 0f, 9f, 6f);
        digitalNumber.draw(batch, delta);
    }

    public void addToScore(int amount) {
        AudioManager.I().startPayout();
        digitalNumber.setScore(digitalNumber.getScore() + amount);
        progressBar.setDisplayedValue(digitalNumber.getScore());
    }

    public void resetScore() {
        digitalNumber.setScore(0);
    }

    public boolean targetScoreReached() {
        return digitalNumber.getScore() >= RoundsManager.I().getCurrentTargetScore();
    }

    public void nextRound() {
        resetScore();
        progressBar.setMaxValue(RoundsManager.I().getCurrentTargetScore());
    }

}
