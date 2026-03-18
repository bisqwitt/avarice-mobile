package com.avaricious.components.slot;

import com.avaricious.effects.PulseEffect;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Slot {
    private float stateTime = 0f;

    public float scale = 1f;
    public float targetScale = 1f;

    private boolean wasSelected = false;
    private float pulseTime = 0f;
    private final float pulseDuration = 0.15f;
    private final float pulseAmp = 0.2f;
    private float targetScaleSpeed = 10f;

    // --- NEW: hover wobble state ---
    private boolean wasHovered = false;
    private float wobbleTime = 0f;
    private final float wobbleDuration = 0.25f;  // seconds
    private final float wobbleAmpDeg = 5f;       // peak rotation in degrees
    private final float wobbleScaleAmp = 0.03f;  // tiny elastic bump
    protected final Vector2 pos;

    private boolean isEmphasized = false;
    private int patternHitCount = 0;

    protected final PulseEffect pulseEffect = new PulseEffect();

    public Slot(Vector2 pos) {
        this.pos = pos;
    }

    public TextureRegion getFrame(Symbol symbol, boolean selected, float delta) {
        if (selected) stateTime += delta;
        return Assets.I().getSymbol(symbol);
    }

    public void tickScale(float delta) {
        scale += (targetScale - scale) * Math.min(1f, targetScaleSpeed * delta);
    }

    public void updatePulse(boolean isSelected, float delta) {
        pulseEffect.update(delta);
        // selection entry wobble
        if (isSelected && !wasSelected) {
            pulseTime = 0f;
            wobbleTime = 0f; // wobble on selection entry
        }

        // DESELECTION wobble
        if (!isSelected && wasSelected) {
            wobbleTime = 0f; // NEW: wobble when deselecting
        }

        if (pulseTime < pulseDuration) {
            pulseTime += delta;
        }
        wasSelected = isSelected;
    }

    public float pulseScale() {
        if (pulseTime >= pulseDuration) return 1f;
        float a = pulseTime / pulseDuration;       // 0..1
        float bump = (float) Math.sin(Math.PI * a); // 0..1..0
        return 1f + bump * pulseAmp;               // peaks at 1 + amp
    }

    public void updateHoverWobble(boolean restart, float delta) {
        if (restart && !wasHovered) {
            wobbleTime = 0f; // restart wobble on hover entry, regardless of selection
        }
        if (wobbleTime < wobbleDuration) {
            wobbleTime += delta;
        }
        wasHovered = restart;
    }

    public void wobble() {
        wobbleTime = 0f;
    }

    public void pulse() {
        pulseTime = 0f;
        pulseEffect.pulse();
    }

    public float getPulseEffectScale() {
        return pulseEffect.getScale();
    }

    public float getPulseEffectRotation() {
        return pulseEffect.getRotation();
    }

    /**
     * Current wobble rotation in degrees (damped sine).
     */
    public float wobbleAngleDeg() {
        if (wobbleTime >= wobbleDuration) return 0f;
        float t = wobbleTime / wobbleDuration;              // 0..1
        float decay = 1f - t;                               // linear decay
        float oscill = (float) Math.sin((float) (Math.PI * 2.5 * t)); // ~1¼ swings
        return wobbleAmpDeg * oscill * decay;
    }

    /**
     * Small elastic scale multiplier for wobble (optional, subtle).
     */
    public float wobbleScale() {
        if (wobbleTime >= wobbleDuration) return 1f;
        float t = wobbleTime / wobbleDuration;
        float decay = 1f - t;
        float oscill = (float) Math.sin((float) (Math.PI * 2.5 * t));
        return 1f + Math.abs(oscill) * decay * wobbleScaleAmp;
    }

    public void beginPatternHit() {
        patternHitCount++;
        targetScale = 1.25f;
        setInPatternHit(true);
    }

    public void endPatternHit() {
        patternHitCount = Math.max(0, patternHitCount - 1);
        if (patternHitCount == 0) {
            targetScale = 1f;
            setInPatternHit(false);
        }
    }

    public Vector2 getPos() {
        return pos;
    }

    public boolean isInPatternHit() {
        return isEmphasized;
    }

    public void setInPatternHit(boolean emphasized) {
        this.isEmphasized = emphasized;
    }

    public float getTargetScale() {
        return scale;
    }

    public Slot setTargetScaleSpeed(float value) {
        this.targetScaleSpeed = value;
        return this;
    }
}
