package com.avaricious.components.popups;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class NumberPopup {

    public final static float defaultWidth = 7 / 15f;
    public final static float defaultHeight = 11 / 15f;

    private enum Phase {PULSE, HOLD, EXIT, FINISHED}

    protected final List<TextureRegion> digitalNumberTextures = new ArrayList<>();
    protected final List<TextureRegion> digitalNumberShadowTextures = new ArrayList<>();

    private final TextureRegion plusTexture = Assets.I().get(AssetKey.PLUS_SYMBOL);
    private final TextureRegion minusTexture = Assets.I().get(AssetKey.MINUS_SYMBOL);
    private final TextureRegion percentageTexture = Assets.I().get(AssetKey.PERCENTAGE_SYMBOL);

    private final int number;

    protected final Rectangle bounds;
    private final Color color;

    protected final float numberOffset;
    protected final float pulseTime = 0.20f; // pop+wobble duration
    protected final float holdTime = 0.40f; // OLD behavior: stay static for this long
    protected final float exitTime = 0.25f; // shrink until gone

    private final boolean manualHold;

    private Phase phase = Phase.PULSE;

    // One timer reused for the current phase (0..phaseDuration)
    private float timeInPhase = 0f;

    private final boolean asPercentage;

    private Runnable onFinished;

    public NumberPopup(int number, Color color, float x, float y, boolean asPercentage, boolean manualHold) {
        this(number, color, new Rectangle(x, y, defaultWidth, defaultHeight), asPercentage, manualHold);
    }

    public NumberPopup(int number, Color color, Rectangle bounds, boolean asPercentage, boolean manualHold) {
        this.number = number;
        this.color = color;
        this.asPercentage = asPercentage;
        this.manualHold = manualHold;

        this.bounds = new Rectangle(bounds);
        setDigitalNumberTextures(number);
        restart();

        float defaultOffset = 0.5f;
        numberOffset = bounds.width == defaultWidth && bounds.height == defaultHeight
            ? defaultOffset
            : defaultOffset + (bounds.width - defaultWidth) * 0.5f;
    }

    public void release() {
        if (manualHold && phase == Phase.HOLD) {
            phase = Phase.EXIT;
            timeInPhase = 0f;
        }
    }

    public boolean isManualHold() {
        return manualHold;
    }

    public void transform(int newValue) {
        setDigitalNumberTextures(newValue);
        restart();
    }

    private void restart() {
        phase = Phase.PULSE;
        timeInPhase = 0f;
    }

    public boolean isFinished() {
        return phase == Phase.FINISHED;
    }

    public boolean isHolding() {
        return phase == Phase.HOLD;
    }

    public void update(float delta) {
        if (phase == Phase.FINISHED) return;

        timeInPhase += delta;

        switch (phase) {
            case PULSE:
                if (timeInPhase >= pulseTime) {
                    phase = Phase.HOLD;
                    timeInPhase = 0f;
                }
                break;

            case HOLD:
                // Old behavior: auto-exit after holdTime
                if (!manualHold && timeInPhase >= holdTime) {
                    phase = Phase.EXIT;
                    timeInPhase = 0f;
                }
                break;
            case EXIT:
                if (timeInPhase >= exitTime) {
                    timeInPhase = exitTime;
                    phase = Phase.FINISHED;
                    if (onFinished != null) onFinished.run();
                }
                break;
            case FINISHED:
                break;
        }
    }

    public void render(SpriteBatch batch, float delta) {
        if (phase == Phase.FINISHED) return;

        float scale = getScale();
        float rotation = getRotation();
        float alpha = getAlpha();

        if (alpha <= 0f || scale <= 0f) return;

        float originX = bounds.width / 2f;
        float originY = bounds.height / 2f;

        // Use alpha for main draw color
        Pencil.I().drawInColor(batch, new Color(color.r, color.g, color.b, alpha),
            () -> batch.draw(
                number < 0 ? minusTexture : plusTexture,
                bounds.x - numberOffset, bounds.y,
                originX, originY,
                bounds.width, bounds.height,
                scale, scale,
                rotation
            ));

        for (int i = 0; i < digitalNumberTextures.size(); i++) {
            int index = i;
            Pencil.I().drawInColor(batch, new Color(color.r, color.g, color.b, alpha),
                () -> batch.draw(
                    digitalNumberTextures.get(index),
                    bounds.x + (numberOffset * index), bounds.y,
                    originX, originY,
                    bounds.width, bounds.height,
                    scale, scale,
                    rotation
                ));
        }

        if (asPercentage) {
            batch.draw(
                percentageTexture,
                bounds.x + 0.4f, bounds.y,
                originX, originY,
                8 / 20f, 13 / 20f,
                scale, scale,
                rotation
            );
        }
    }

    protected float getPulseCurve() {
        float t = clamp01(timeInPhase / pulseTime);
        float pulse = 1f - 4f * (t - 0.5f) * (t - 0.5f);
        return Math.max(0f, pulse);
    }

    protected float getScale() {
        float baseScale = 1.0f;

        if (phase == Phase.PULSE) {
            float pulseScale = 0.35f;
            return baseScale + getPulseCurve() * pulseScale;
        }

        if (phase == Phase.HOLD) {
            return baseScale;
        }

        // EXIT
        float t = clamp01(timeInPhase / exitTime);
        return baseScale * (1f - easeInQuad(t));
    }

    protected float getRotation() {
        if (phase == Phase.PULSE) {
            float wobbleAngle = 8f;
            return getPulseCurve() * wobbleAngle;
        }
        return 0f;
    }

    protected float getAlpha() {
        if (phase == Phase.PULSE || phase == Phase.HOLD) {
            return 1f;
        }

        // EXIT fade
        float t = clamp01(timeInPhase / exitTime);
        return 1f - t;
    }

    private float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private float easeInQuad(float t) {
        return t * t;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    private void setDigitalNumberTextures(int number) {
        digitalNumberTextures.clear();
        digitalNumberShadowTextures.clear(); // IMPORTANT: keep lists in sync

        for (char c : String.valueOf(number).toCharArray()) {
            int digit = c - '0';
            digitalNumberTextures.add(Assets.I().getDigitalNumber(digit));
//            digitalNumberShadowTextures.add(new TextureRegion(Assets.I().getDigitalNumberShadow(digit)));
        }
    }
}
