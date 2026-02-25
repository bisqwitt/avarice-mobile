package com.avaricious.effects;

public class Pulseable {

    private float pulseTime = 0f;
    private float scale = 1f;

    public void pulse() {
        pulseTime = 0f;
    }

    public void update(float delta) {
        float scale = 1f;
        float pulseDuration = 0.2f;

        if (pulseTime < pulseDuration) {
            pulseTime += delta;
            float t = pulseTime / pulseDuration;
            if (t > 1) t = 1f;

            float pulseCurve = 1f - 4f * (t - 0.5f) * (t - 0.5f);
            if (pulseCurve < 0f) pulseCurve = 0f;

            float baseScale = 1f;
            float pulseScale = 0.35f;
            scale = baseScale + pulseCurve * pulseScale;
        }
        this.scale = scale;
    }

    public float getPulseScale() {
        return scale;
    }

}
