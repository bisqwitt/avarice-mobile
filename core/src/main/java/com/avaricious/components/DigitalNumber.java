package com.avaricious.components;

import com.avaricious.effects.IdleFloatEffect;
import com.avaricious.effects.IdleSwayEffect;
import com.avaricious.effects.PulseEffect;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class DigitalNumber {

    protected final List<TextureRegion> numberTextures = new ArrayList<>();
    protected final List<TextureRegion> numberShadowTextures = new ArrayList<>();

    protected final Color color;
    protected final Rectangle bounds;
    protected final float offset;

    private ZIndex zIndex = ZIndex.DIGITAL_NUMBER;

    private int score;

    private final PulseEffect pulseEffect = new PulseEffect();
    private final IdleFloatEffect floatEffect = new IdleFloatEffect();
    private final IdleSwayEffect swayEffect = new IdleSwayEffect(1.2f, 0.4f);

    public DigitalNumber(int initialScore, Color color, Rectangle bounds, float offset) {
        setScore(initialScore);
        this.color = color;
        this.bounds = bounds;
        this.offset = offset;

        updateDigitalNumbers(initialScore);
    }

    public DigitalNumber(int initialScore, Color color, int setLength, Rectangle bounds, float offset) {
        score = initialScore;
        this.color = color;
        this.bounds = bounds;
        this.offset = offset;

        for (int i = 0; i < setLength; i++) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
        updateDigitalNumbers(score);
    }

    public void draw(float delta) {
        floatEffect.update(delta);
        swayEffect.update(delta);
        pulseEffect.update(delta);

        float numberY = getNumberY();
        float scale = pulseEffect.getScale();
        float rotation = pulseEffect.getRotation() + swayEffect.getRotation();

        for (int i = 0; i < numberTextures.size(); i++) {
            Pencil.I().addDrawing(new TextureDrawing(
                numberShadowTextures.get(i),
                new Rectangle(bounds.x + (i * offset), numberY - 0.1f, bounds.width, bounds.height),
                scale, rotation, getLayer(), Assets.I().shadowColor()
            ));
            Pencil.I().addDrawing(new TextureDrawing(
                numberTextures.get(i),
                new Rectangle(bounds.x + (i * offset), numberY, bounds.width, bounds.height),
                scale, rotation, getLayer(), color
            ));
        }
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

        updateDigitalNumbers(score);
        pulseEffect.pulse();
    }

    public int getScore() {
        return score;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getNumberY() {
        return bounds.y + floatEffect.getYOffset();
    }

    public float getRotation() {
        return swayEffect.getRotation();
    }

    protected ZIndex getLayer() {
        return zIndex;
    }

    public void setZIndex(ZIndex zIndex) {
        this.zIndex = zIndex;
    }
}
