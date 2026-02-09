package com.avaricious;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class EffectManager {

    public static float streak = 0f;

    public static void create(TextureRegion texture, Rectangle bounds, TextureGlow.Type parentType, Color echoColor) {
        ParticleManager.I().create(bounds.x, bounds.y, ParticleType.RAINBOW, streak);
        TextureEcho.create(texture, bounds, echoColor, streak);
        TextureGlow.create(texture, bounds, parentType, streak);
    }

    public static void increaseStreak() {
        streak++;
    }

    public static void endStreak() {
        streak = 0f;
    }

}
