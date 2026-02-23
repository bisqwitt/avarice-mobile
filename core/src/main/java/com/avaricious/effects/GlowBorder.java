package com.avaricious.effects;

import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class GlowBorder {

//    List<GlowBorder, float>


    public static void create() {

    }

    public static void draw(SpriteBatch batch) {
//        drawGlowBorder(batch, Assets.I().);
    }

    private static float stateTime = 0f;


    public static void drawGlowBorder(SpriteBatch batch, TextureRegion textureRegion, Rectangle r, float hover, float delta) {
        stateTime += delta;

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
            drawRectOutline(batch, textureRegion, r, thickness * (4.0f + hover));
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private static void drawRectOutline(SpriteBatch batch, TextureRegion px, Rectangle r, float t) {
        // bottom
        batch.draw(px, r.x - t, r.y - t, r.width + 2f * t, t);
        // top
        batch.draw(px, r.x - t, r.y + r.height, r.width + 2f * t, t);
        // left
        batch.draw(px, r.x - t, r.y, t, r.height);
        // right
        batch.draw(px, r.x + r.width, r.y, t, r.height);
    }



}
