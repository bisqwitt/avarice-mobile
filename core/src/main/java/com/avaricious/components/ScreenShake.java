package com.avaricious.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class ScreenShake {

    private static ScreenShake instance;

    public static ScreenShake I() {
        return instance == null ? instance = new ScreenShake() : instance;
    }

    private ScreenShake() {
    }

    private Camera camera;

    // Baseline
    private final Vector2 basePos = new Vector2();
    private final Vector3 baseUp = new Vector3();
    private boolean baseCaptured = false;

    // Trauma model (0..1)
    private float trauma = 0f;
    private float traumaDecayPerSecond = 1.4f;

    // Tuning
    private float tune = 9f;
    private float maxOffsetPixels = 14f / tune;
    private float maxRollDeg = 1.8f / tune;
    private float frequencyHz = 18f / tune;

    // Time & seeds
    private float time = 0f;
    private float seedX = MathUtils.random(0f, 9999f);
    private float seedY = MathUtils.random(0f, 9999f);
    private float seedR = MathUtils.random(0f, 9999f);

    // Track current roll so we can undo cleanly
    private float currentRollDeg = 0f;

    public ScreenShake setCamera(Camera camera) {
        this.camera = camera;
        this.baseCaptured = false; // recapture baseline next update
        this.currentRollDeg = 0f;
        return this;
    }

    public void addTrauma(float amount) {
        trauma = MathUtils.clamp(trauma + amount, 0f, 1f);
    }

    /**
     * Call after resize or whenever you "snap" the camera to a new base.
     */
    public void captureBaseNow() {
        if (camera == null) return;
        basePos.set(camera.position.x, camera.position.y);
        baseUp.set(camera.up);
        baseCaptured = true;
    }

    public void update(float delta) {
        if (camera == null) return;

        if (!baseCaptured) {
            captureBaseNow();
        }

        if (trauma <= 0.0001f) {
            restore();
            return;
        }

        time += delta;
        trauma = MathUtils.clamp(trauma - traumaDecayPerSecond * delta, 0f, 1f);

        float shake = trauma * trauma; // nonlinear = nicer

        float w = MathUtils.PI2 * frequencyHz;

        float nx = smoothNoise(seedX + time * 1.7f);
        float ny = smoothNoise(seedY + time * 1.9f);
        float nr = smoothNoise(seedR + time * 1.3f);

        float oscX = MathUtils.sin(w * time) * 0.55f + (nx - 0.5f) * 0.9f;
        float oscY = MathUtils.sin(w * time + 1.7f) * 0.55f + (ny - 0.5f) * 0.9f;

        float offsetX = oscX * (maxOffsetPixels * shake);
        float offsetY = oscY * (maxOffsetPixels * shake);

        // Translation
        camera.position.set(basePos.x + offsetX, basePos.y + offsetY, camera.position.z);

        // Roll (rotate UP around the camera's forward axis)
        float targetRoll = (nr - 0.5f) * 2f * (maxRollDeg * shake);
        applyRoll(targetRoll);
    }

    public boolean isShaking() {
        return trauma > 0.0001f;
    }

    public ScreenShake setMaxOffsetPixels(float px) {
        this.maxOffsetPixels = px;
        return this;
    }

    public ScreenShake setMaxRollDeg(float deg) {
        this.maxRollDeg = deg;
        return this;
    }

    public ScreenShake setFrequencyHz(float hz) {
        this.frequencyHz = hz;
        return this;
    }

    public ScreenShake setTraumaDecay(float perSec) {
        this.traumaDecayPerSecond = perSec;
        return this;
    }

    private void restore() {
        // Restore orientation and position
        camera.up.set(baseUp);
        currentRollDeg = 0f;
        camera.position.set(basePos.x, basePos.y, camera.position.z);
    }

    private void applyRoll(float targetRollDeg) {
        // Reset to base up, then apply target roll (prevents drift)
        camera.up.set(baseUp);

        if (Math.abs(targetRollDeg) > 0.0001f) {
            // Rotate UP vector around the forward axis (direction)
            camera.up.rotate(camera.direction, targetRollDeg);
        }
        currentRollDeg = targetRollDeg;
    }

    // --- smooth noise helpers ---

    private float smoothNoise(float x) {
        int x0 = (int) Math.floor(x);
        int x1 = x0 + 1;

        float t = x - x0;
        t = t * t * (3f - 2f * t); // smoothstep

        float v0 = hashTo01(x0);
        float v1 = hashTo01(x1);

        return MathUtils.lerp(v0, v1, t);
    }

    private float hashTo01(int n) {
        n = (n << 13) ^ n;
        int nn = (n * (n * n * 15731 + 789221) + 1376312589);
        nn &= 0x7fffffff;
        return nn / 2147483647f;
    }
}
