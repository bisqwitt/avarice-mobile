package com.avaricious.components.popups;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class NumberPopup {

    public final static float defaultWidth = 7 / 12f;
    public final static float defaultHeight = 11 / 12f;

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

        float defaultOffset = 0.6f;
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

        float yOffset = getPulseYOffset();

        // Use alpha for main draw color
        Pencil.I().addDrawing(new TextureDrawing(
            number < 0 ? minusTexture : plusTexture,
            new Rectangle(bounds.x - numberOffset, bounds.y + yOffset, bounds.width, bounds.height),
            scale, rotation, 16, new Color(color.r, color.g, color.b, alpha)
        ));

        for (int i = 0; i < digitalNumberTextures.size(); i++) {
            int index = i;
            Pencil.I().addDrawing(new TextureDrawing(
                digitalNumberTextures.get(index),
                new Rectangle(bounds.x + (numberOffset * index), bounds.y + yOffset, bounds.width, bounds.height),
                scale, rotation, 16, new Color(color.r, color.g, color.b, alpha)
            ));
        }

        if (asPercentage) {
            Pencil.I().addDrawing(new TextureDrawing(
                percentageTexture,
                new Rectangle(bounds.x + 0.4f, bounds.y + yOffset, 8 / 20f, 13 / 20f),
                scale, rotation, 16
            ));
        }
    }

    protected float getPulseCurve() {
        float t = clamp01(timeInPhase / pulseTime);

        // Main pop: quick overshoot then settle
        float pop = easeOutBack(t);

        // Secondary tiny bounce near the end (damped sine)
        float bounceWindow = smoothstep(clamp01((t - 0.55f) / 0.45f)); // activates late
        float bounce = (float) Math.sin((t - 0.55f) * Math.PI * 3.0) * 0.08f * bounceWindow;

        // Clamp-ish: keep it sane
        return pop + bounce;
    }

    protected float getScale() {
        float baseScale = 1.0f;

        if (phase == Phase.PULSE) {
            float t = clamp01(timeInPhase / pulseTime);

            // Overshoot amount (tune)
            float overshoot = 0.28f;

            // getPulseCurve() is ~1 at rest end, >1 at overshoot
            // We want scale to start at ~0.85 and end at 1.0 with overshoot.
            float startScale = 0.85f;
            float pop = getPulseCurve(); // around 0..~1.1

            // Normalize-ish: map pop so it starts near 0 and ends near 1
            // We'll use smoothstep for a stable end.
            float settle = smoothstep(t);

            // pop gives overshoot, settle ensures it lands exactly at 1
            float scale = startScale + (1f - startScale) * settle;
            scale += (pop - settle) * overshoot;

            return scale;
        }

        if (phase == Phase.HOLD) {
            return baseScale;
        }

        // EXIT
        float t = clamp01(timeInPhase / exitTime);
        return baseScale * (1f - easeInQuad(t));
    }

    protected float getRotation() {
        if (phase != Phase.PULSE) return 0f;

        float t = clamp01(timeInPhase / pulseTime);

        // Damped oscillation: starts strong, decays quickly
        float maxAngle = 7.0f;     // tune
        float damping = 10.0f;     // tune
        float freq = 14.0f;        // tune (radians-ish)

        float osc = (float) Math.sin(t * freq) * (float) Math.exp(-damping * t);

        // Optional: flip direction based on sign so plus/minus feel distinct
        float dir = (number < 0) ? -1f : 1f;

        return dir * maxAngle * osc;
    }

    protected float getPulseYOffset() {
        if (phase != Phase.PULSE) return 0f;
        float t = clamp01(timeInPhase / pulseTime);

        // Up-kick early, settle back
        float kick = (float) Math.sin(Math.PI * t) * 0.06f; // tune (world units)
        return kick;
    }

    protected float getAlpha() {
        if (phase == Phase.PULSE || phase == Phase.HOLD) {
            return 1f;
        }

        // EXIT fade
        float t = clamp01(timeInPhase / exitTime);
        return 1f - t;
    }

    private float easeOutBack(float t) {
        // Standard "back" overshoot ease
        float s = 1.70158f;
        t -= 1f;
        return 1f + (s + 1f) * t * t * t + s * t * t;
    }

    private float smoothstep(float t) {
        // Nice smooth 0..1
        return t * t * (3f - 2f * t);
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
