package com.avaricious.components;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import javax.swing.plaf.basic.BasicIconFactory;

/**
 * Draws an animated "light bulb" marquee border around a rectangle.
 * No Stage/Scene2D. Call update(dt) then draw(batch, x, y, w, h).
 */
public final class LightBulbBorder {

    private final float BULB_WIDTH = 97 / 250f;
    private final float BULB_HEIGHT = 91 / 250f;

    private final TextureRegion bulbOn = Assets.I().get(AssetKey.LIGHT_BULB_ON);
//    private final TextureRegion bulbOff = Assets.I().get(AssetKey.LIGHT_BULB_OFF); // may be null -> uses tint fallback
    private final TextureRegion bulbOff = null;

    // Layout
    private float gap = 1f;          // distance from target rect edge to bulb centers
    private float spacing = 0.25f;      // additional spacing between bulbs
    private float inset = 0f;        // inset bulbs towards the rect (optional)
    private boolean keepCornersClear = true; // avoids corner overlap

    // Animation
    private float stepTime = 0.2f;  // seconds per step
    private float acc = 0f;
    private int step = 0;

    // Pattern: [ON x onRun] then [OFF x offRun], repeating
    private int onRun = 3;
    private int offRun = 3;
    private boolean clockwise = true;

    // Color control
    private final Color onColor = new Color(1, 1, 1, 1);
    private final Color offTint = new Color(0.35f, 0.35f, 0.35f, 1f);

    // ---------- Configuration ----------
    public LightBulbBorder setGap(float gap) { this.gap = gap; return this; }
    public LightBulbBorder setSpacing(float spacing) { this.spacing = spacing; return this; }
    public LightBulbBorder setInset(float inset) { this.inset = inset; return this; }
    public LightBulbBorder setKeepCornersClear(boolean v) { this.keepCornersClear = v; return this; }

    public LightBulbBorder setStepTime(float secondsPerStep) {
        this.stepTime = Math.max(0.01f, secondsPerStep);
        return this;
    }

    public LightBulbBorder setPattern(int onRun, int offRun) {
        this.onRun = Math.max(1, onRun);
        this.offRun = Math.max(0, offRun);
        return this;
    }

    public LightBulbBorder setClockwise(boolean clockwise) { this.clockwise = clockwise; return this; }

    public LightBulbBorder setOnColor(Color c) {
        if (c != null) this.onColor.set(c);
        return this;
    }

    /** Used only when bulbOff is null (we tint bulbOn darker). */
    public LightBulbBorder setOffTint(Color c) {
        if (c != null) this.offTint.set(c);
        return this;
    }

    // ---------- Runtime ----------
    public void reset() { step = 0; acc = 0f; }

    public void update(float dt) {
        acc += dt;
        while (acc >= stepTime) {
            acc -= stepTime;
            step++;
        }
    }

    /**
     * Draw marquee border around the given rectangle (x,y,w,h).
     * (x,y) is bottom-left.
     */
    public void draw(float x, float y, float w, float h, float delta) {
        update(delta);

        final float bw = BULB_WIDTH;
        final float bh = BULB_HEIGHT;

        // Border rectangle where bulbs are placed (expanded by gap, optionally inset back)
        final float left   = x - gap + inset;
        final float right  = x + w + gap - inset;
        final float bottom = y - gap + inset;
        final float top    = y + h + gap - inset;

        // Step size along edges
        final float stepX = bw + spacing;
        final float stepY = bh + spacing;

        // How many bulbs per edge
        int nBottom = countAlong((right - left), stepX, keepCornersClear);
        int nTop    = nBottom;
        int nLeft   = countAlong((top - bottom), stepY, keepCornersClear);
        int nRight  = nLeft;

        // Total bulbs in the loop (start at bottom-left, go around)
        int total = nBottom + nRight + nTop + nLeft;
        if (total <= 0) return;

        // We index bulbs along the perimeter; marquee uses (index + step) % period
        int period = onRun + offRun;
        if (period <= 0) period = 1;

        int idx = 0;

        // Bottom edge: left -> right
        float startBX = left + cornerOffset(stepX);
        float yB = bottom - (bh * 0.5f);
        for (int i = 0; i < nBottom; i++, idx++) {
            float cx = startBX + i * stepX;
            drawBulb(cx - bw * 0.5f, yB, idx, period);
        }

        // Right edge: bottom -> top
        float xR = right - (bw * 0.5f);
        float startRY = bottom + cornerOffset(stepY);
        for (int i = 0; i < nRight; i++, idx++) {
            float cy = startRY + i * stepY;
            drawBulb(xR, cy - bh * 0.5f, idx, period);
        }

        // Top edge: right -> left
        float startTX = right - cornerOffset(stepX);
        float yT = top - (bh * 0.5f);
        for (int i = 0; i < nTop; i++, idx++) {
            float cx = startTX - i * stepX;
            drawBulb(cx - bw * 0.5f, yT, idx, period);
        }

        // Left edge: top -> bottom
        float xL = left - (bw * 0.5f);
        float startLY = top - cornerOffset(stepY);
        for (int i = 0; i < nLeft; i++, idx++) {
            float cy = startLY - i * stepY;
            drawBulb(xL, cy - bh * 0.5f, idx, period);
        }

    }

    // ---------- Internals ----------
    private int countAlong(float length, float step, boolean clearCorners) {
        if (length <= 0) return 0;
        float usable = length;
        if (clearCorners) usable = Math.max(0, usable - 2f * step); // reserve space near corners
        int n = (int)Math.floor(usable / step) + 1;
        return Math.max(0, n);
    }

    private float cornerOffset(float step) {
        return keepCornersClear ? step : 0f;
    }

    private void drawBulb(float x, float y, int index, int period) {
        int dirStep = clockwise ? step : -step;
        int phase = mod(index + dirStep, period);
        boolean on = phase < onRun;

        if (on) {
            Pencil.I().addDrawing(new TextureDrawing(
                bulbOn, new Rectangle(x, y, BULB_WIDTH, BULB_HEIGHT), 1, onColor
            ));
        } else {
            if (bulbOff != null) {
                Pencil.I().addDrawing(new TextureDrawing(
                    bulbOff, new Rectangle(x, y, BULB_WIDTH, BULB_HEIGHT), 1, onColor
                ));
            } else {
                Pencil.I().addDrawing(new TextureDrawing(
                    bulbOn, new Rectangle(x, y, BULB_WIDTH, BULB_HEIGHT), 1, offTint
                ));
            }
        }
    }

    private int mod(int a, int m) {
        int r = a % m;
        return r < 0 ? r + m : r;
    }
}
