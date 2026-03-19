package com.avaricious.effects;

import com.badlogic.gdx.math.MathUtils;

public class IdleSwayEffect {
    private final float amplitudeDeg = 2.2f;
    private final float speed = 1f;
    private final float phase = MathUtils.random(0f, MathUtils.PI2);
    private final float noiseAmount = 0.15f;
    private final float noiseSpeed = 0.7f;

    private float time;
    private boolean enabled;

    public IdleSwayEffect() {
        this.time = 0f;
        this.enabled = true;
    }

    public void update(float delta) {
        if (!enabled) return;
        time += delta;
    }

    public float getRotation() {
        if (!enabled) return 0;

        float mainWave = MathUtils.sin(time * speed + phase);
        float secondaryWave = MathUtils.sin(time * noiseSpeed + phase * 1.731f);

        return mainWave * amplitudeDeg +
            secondaryWave * amplitudeDeg * noiseAmount;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
