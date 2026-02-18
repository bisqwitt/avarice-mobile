package com.avaricious.components.buttons;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DisablableButton extends Button {

    // "disabled" now means: not interactive
    private boolean disabled = true;

    // animation progress 0..1 (0 = hidden, 1 = shown)
    private float vis = 0f;
    private float visTarget = 0f;

    // tuning (world units)
    private float slideInOffset = 0.45f;     // how far below it comes from
    private float animSpeed = 10f;           // higher = faster
    private float overshootScale = 1.06f;    // Balatro-ish pop

    private final TextureRegion buttonShadow = Assets.I().get(AssetKey.BUTTON_SHADOW);

    public DisablableButton(Runnable onButtonPressedRunnable,
                            TextureRegion defaultButtonTexture,
                            TextureRegion pressedButtonTexture,
                            TextureRegion hoveredButtonTexture,
                            Rectangle buttonRectangle,
                            int key) {
        super(onButtonPressedRunnable, defaultButtonTexture, pressedButtonTexture, hoveredButtonTexture, buttonRectangle, key);
        setVisibleAnimated(false, true); // start hidden (instant)
    }

    /**
     * Call this instead of setDisabled(!visible).
     */
    public void setVisibleAnimated(boolean visible) {
        visTarget = visible ? 1f : 0f;
        disabled = !visible; // immediately block input while animating in/out
        if (visible) currentTexture = defaultButtonTexture;
    }

    /**
     * Optional: force state instantly (e.g., on screen show).
     */
    public void setVisibleAnimated(boolean visible, boolean instant) {
        visTarget = visible ? 1f : 0f;
        if (instant) vis = visTarget;
        disabled = !visible;
        if (visible) currentTexture = defaultButtonTexture;
    }

    public boolean isVisibleNow() {
        return vis > 0.001f;
    }

    /**
     * Update should be called once per frame before draw/input.
     */
    public void update(float delta) {
        // Smoothly move vis toward target
        float step = animSpeed * delta;
        if (vis < visTarget) vis = Math.min(visTarget, vis + step);
        else if (vis > visTarget) vis = Math.max(visTarget, vis - step);

        // When fully shown, enable input. When fully hidden, keep disabled.
        if (vis >= 0.999f) disabled = false;
        if (vis <= 0.001f) disabled = true;
    }

    @Override
    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        // No input unless fully shown (Balatro-style: commit at the end of the animation)
        if (disabled || vis < 0.999f) {
            wasHovered = false;
            return;
        }
        super.handleInput(mouse, pressed, wasPressed);
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        if (vis <= 0.001f) return;

        // Ease for nicer motion
        float t = Interpolation.pow3Out.apply(vis);

        // Slide from below
        float yOffset = (1f - t) * slideInOffset;

        // Slight pop/overshoot near the end
        float pop = (vis > 0.7f) ? (vis - 0.7f) / 0.3f : 0f; // 0..1
        float scale = 1f + (overshootScale - 1f) * Interpolation.swingOut.apply(pop);

        // Draw with transform around center
        Rectangle r = buttonRectangle;

        float cx = r.x + r.width * 0.5f;
        float cy = r.y + r.height * 0.5f;

        float w = r.width * scale;
        float h = r.height * scale;

        float drawX = cx - w * 0.5f;
        float drawY = (cy - h * 0.5f) - yOffset;

        // Optional: fade in (uncomment if you want)
        // batch.setColor(1f, 1f, 1f, t);

        drawWithShadow(batch, drawX, drawY, w, h);

        // batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawWithShadow(SpriteBatch batch, float x, float y, float w, float h) {
        if (showShadow) {
            batch.setColor(Assets.I().shadowColor());
            batch.draw(buttonShadow, x, y - 0.1f, w, h);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.draw(defaultButtonTexture, x, currentTexture == pressedButtonTexture ? y - 0.1f : y, w, h);
    }
}
