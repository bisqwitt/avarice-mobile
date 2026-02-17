package com.avaricious.components.slot;

import com.avaricious.components.Dumpster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Draggable slot/card behavior (Balatro-like):
 * - smooth follow
 * - grip offset (no snapping)
 * - tilt based on velocity
 * - lerp back to origin on release
 * <p>
 * Rendering should use getRenderPos() instead of getPos().
 */
public class DragableSlot extends Slot {

    // --- Size for hit-testing (set to your card sprite size in world units) ---
    private float width;
    private float height;

    // --- Drag state ---
    private boolean dragging = false;
    private int draggingPointer = -1;

    // Offset from base position (Slot.pos) to where we actually render
    private final Vector2 offset = new Vector2();
    private final Vector2 targetOffset = new Vector2();

    // Cursor grip: (cursor - renderPos) at the moment we started dragging
    private final Vector2 grip = new Vector2();

    // Velocity for tilt
    private final Vector2 lastRenderPos = new Vector2();
    private final Vector2 vel = new Vector2();

    private final Vector2 lastMouse = new Vector2();
    private float dragVelX = 0f;


    // --- Feel knobs ---
    private float dragFollowSpeed = 28f;     // higher = snappier to cursor
    private float returnSpeed = 18f;         // higher = snappier return to origin
    private float maxTiltDeg = 12f;          // max drag tilt
    private float tiltResponsiveness = 10f;  // smoothing for tilt
    private float dragScaleMul = 1.10f;      // scale while dragging
    private float dropWobble = 1f;           // call wobble() on release if you like

    // Smoothed tilt
    private float tiltDeg = 0f;

    private boolean applying = false;
    private float applyTime = 0f;
    private float applyDuration = 0.35f;
    private float applyStartTilt = 0f;

    private float alpha = 1f;
    private float extraScaleMul = 1f;

    // Optional: callback on finish
    private Runnable onApplyFinished = null;

    // Temp to avoid allocations
    private final Vector2 tmp = new Vector2();
    private final Vector2 renderPos = new Vector2();

    public DragableSlot(Vector2 pos, float width, float height) {
        super(pos);
        this.width = width;
        this.height = height;

        // initialize lastRenderPos
        renderPos.set(super.getPos());
        lastRenderPos.set(renderPos);
    }

    // --- Public API ---

    /**
     * Call every frame.
     */
    public void update(float delta) {
        if (applying) {
            applyTime += delta;
            float t = Math.min(1f, applyTime / applyDuration);

            // Easing (Balatro-ish): fast start, smooth end
            float ease = 1f - (float) Math.pow(1f - t, 3); // cubic out

            // Shrink + fade out near the end
            extraScaleMul = 1f - 0.35f * ease;   // ends at 0.65
            alpha = 1f - ease;            // ends at 0

            // Reduce tilt to zero
            tiltDeg = applyStartTilt * (1f - ease);

            if (t >= 1f) {
                applying = false;
                alpha = 1f;
                extraScaleMul = 1f;
                offset.setZero();
                targetOffset.setZero();
                tiltDeg = 0f;

                if (onApplyFinished != null) {
                    Runnable r = onApplyFinished;
                    onApplyFinished = null;
                    r.run();
                }
            }
            return;
        }
        // Compute current render pos (base + offset)
        getRenderPos(renderPos);

        // Velocity (for tilt)
        vel.set(renderPos).sub(lastRenderPos).scl(delta > 0 ? (1f / delta) : 0f);
        lastRenderPos.set(renderPos);

        // Offset smoothing: follow cursor if dragging, otherwise return to zero
        float speed = dragging ? dragFollowSpeed : returnSpeed;
        offset.x += (targetOffset.x - offset.x) * Math.min(1f, speed * delta);
        offset.y += (targetOffset.y - offset.y) * Math.min(1f, speed * delta);

        if (dragging) {
            // tune 0.08..0.25 depending on your world scale
            float targetTilt = MathUtils.clamp(dragVelX * 0.25f, -maxTiltDeg, maxTiltDeg);
            tiltDeg += (targetTilt - tiltDeg) * Math.min(1f, tiltResponsiveness * delta);
        } else {
            // decay tilt back to 0 when not dragging
            tiltDeg += (0f - tiltDeg) * Math.min(1f, tiltResponsiveness * delta);
        }
    }

