package com.avaricious.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

/**
 * Shader-based marquee "light bulb" border (no textures, no Stage).
 *
 * How it looks like "real light":
 * - Bulb BODY pass (normal alpha blending): glass + rim + spec highlight + hot core
 * - Bulb GLOW pass (additive blending): wide halo drawn on a larger quad
 *
 * Usage:
 *   LightBulbBorderShader border = new LightBulbBorderShader();
 *   ...
 *   border.update(Gdx.graphics.getDeltaTime());
 *   border.draw(camera.combined, x, y, w, h);
 *   ...
 *   border.dispose();
 */
public final class LightBulbBorderShader {

    // ---------- Shaders (embedded; no external files needed) ----------
    private static final String VERT = ""
        + "attribute vec4 a_position;\n"
        + "attribute vec2 a_texCoord0;\n"
        + "uniform mat4 u_projTrans;\n"
        + "varying vec2 v_texCoords;\n"
        + "void main(){\n"
        + "  v_texCoords = a_texCoord0;\n"
        + "  gl_Position = u_projTrans * a_position;\n"
        + "}\n";

    private static final String FRAG = ""
        + "#ifdef GL_ES\n"
        + "precision mediump float;\n"
        + "#endif\n"
        + "uniform vec2  u_resolution;\n"
        + "uniform float u_on;\n"
        + "uniform float u_time;\n"
        + "uniform float u_glowOnly;\n"
        + "uniform vec3  u_colorOn;\n"
        + "uniform vec3  u_colorOff;\n"
        + "varying vec2 v_texCoords;\n"
        + "\n"
        + "float hash21(vec2 p){\n"
        + "  p = fract(p*vec2(123.34, 456.21));\n"
        + "  p += dot(p, p+45.32);\n"
        + "  return fract(p.x*p.y);\n"
        + "}\n"
        + "\n"
        + "void main(){\n"
        + "  vec2 uv = v_texCoords - 0.5;\n"
        + "  uv.y *= 1.08; // slight squash for a nicer bulb feel\n"
        + "  float d = length(uv);\n"
        + "\n"
        + "  float rCore  = 0.20;\n"
        + "  float rGlass = 0.45;\n"
        + "  float rHalo  = 0.75;\n"
        + "\n"
        + "  float glass = smoothstep(rGlass, rGlass - 0.02, d);\n"
        + "  float rim   = smoothstep(rGlass, rGlass - 0.06, d) - glass;\n"
        + "  float core  = smoothstep(rCore,  rCore  - 0.06, d);\n"
        + "  float halo  = smoothstep(rHalo,  0.10, d);\n"
        + "\n"
        + "  vec2 hpos = uv - vec2(-0.14, 0.14);\n"
        + "  float h = exp(-dot(hpos,hpos) * 60.0);\n"
        + "\n"
        + "  float n = hash21(v_texCoords * 128.0 + u_time);\n"
        + "  float flicker = 1.0 + (n - 0.5) * 0.08;\n"
        + "\n"
        + "  float on = clamp(u_on, 0.0, 1.0);\n"
        + "  vec3 base = mix(u_colorOff, u_colorOn, on);\n"
        + "\n"
        + "  vec3 bodyColor =\n"
        + "      base * (0.35 * glass) +\n"
        + "      base * (0.20 * rim) +\n"
        + "      u_colorOn * (1.65 * core) * on * flicker +\n"
        + "      vec3(1.0) * (0.35 * h) * (0.3 + 0.7 * on);\n"
        + "  float bodyAlpha = clamp(glass + 0.40*rim + 0.25*h, 0.0, 1.0);\n"
        + "\n"
        + "  vec3 glowColor =\n"
        + "      u_colorOn * (1.45 * halo) * on * flicker +\n"
        + "      u_colorOn * (0.65 * core) * on;\n"
        + "  float glowAlpha = clamp(halo * on, 0.0, 1.0);\n"
        + "\n"
        + "  vec3 outColor = mix(bodyColor, glowColor, u_glowOnly);\n"
        + "  float outAlpha = mix(bodyAlpha, glowAlpha, u_glowOnly);\n"
        + "\n"
        + "  gl_FragColor = vec4(outColor, outAlpha);\n"
        + "}\n";

