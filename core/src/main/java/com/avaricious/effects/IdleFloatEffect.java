package com.avaricious.effects;

import com.badlogic.gdx.math.MathUtils;

public class IdleFloatEffect {
    private final float amplitude;
    private final float speed;
    private final float phase = MathUtils.random(0f, MathUtils.PI2);
    private final float noiseAmount = 0.15f;
    private final float noiseSpeed = 0.7f;

    private float time;
    private boolean enabled;

    public IdleFloatEffect() {
        this(0.04f, 1f);
    }

    public IdleFloatEffect(float amplitude, float speed) {
        this.time = 0f;
        this.enabled = true;
        this.amplitude = amplitude;
        this.speed = speed;
    }

    public void update(float delta) {
        if (!enabled) return;
        time += delta;
    }

    public float getYOffset() {
        if (!enabled) return 0f;

        float mainWave = MathUtils.sin(time * speed + phase);
        float secondaryWave = MathUtils.sin(time * noiseSpeed + phase * 1.731f);

        return mainWave * amplitude +
            secondaryWave * amplitude * noiseAmount;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
