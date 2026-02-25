package com.avaricious.components;

import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class DigitalNumber {

    protected final List<TextureRegion> numberTextures = new ArrayList<>();
    protected final List<TextureRegion> numberShadowTextures = new ArrayList<>();

    protected final Color color;
    protected final Rectangle rectangle;
    protected final float offset;

    private int score;
    private int displayedScore;

    private boolean internalScoreIsDisplayed = true;

    private float hoverTime = 0f;

    private Runnable onInternalScoreDisplayed;

    public DigitalNumber(int initialScore, Color color, Rectangle rectangle, float offset) {
        setScore(initialScore);
        displayedScore = score;
        this.color = color;
        this.rectangle = rectangle;
        this.offset = offset;

        updateDigitalNumbers(initialScore);
    }

    public DigitalNumber(int initialScore, Color color, int setLength, Rectangle rectangle, float offset) {
        score = initialScore;
        displayedScore = initialScore;
        this.color = color;
        this.rectangle = rectangle;
        this.offset = offset;

        for (int i = 0; i < setLength; i++) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
        updateDigitalNumbers(score);
    }

    public void draw(SpriteBatch batch, float delta) {
        if (displayedScore < score) {
            long diff = score - displayedScore;
            displayedScore += (int) Math.ceil(diff * 0.025);
            updateDigitalNumbers(displayedScore);
        } else if (displayedScore > score) {
            long diff = displayedScore - score;
            displayedScore -= (int) Math.ceil(diff * 0.025);
            updateDigitalNumbers(displayedScore);
        } else if (!internalScoreIsDisplayed) {
            internalScoreIsDisplayed = true;
            if (onInternalScoreDisplayed != null) onInternalScoreDisplayed.run();
        }

        hoverTime += delta;
        float numberBaseY = calcHoverY();

        Pencil.I().drawInColor(batch, new Color(1f, 1f, 1f, 0.25f),
            () -> {
                for (int i = 0; i < numberTextures.size(); i++) {
                    batch.draw(numberShadowTextures.get(i),
                        rectangle.x + (i * offset), numberBaseY - 0.1f, rectangle.width, rectangle.height);
                }
            });
        Pencil.I().drawInColor(batch, color,
            () -> {
                batch.setColor(color);
                for (int i = 0; i < numberTextures.size(); i++) {
                    batch.draw(numberTextures.get(i), rectangle.x + (i * offset), numberBaseY, rectangle.width, rectangle.height);
                }
            });
    }

    private void updateDigitalNumbers(int score) {
        int tempScore = score;
        Assets assetManager = Assets.I();

        for (int i = numberTextures.size() - 1; i >= 0; i--) {
            numberTextures.set(i, assetManager.getDigitalNumber(tempScore % 10));
            numberShadowTextures.set(i, assetManager.getDigitalNumberShadow(tempScore % 10));
            tempScore /= 10;
        }
    }

    public void setScore(int score) {
        if (score < 0) score = 0;

        this.score = score;
        int digits = score == 0 ? 1 : (int) Math.log10(score) + 1;
        while (digits > numberTextures.size()) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
        internalScoreIsDisplayed = false;
    }

    public float calcHoverY() {
        float hoverSpeed = 1.5f;
        float hoverStrength = 0.03f;
        float hoverOffset = (float) Math.sin(hoverTime * hoverSpeed) * hoverStrength;
        return rectangle.y + hoverOffset;
    }

    public int getScore() {
        return score;
    }

    public void setOnInternalScoreDisplayed(Runnable onInternalScoreDisplayed) {
        this.onInternalScoreDisplayed = onInternalScoreDisplayed;
    }

    public Rectangle getBounds() {
        return rectangle;
    }

    private Vector2 getNumberCenter(float index) {
        return new Vector2((rectangle.x + index * offset) + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }
}