    // ---------- Rendering ----------
    private final ShaderProgram shader;
    private final Mesh quad;

    // Time
    private float timeSeconds = 0f;

    // Marquee animation
    private float stepTime = 0.08f;
    private float acc = 0f;
    private int step = 0;

    // Pattern: ON for onRun bulbs, then OFF for offRun bulbs
    private int onRun = 4;
    private int offRun = 4;
    private boolean clockwise = true;

    // Geometry
    private float bulbSize = 0.5f;        // body quad size
    private float glowScale = 1.85f;     // glow quad size multiplier
    private float gap = 1f;             // distance from target rect to bulb centers
    private float spacing = 1f;          // gap between bulbs (in addition to bulb size)
    private boolean keepCornersClear = true;

    // Colors (tune to match your references)
    private final Color onColor = new Color(1f, 0.85f, 0.25f, 1f);
    private final Color offColor = new Color(0.22f, 0.18f, 0.16f, 1f);

    // Reused arrays (avoid allocations)
    private final float[] verts = new float[4 * 5];
    private final short[] inds = new short[] {0, 1, 2, 2, 3, 0};

    public LightBulbBorderShader() {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(VERT, FRAG);
        if (!shader.isCompiled()) throw new IllegalStateException(shader.getLog());

        quad = new Mesh(true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );
        quad.setIndices(inds);
    }

    // ---------------- Public API ----------------

    public void update(float dt) {
        timeSeconds += dt;

        acc += dt;
        while (acc >= stepTime) {
            acc -= stepTime;
            step++;
        }
    }

    /**
     * Draw the border around target rectangle (x,y,w,h). x/y bottom-left.
     * Needs a projection matrix (camera.combined).
     */
    public void draw(Matrix4 projection, float x, float y, float w, float h) {
        Gdx.gl.glEnable(GL20.GL_BLEND);

        // Compute bulb loop rect (where bulb centers sit)
        float left   = x - gap;
        float right  = x + w + gap;
        float bottom = y - gap;
        float top    = y + h + gap;

        // 1) BODY PASS (normal alpha)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        beginShader(projection, /*glowOnly=*/0f, bulbSize);
        drawPerimeter(left, right, bottom, top, bulbSize);

        // 2) GLOW PASS (additive)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        float glowSize = bulbSize * glowScale;
        beginShader(projection, /*glowOnly=*/1f, glowSize);
        drawPerimeter(left, right, bottom, top, glowSize);
    }

    public void dispose() {
        quad.dispose();
        shader.dispose();
    }

    // ------------- Configuration (optional) -------------

    public LightBulbBorderShader setBulbSize(float px) { this.bulbSize = Math.max(2f, px); return this; }
    public LightBulbBorderShader setGlowScale(float s) { this.glowScale = Math.max(1.0f, s); return this; }
    public LightBulbBorderShader setGap(float px) { this.gap = px; return this; }
    public LightBulbBorderShader setSpacing(float px) { this.spacing = px; return this; }
    public LightBulbBorderShader setKeepCornersClear(boolean v) { this.keepCornersClear = v; return this; }

    public LightBulbBorderShader setStepTime(float secondsPerStep) {
        this.stepTime = Math.max(0.01f, secondsPerStep);
        return this;
    }

    public LightBulbBorderShader setPattern(int onRun, int offRun) {
        this.onRun = Math.max(1, onRun);
        this.offRun = Math.max(0, offRun);
        return this;
    }

    public LightBulbBorderShader setClockwise(boolean clockwise) { this.clockwise = clockwise; return this; }

    public LightBulbBorderShader setOnColor(Color c) { if (c != null) this.onColor.set(c); return this; }
    public LightBulbBorderShader setOffColor(Color c) { if (c != null) this.offColor.set(c); return this; }

