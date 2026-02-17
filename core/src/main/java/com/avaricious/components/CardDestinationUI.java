package com.avaricious.components;

import com.avaricious.components.slot.SlotMachine;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CardDestinationUI {

    private final Dumpster dumpster = new Dumpster();

    private final TextureRegion yellowTexture = Assets.I().get(AssetKey.YELLOW_PIXEL);
    private final TextureRegion redTexture = Assets.I().get(AssetKey.DARK_RED_PIXEL);

    private float stateTime = 0f;

    private float windowHover = 0f;
    private float dumpsterHover = 0f;

    public void draw(SpriteBatch batch, float delta, Vector2 cardCenterPos, boolean cardIsDragging) {
        stateTime += delta;

        dumpster.update(delta, cardCenterPos, cardIsDragging);

        Rectangle window = SlotMachine.windowBounds;
        Rectangle dumpHit = dumpster.getHitBox();

        boolean windowIsHovered = window.contains(cardCenterPos);
        boolean dumpsterIsHovered = dumpHit.contains(cardCenterPos);

        windowHover = approach(windowHover, windowIsHovered ? 1f : 0f, 12f, delta);
        dumpsterHover = approach(dumpsterHover, dumpsterIsHovered ? 1f : 0f, 12f, delta);

        if (cardIsDragging) {
            drawGlowBorder(batch, yellowTexture, window, windowHover);
//            drawGlowBorder(batch, redTexture, dumpHit, dumpsterHover);
        }

        dumpster.draw(batch);
    }

    public boolean isOverDumpster(Vector2 cardCenterPos) {
        return dumpster.getHitBox().contains(cardCenterPos);
    }

    private void drawGlowBorder(SpriteBatch batch, TextureRegion textureRegion, Rectangle r, float hover) {
        // pulse 0..1
        float pulse = 0.5f + 0.5f * (float) Math.sin(stateTime * 8f);

        // hover is already smooth 0..1
        float thickness = 0.03f + 0.04f * hover;

        float baseAlpha = (0.65f + 0.25f * pulse) + 0.25f * hover;
        float glowAlpha = (0.18f + 0.12f * pulse) + 0.18f * hover;

        baseAlpha = Math.min(baseAlpha, 1f);
        glowAlpha = Math.min(glowAlpha, 1f);

        batch.setColor(1f, 1f, 1f, baseAlpha);
        drawRectOutline(batch, textureRegion, r, thickness);

        batch.setColor(1f, 1f, 1f, glowAlpha);
        drawRectOutline(batch, textureRegion, r, thickness * 2.5f);

        // Extra pass scales with hover (instead of boolean)
        if (hover > 0.001f) {
            float extraAlpha = (0.10f + 0.08f * pulse) * hover; // fade in/out smoothly
            batch.setColor(1f, 1f, 1f, extraAlpha);
            drawRectOutline(batch, textureRegion, r, thickness * (4.0f + 1.0f * hover));
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawRectOutline(SpriteBatch batch, TextureRegion px, Rectangle r, float t) {
        // bottom
        batch.draw(px, r.x - t, r.y - t, r.width + 2f * t, t);
        // top
        batch.draw(px, r.x - t, r.y + r.height, r.width + 2f * t, t);
        // left
        batch.draw(px, r.x - t, r.y, t, r.height);
        // right
        batch.draw(px, r.x + r.width, r.y, t, r.height);
    }

    private float approach(float current, float target, float speed, float delta) {
        float diff = target - current;
        float step = speed * delta;
        if (Math.abs(diff) <= step) return target;
        return current + Math.signum(diff) * step;
    }

    public Dumpster getDumpster() {
        return dumpster;
    }
}
