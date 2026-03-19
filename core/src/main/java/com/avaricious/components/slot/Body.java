package com.avaricious.components.slot;

import com.avaricious.effects.IdleSwayEffect;
import com.avaricious.effects.PulseEffect;
import com.badlogic.gdx.math.Vector2;

public class Body {
    public float scale = 1f;
    public float targetScale = 1f;

    private float targetScaleSpeed = 10f;

    protected final Vector2 pos;

    private boolean isEmphasized = false;
    private int patternHitCount = 0;

    protected final PulseEffect pulseEffect = new PulseEffect();
    protected final IdleSwayEffect idleSwayEffect = new IdleSwayEffect();

    public Body(Vector2 pos) {
        this.pos = pos;
    }

    public void update(float delta) {
        pulseEffect.update(delta);
        idleSwayEffect.update(delta);

        scale += (targetScale - scale) * Math.min(1f, targetScaleSpeed * delta);
    }

    public void pulse() {
        pulseEffect.pulse();
    }

    public float getScale() {
        return scale * pulseEffect.getScale();
    }

    public float getRotation() {
        return pulseEffect.getRotation() + idleSwayEffect.getRotation();
    }

    public void beginPatternHit() {
        patternHitCount++;
        targetScale = 1.25f;
        setInPatternHit(true);
        setIdleSwayEffectEnabled(false);
    }

    public void endPatternHit() {
        patternHitCount = Math.max(0, patternHitCount - 1);
        if (patternHitCount == 0) {
            targetScale = 1f;
            setInPatternHit(false);
            setIdleSwayEffectEnabled(true);
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

    public Body setTargetScaleSpeed(float value) {
        this.targetScaleSpeed = value;
        return this;
    }

    public void setIdleSwayEffectEnabled(boolean enabled) {
        idleSwayEffect.setEnabled(enabled);
    }

}