    // ---------------- Internals ----------------

    private void beginShader(Matrix4 projection, float glowOnly, float currentQuadSize) {
        shader.bind();
        shader.setUniformMatrix("u_projTrans", projection);
        shader.setUniformf("u_time", timeSeconds);
        shader.setUniformf("u_glowOnly", glowOnly);

        shader.setUniformf("u_colorOn", onColor.r, onColor.g, onColor.b);
        shader.setUniformf("u_colorOff", offColor.r, offColor.g, offColor.b);

        // Not strictly required for this shader, but kept for future extensions
        shader.setUniformf("u_resolution", currentQuadSize, currentQuadSize);
    }

    private void drawPerimeter(float left, float right, float bottom, float top, float quadSize) {
        float stepSize = quadSize + spacing;

        int nBottom = countAlong((right - left), stepSize, keepCornersClear);
        int nRight  = countAlong((top - bottom), stepSize, keepCornersClear);
        int nTop    = nBottom;
        int nLeft   = nRight;

        int idx = 0;

        // Bottom: left -> right
        float startBX = left + cornerOffset(stepSize);
        float yB = bottom;
        for (int i = 0; i < nBottom; i++, idx++) {
            float cx = startBX + i * stepSize;
            drawBulb(cx, yB, quadSize, idx);
        }

        // Right: bottom -> top
        float xR = right;
        float startRY = bottom + cornerOffset(stepSize);
        for (int i = 0; i < nRight; i++, idx++) {
            float cy = startRY + i * stepSize;
            drawBulb(xR, cy, quadSize, idx);
        }

        // Top: right -> left
        float startTX = right - cornerOffset(stepSize);
        float yT = top;
        for (int i = 0; i < nTop; i++, idx++) {
            float cx = startTX - i * stepSize;
            drawBulb(cx, yT, quadSize, idx);
        }

        // Left: top -> bottom
        float xL = left;
        float startLY = top - cornerOffset(stepSize);
        for (int i = 0; i < nLeft; i++, idx++) {
            float cy = startLY - i * stepSize;
            drawBulb(xL, cy, quadSize, idx);
        }
    }

    private void drawBulb(float cx, float cy, float size, int index) {
        // Decide on/off by marquee pattern
        int period = onRun + offRun;
        if (period <= 0) period = 1;

        int dirStep = clockwise ? step : -step;
        int phase = mod(index + dirStep, period);
        boolean isOn = phase < onRun;

        shader.setUniformf("u_on", isOn ? 1f : 0f);

        float half = size * 0.5f;

        // Quad vertices: x,y,z,u,v (v_texCoords drives the radial math)
        // Bottom-left
        verts[0]  = cx - half; verts[1]  = cy - half; verts[2]  = 0f; verts[3]  = 0f; verts[4]  = 0f;
        // Bottom-right
        verts[5]  = cx + half; verts[6]  = cy - half; verts[7]  = 0f; verts[8]  = 1f; verts[9]  = 0f;
        // Top-right
        verts[10] = cx + half; verts[11] = cy + half; verts[12] = 0f; verts[13] = 1f; verts[14] = 1f;
        // Top-left
        verts[15] = cx - half; verts[16] = cy + half; verts[17] = 0f; verts[18] = 0f; verts[19] = 1f;

        quad.setVertices(verts);
        quad.render(shader, GL20.GL_TRIANGLES);
    }

    private int countAlong(float length, float step, boolean clearCorners) {
        if (length <= 0f) return 0;
        float usable = length;
        if (clearCorners) usable = Math.max(0f, usable - 2f * step);
        int n = (int)Math.floor(usable / step) + 1;
        return Math.max(0, n);
    }

    private float cornerOffset(float step) {
        return keepCornersClear ? step : 0f;
    }

    private int mod(int a, int m) {
        int r = a % m;
        return r < 0 ? r + m : r;
    }
}
