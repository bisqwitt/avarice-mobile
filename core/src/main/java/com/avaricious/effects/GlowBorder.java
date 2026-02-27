package com.avaricious.effects;

import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

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

        drawRectOutline(textureRegion, r, thickness, new Color(1f, 1f, 1f, baseAlpha));

        drawRectOutline(textureRegion, r, thickness * 2.5f, new Color(1f, 1f, 1f, glowAlpha));

        // Extra pass scales with hover (instead of boolean)
        if (hover > 0.001f) {
            float extraAlpha = (0.10f + 0.08f * pulse) * hover; // fade in/out smoothly
            drawRectOutline(textureRegion, r, thickness * (4.0f + hover), new Color(1f, 1f, 1f, extraAlpha));
        }
    }

    private static void drawRectOutline(TextureRegion px, Rectangle r, float t, Color color) {
        // bottom
        Pencil.I().addDrawing(new TextureDrawing(
            px, new Rectangle(r.x - t, r.y - t, r.width + 2f * t, t),
            11, color
        ));
        // top
        Pencil.I().addDrawing(new TextureDrawing(
            px, new Rectangle(r.x - t, r.y + r.height, r.width + 2f * t, t),
            11, color
        ));
        // left
        Pencil.I().addDrawing(new TextureDrawing(
            px, new Rectangle(r.x - t, r.y, t, r.height),
            11, color
        ));
        // right
        Pencil.I().addDrawing(new TextureDrawing(
            px, new Rectangle(r.x + r.width, r.y, t, r.height),
            11, color
        ));
    }
}
