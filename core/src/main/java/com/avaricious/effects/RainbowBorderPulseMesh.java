package com.avaricious.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class RainbowBorderPulseMesh {

    private static RainbowBorderPulseMesh instance;

    public static RainbowBorderPulseMesh I() {
        return instance == null ? (instance = new RainbowBorderPulseMesh()) : instance;
    }

    // Pulse timing
    private float duration = 0.8f;
    private float t = 0f;
    private boolean active = false;

    // Thickness in PIXELS (screen space)
    public float baseThickness = 10f;
    public float pulseExtraThickness = 8f;

    // Optional: set >0 to make rainbow drift while pulsing
    public float phaseSpeed = 0f; // cycles per second

    // Mesh quality: higher = smoother rainbow around edges
    public int segmentsPerEdge = 96;

    private Mesh mesh;
    private ShaderProgram shader;
    private final Matrix4 proj = new Matrix4();

    // cached sizing to rebuild buffers when needed
    private int cachedSegments = -1;
    private int cachedW = -1, cachedH = -1;

    // working buffers
    private float[] vertices;   // x,y,r,g,b,a (6 floats per vertex)
    private short[] indices;

    private final Color tmp = new Color();

    private RainbowBorderPulseMesh() {
        // You can enable this if you want shader compilation logs:
        // ShaderProgram.pedantic = false;
    }

    public void triggerOnce() {
        t = 0f;
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void update(float delta) {
        if (!active) return;
        t += delta;
        if (t >= duration) {
            t = duration;
            active = false;
        }
    }

    /**
     * Call this while batch is drawing; it will end/begin safely.
     */
    public void render(SpriteBatch batch, float delta) {
        update(delta);
        if (!active) return;

        boolean wasDrawing = batch.isDrawing();
        if (wasDrawing) batch.end();

        renderInternal(delta);

        if (wasDrawing) batch.begin();
    }

    private void renderInternal(float delta) {
        final int W = Gdx.graphics.getWidth();
        final int H = Gdx.graphics.getHeight();

        ensureResources(W, H);

        float u = t / duration;
        float pulse = easeOutCubic(1f - Math.abs(2f * u - 1f)); // 0..1 peak in middle
        float alpha = 0.15f + 0.85f * pulse;
        float thickness = baseThickness + pulseExtraThickness * pulse;

        // Build vertices each frame because thickness & alpha change during pulse
        buildBorderRingVertices(W, H, thickness, alpha, (phaseSpeed * t) % 1f);

        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        // Screen-space ortho projection (pixel coords)
        proj.setToOrtho2D(0, 0, W, H);

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shader.bind();
        shader.setUniformMatrix("u_projTrans", proj);
        mesh.render(shader, GL20.GL_TRIANGLES);
    }

    private void ensureResources(int W, int H) {
        if (mesh == null || shader == null || cachedSegments != segmentsPerEdge || cachedW != W || cachedH != H) {
            dispose(); // clean old

            cachedSegments = segmentsPerEdge;
            cachedW = W;
            cachedH = H;

            // Total perimeter "points" (see build method):
            // points = 4*segmentsPerEdge + 1 (closing duplicate of start)
            int points = 4 * segmentsPerEdge + 1;
            int vertsCount = points * 2;            // outer+inner per point
            int floatsPerVert = 6;                 // x,y,r,g,b,a
            vertices = new float[vertsCount * floatsPerVert];

            // Triangles: for each segment between point i and i+1, two triangles = 6 indices
            int segments = points - 1;
            indices = new short[segments * 6];

            // Build indices once (topology constant)
            int idx = 0;
            for (int i = 0; i < segments; i++) {
                short o0 = (short) (2 * i);
                short i0 = (short) (2 * i + 1);
                short o1 = (short) (2 * (i + 1));
                short i1 = (short) (2 * (i + 1) + 1);

                // Triangle 1: o0, i0, o1
                indices[idx++] = o0;
                indices[idx++] = i0;
                indices[idx++] = o1;

                // Triangle 2: i0, i1, o1
                indices[idx++] = i0;
                indices[idx++] = i1;
                indices[idx++] = o1;
            }

            mesh = new Mesh(false, vertsCount, indices.length,
                new VertexAttribute(Usage.Position, 2, "a_position"),
                new VertexAttribute(Usage.ColorUnpacked, 4, "a_color"));

            shader = buildShader();
            if (!shader.isCompiled()) {
                throw new IllegalStateException("RainbowBorder shader compile error:\n" + shader.getLog());
            }
        }
    }

    private ShaderProgram buildShader() {
        final String vert =
            "attribute vec2 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "void main(){\n" +
                "  v_color = a_color;\n" +
                "  gl_Position = u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
                "}\n";

        final String frag =
            "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "void main(){\n" +
                "  gl_FragColor = v_color;\n" +
                "}\n";

        return new ShaderProgram(vert, frag);
    }

    /**
     * Builds a continuous perimeter ring:
     * - bottom:  i=0..S    (includes (0,0) and (W,0))
     * - right:   i=1..S    (excludes (W,0), includes (W,H))
     * - top:     i=1..S    (excludes (W,H), includes (0,H))
     * - left:    i=1..S-1  (excludes (0,H) and (0,0))
     * then duplicates start point to close strip.
     */
    private void buildBorderRingVertices(float W, float H, float thickness, float alpha, float phase) {
        float P = 2f * (W + H); // perimeter

        // Clamp thickness so it never eats whole screen (safety)
        thickness = Math.min(thickness, Math.min(W, H) * 0.49f);

        int S = segmentsPerEdge;

        int pointIndex = 0; // perimeter point index
        final int[] v = {0};          // float index in vertices array

        // Helper lambda-ish: writes two vertices (outer, inner) for a perimeter point
        // Using local method style in Java:
        class Writer {
            void write(float ox, float oy, float ix, float iy, float s) {
                float hue = (s / P + phase) % 1f;
                tmp.fromHsv(hue * 360f, 1f, 1f);
                tmp.a = alpha;

                // outer vertex
                vertices[v[0]++] = ox;
                vertices[v[0]++] = oy;
                vertices[v[0]++] = tmp.r;
                vertices[v[0]++] = tmp.g;
                vertices[v[0]++] = tmp.b;
                vertices[v[0]++] = tmp.a;

                // inner vertex
                vertices[v[0]++] = ix;
                vertices[v[0]++] = iy;
                vertices[v[0]++] = tmp.r;
                vertices[v[0]++] = tmp.g;
                vertices[v[0]++] = tmp.b;
                vertices[v[0]++] = tmp.a;
            }
        }
        Writer w = new Writer();

        // Bottom edge (0,0) -> (W,0)
        for (int i = 0; i <= S; i++) {
            float x = (W * i) / S;
            float y = 0f;

            float s = x; // along bottom
            w.write(x, y, x, y + thickness, s);
            pointIndex++;
        }

        // Right edge (W,0) -> (W,H), skip first corner
        for (int i = 1; i <= S; i++) {
            float x = W;
            float y = (H * i) / S;

            float s = W + y;
            w.write(x, y, x - thickness, y, s);
            pointIndex++;
        }

        // Top edge (W,H) -> (0,H), skip first corner
        for (int i = 1; i <= S; i++) {
            float x = W - (W * i) / S;
            float y = H;

            float s = W + H + (W - x);
            w.write(x, y, x, y - thickness, s);
            pointIndex++;
        }

        // Left edge (0,H) -> (0,0), skip both corners
        for (int i = 1; i <= S - 1; i++) {
            float x = 0f;
            float y = H - (H * i) / S;

            float s = 2f * W + H + (H - y);
            w.write(x, y, x + thickness, y, s);
            pointIndex++;
        }

        // Close the loop by duplicating the first point (0,0)
        {
            float x = 0f, y = 0f;
            float s = 0f;
            w.write(x, y, x, y + thickness, s);
            pointIndex++;
        }

        // At this point, we should have exactly (4*S + 1) points -> (4*S + 1)*2 vertices
        // If you want, you can assert in debug builds:
        // int expectedPoints = 4*S + 1;
        // if (pointIndex != expectedPoints) Gdx.app.log("Border", "points=" + pointIndex + " expected=" + expectedPoints);
    }

    private static float easeOutCubic(float x) {
        x = MathUtils.clamp(x, 0f, 1f);
        return 1f - (float) Math.pow(1f - x, 3);
    }

    public void dispose() {
        if (mesh != null) {
            mesh.dispose();
            mesh = null;
        }
        if (shader != null) {
            shader.dispose();
            shader = null;
        }
    }
}