    /**
     * Hit test in WORLD coordinates.
     */
    public boolean hitTest(float worldX, float worldY) {
        getRenderPos(renderPos);
        float x = renderPos.x;
        float y = renderPos.y;

        // Assumes renderPos is bottom-left. If you treat pos as center, adjust accordingly.
        return worldX >= x && worldX <= x + width && worldY >= y && worldY <= y + height;
    }

    /**
     * Begin dragging if not already dragging. worldX/worldY are WORLD coords.
     */
    public boolean beginDrag(float worldX, float worldY, int pointer) {
        if (dragging) return false;
        dragging = true;
        draggingPointer = pointer;

        lastMouse.set(worldX, worldY);
        dragVelX = 0f;

        // Grip = cursor - currentRenderPos (so we keep relative grab point)
        getRenderPos(renderPos);
        grip.set(worldX - renderPos.x, worldY - renderPos.y);

        // Immediately set target so it feels responsive on pickup
        dragTo(worldX, worldY, pointer);
        return true;
    }

    /**
     * Continue dragging.
     */
    public void dragTo(float worldX, float worldY, int pointer) {
        if (!dragging || pointer != draggingPointer) return;

        // Desired render position = cursor - grip
        float desiredX = worldX - grip.x;
        float desiredY = worldY - grip.y;

        // basePos is Slot.getPos() (DO NOT mutate it)
        Vector2 base = super.getPos();

        // targetOffset = desiredRenderPos - basePos
        targetOffset.set(desiredX - base.x, desiredY - base.y);

        float dx = worldX - lastMouse.x;
        lastMouse.set(worldX, worldY);

        // dx is world-units per frame, convert to "per second"
        dragVelX = dx / Math.max(1e-6f, Gdx.graphics.getDeltaTime());

    }

    /**
     * End dragging; card lerps back (or you can add drop logic here).
     */
    public void endDrag(int pointer) {
        if (!dragging || pointer != draggingPointer) return;

        // Return to origin
        if (!SlotMachine.windowBounds.contains(getCardCenter()) && !Dumpster.hitBox.contains(getCardCenter())) {
            targetOffset.setZero();
            dragging = false;
            draggingPointer = -1;
        }

        if (dropWobble > 0f) {
            wobble();
        }
    }

    public void startApplyAnimation(float duration, Runnable onFinished) {
        this.applying = true;
        this.applyTime = 0f;
        this.applyDuration = Math.max(0.01f, duration);
        this.onApplyFinished = onFinished;

        this.applyStartTilt = this.tiltDeg;

        // Make it feel snappy
        this.dragging = false;
        this.draggingPointer = -1;
    }

    public boolean isApplying() {
        return applying;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getExtraScaleMul() {
        return extraScaleMul;
    }

    // --- Rendering helpers ---

    /**
     * Render position (base + offset) into out (WORLD coords).
     * Use this instead of getPos() when drawing.
     */
    public Vector2 getRenderPos(Vector2 out) {
        Vector2 base = super.getPos();
        return out.set(base.x + offset.x, base.y + offset.y);
    }

    /**
     * Additional rotation from drag. Combine with Slot.wobbleAngleDeg().
     */
    public float getDragTiltDeg() {
        return dragging ? tiltDeg : tiltDeg * 0.25f; // keep a tiny residual if you like
    }

    /**
     * Additional scale while dragging. Combine with Slot.pulseScale()/wobbleScale().
     */
    public float getDragScaleMul() {
        return dragging ? dragScaleMul : 1f;
    }

    public boolean isDragging() {
        return dragging;
    }

    public Vector2 getCardCenter() {
        return new Vector2(renderPos.x + width / 2, renderPos.y + height / 2);
    }

    // --- Tuning knobs (optional) ---

    public DragableSlot setFollow(float dragFollowSpeed, float returnSpeed) {
        this.dragFollowSpeed = dragFollowSpeed;
        this.returnSpeed = returnSpeed;
        return this;
    }

    public DragableSlot setTilt(float maxTiltDeg, float tiltResponsiveness) {
        this.maxTiltDeg = maxTiltDeg;
        this.tiltResponsiveness = tiltResponsiveness;
        return this;
    }

    public DragableSlot setDragScale(float dragScaleMul) {
        this.dragScaleMul = dragScaleMul;
        return this;
    }

    public DragableSlot setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public void pulse() {
    }
}
