package com.avaricious.components;

import com.avaricious.components.slot.SlotMachine;
import com.avaricious.effects.GlowBorder;
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

    private float windowHover = 0f;
    private float dumpsterHover = 0f;

    public void draw(SpriteBatch batch, float delta, Vector2 cardCenterPos, boolean cardIsDragging) {

        dumpster.update(delta, cardCenterPos, cardIsDragging);

        Rectangle window = SlotMachine.windowBounds;
        Rectangle dumpHit = dumpster.getHitBox();

        boolean windowIsHovered = window.contains(cardCenterPos);
        boolean dumpsterIsHovered = dumpHit.contains(cardCenterPos);

        windowHover = approach(windowHover, windowIsHovered ? 1f : 0f, 12f, delta);
        dumpsterHover = approach(dumpsterHover, dumpsterIsHovered ? 1f : 0f, 12f, delta);

        if (cardIsDragging) {
            GlowBorder.drawGlowBorder(batch, yellowTexture, window, windowHover, delta);
//            drawGlowBorder(batch, redTexture, dumpHit, dumpsterHover);
        }

        dumpster.draw(batch);
    }

    public boolean isOverDumpster(Vector2 cardCenterPos) {
        return dumpster.getHitBox().contains(cardCenterPos);
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
