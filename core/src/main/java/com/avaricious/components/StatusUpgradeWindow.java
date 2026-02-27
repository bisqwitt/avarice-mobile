package com.avaricious.components;

import com.avaricious.components.bars.StatusUpgradeBar;
import com.avaricious.components.bars.UpgradeBar;
import com.avaricious.stats.statupgrades.StatusRelic;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.List;

public class StatusUpgradeWindow {

    private final float WINDOW_X = 0.75f;
    private final float WINDOW_Y = 6f;

    private final TextureRegion window = Assets.I().get(AssetKey.STATUS_UPGRADE_WINDOW);
    private final TextureRegion shadow = Assets.I().get(AssetKey.STATUS_UPGRADE_WINDOW_SHADOW);
    private final TextureRegion levelUpTxt = new TextureRegion(Assets.I().get(AssetKey.LEVEL_UP_TEXT));

    private final Runnable onExit;

    private static final float PULSE_SPEED = 3.5f;
    private static final float PULSE_AMPLITUDE = 0.05f;
    private float levelUpAnimTime = 0f;
    private float levelUpTxtScale = 1;

    private final UpgradeBar upgradeBar = new StatusUpgradeBar(randomStatUpgrades(),
        new Rectangle(WINDOW_X + 1.5f, WINDOW_Y + 1.6f, 1.25f, 1.25f));

    private enum State {HIDDEN, ENTERING, SHOWN, EXITING}

    private State state = State.HIDDEN;

    private float animT = 0f;

    private static final float ENTER_DUR = 0.18f; // snappy
    private static final float EXIT_DUR = 0.14f;

    private float windowScale = 1f;
    private float windowAlpha = 1f;
    private float windowYOffset = 0f;  // small slide

    public StatusUpgradeWindow(Runnable onExit) {
        this.onExit = onExit;
        upgradeBar.setOnUpgradeClickedAndAnimationEnded(() -> {
            state = State.EXITING;
            animT = 0f;
        });
    }

    private List<Upgrade> randomStatUpgrades() {
        return Arrays.asList(StatusRelic.newRandom(), StatusRelic.newRandom(), StatusRelic.newRandom());
    }

    public void draw(SpriteBatch batch, float delta) {
        if (state == State.HIDDEN) return;

        // Update enter/exit animation
        if (state == State.ENTERING) {
            animT += delta;
            float p = clamp01(animT / ENTER_DUR);

            windowScale = lerp(0.85f, 1.00f, easeOutBack(p)); // pop + overshoot
            windowAlpha = lerp(0.00f, 1.00f, p);              // fade in
            windowYOffset = lerp(-0.25f, 0.00f, p);            // slide up a bit

            if (p >= 1f) {
                state = State.SHOWN;
                animT = 0f;
            }
        } else if (state == State.EXITING) {
            animT += delta;
            float p = clamp01(animT / EXIT_DUR);

            windowScale = lerp(1.00f, 0.92f, easeInQuad(p));  // slight shrink
            windowAlpha = lerp(1.00f, 0.00f, p);              // fade out
            windowYOffset = lerp(0.00f, -0.15f, p);            // slide down a bit

            if (p >= 1f) {
                state = State.HIDDEN;
                animT = 0f;
                onExit.run();
            }
        } else {
            windowScale = 1f;
            windowAlpha = 1f;
            windowYOffset = 0f;
        }

        // Existing pulse animation (only when visible)
        if (state == State.SHOWN) {
            levelUpAnimTime += delta;
            levelUpTxtScale = 1f + (float) Math.sin(levelUpAnimTime * PULSE_SPEED) * PULSE_AMPLITUDE;
        }

        // Apply alpha + scale + offset while drawing

        float wWin = 225f / 30f;
        float hWin = 163f / 30f;

        // Draw window centered scaling (Balatro-ish pop)
        float originX = wWin / 2f;
        float originY = hWin / 2f;

        Pencil.I().addDrawing(new TextureDrawing(window,
            new Rectangle(WINDOW_X, WINDOW_Y + windowYOffset, wWin, hWin),
            windowScale, 0f,
            15, new Color(1f, 1f, 1f, windowAlpha)
        ));

        // Draw upgrade bar and text; simplest is to just draw them with same alpha
        upgradeBar.draw(batch);

        float w = 55f / 15f;
        float h = 13f / 15f;
        Pencil.I().addDrawing(new TextureDrawing(
            levelUpTxt,
            new Rectangle(WINDOW_X + 1.8f, WINDOW_Y + 3.6f + windowYOffset, w, h),
            levelUpTxtScale * windowScale, 0f,
            15
        ));
    }


    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if (state != State.SHOWN) return;
        upgradeBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
    }

    public void show() {
        upgradeBar.loadUpgrades(randomStatUpgrades());
        state = State.ENTERING;
        animT = 0f;
        levelUpAnimTime = 0f;
    }

    private static float clamp01(float x) {
        return Math.max(0f, Math.min(1f, x));
    }

    // Overshoot "pop" (easeOutBack)
    private static float easeOutBack(float t) {
        t = clamp01(t);
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
    }

    private static float easeInQuad(float t) {
        t = clamp01(t);
        return t * t;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }


}
