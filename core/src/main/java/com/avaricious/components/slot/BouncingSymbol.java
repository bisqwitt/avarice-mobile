package com.avaricious.components.slot;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.effects.PulseEffect;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.effects.particle.ParticleType;
import com.avaricious.utility.Assets;
import com.avaricious.utility.GameContext;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class BouncingSymbol {

    private final TextureRegion texture;
    private final TextureRegion whiteTexture;

    private final PulseEffect pulseEffect = new PulseEffect();

    private float x;
    private float y;

    private float velocityX;
    private float velocityY;

    private float rotation;
    private float rotationVelocity;

    private float scale = 1f;

    private float lifeTime = 0f;
    private boolean finished = false;

    /*
     * Physics
     */
    private static final float AIR_DRAG = 0.998f;
    private static final float ROTATION_DRAG = 0.997f;

    private static final float MIN_BOUNCE = 0.85f;
    private static final float MAX_BOUNCE = 0.97f;

    private static final float MAX_LIFETIME = 8f;
    private static final float FADE_DURATION = 0.5f;

    /*
     * Organic movement
     */
    private final float wobbleSpeed;
    private final float wobbleStrength;

    private boolean claimed = false;

    public BouncingSymbol(Symbol symbol, float x, float y) {
        this.texture = Assets.I().getSymbol(symbol);
        this.whiteTexture = Assets.I().get(symbol.whiteKey());

        this.x = x;
        this.y = y;

        /*
         * Launch in a random direction.
         */
        float angle = MathUtils.random(0f, MathUtils.PI2);
        float speed = MathUtils.random(6f, 12f);

        velocityX = MathUtils.cos(angle) * speed;
        velocityY = MathUtils.sin(angle) * speed;

        /*
         * Initial rotation.
         */
        rotation = MathUtils.random(-10f, 10f);
        rotationVelocity = MathUtils.random(-180f, 180f);

        /*
         * Slightly different wobble for every symbol.
         */
        wobbleSpeed = MathUtils.random(7f, 12f);
        wobbleStrength = MathUtils.random(4f, 10f);

        /*
         * Falling symbol pulse settings.
         */
        pulseEffect.setStrength(3.25f);
        pulseEffect.setSpeed(0.125f);
    }

    public void update(float delta) {
        lifeTime += delta;

        updatePhysics(delta);
        pulseEffect.update(delta);

        handleHorizontalCollisions();
        handleVerticalCollisions();

        updateScale();

        if (lifeTime >= MAX_LIFETIME) {
            finished = true;
        }
    }

    private void updatePhysics(float delta) {
        float movementDrag =
            (float) Math.pow(AIR_DRAG, delta * 60f);

        velocityX *= movementDrag;
        velocityY *= movementDrag;

        rotationVelocity *=
            (float) Math.pow(ROTATION_DRAG, delta * 60f);

        /*
         * Movement
         */
        x += velocityX * delta;
        y += velocityY * delta;

        /*
         * Rotation
         */
        rotation += rotationVelocity * delta;

        /*
         * Small organic wobble.
         */
        rotation +=
            MathUtils.sin(lifeTime * wobbleSpeed)
                * wobbleStrength
                * delta;
    }

    public boolean handleInput(Vector2 mouse, boolean touching, boolean wasTouching) {
        if (!contains(mouse) || claimed) {
            return false;
        }

        pulseEffect.pulse();
        lifeTime = MAX_LIFETIME - FADE_DURATION;

        ParticleManager.I().create(x, y, ParticleType.WHITE, 0.02f, 50f, ZIndex.SLOT_MACHINE);
        PopupManager.I().spawnNumber(1, Assets.I().blue(),
            x, y, false);
        ScoreDisplay.I().addToScore(1);

        claimed = true;

        return true;
    }

    public boolean contains(Vector2 mouse) {
        float mouseX = mouse.x;
        float mouseY = mouse.y;
        /*
         * Deliberately ignore PulseEffect scale here.
         * The hitbox therefore doesn't grow when pulsing.
         */
        float width = SlotMachine.CELL_W * scale;
        float height = SlotMachine.CELL_H * scale;

        float drawX =
            x - (width - SlotMachine.CELL_W) / 2f;

        float drawY =
            y - (height - SlotMachine.CELL_H) / 2f;

        return mouseX >= drawX
            && mouseX <= drawX + width
            && mouseY >= drawY
            && mouseY <= drawY + height;
    }

    private void handleHorizontalCollisions() {
        float width = getWidth();

        float screenLeft =
            GameContext.I().viewport.getCamera().position.x
                - GameContext.I().viewport.getWorldWidth() / 2f;

        float screenRight =
            GameContext.I().viewport.getCamera().position.x
                + GameContext.I().viewport.getWorldWidth() / 2f;

        float drawX =
            x - (width - SlotMachine.CELL_W) / 2f;

        float left = drawX;
        float right = drawX + width;

        /*
         * LEFT WALL
         */
        if (left < screenLeft) {
            float overlap = screenLeft - left;

            x += overlap;

            velocityX =
                Math.abs(velocityX)
                    * randomBounce();

            rotationVelocity +=
                MathUtils.random(-90f, 90f);
        }

        /*
         * RIGHT WALL
         */
        if (right > screenRight) {
            float overlap = right - screenRight;

            x -= overlap;

            velocityX =
                -Math.abs(velocityX)
                    * randomBounce();

            rotationVelocity +=
                MathUtils.random(-90f, 90f);
        }
    }

    private void handleVerticalCollisions() {
        float height = getHeight();

        float screenBottom =
            GameContext.I().viewport.getCamera().position.y
                - GameContext.I().viewport.getWorldHeight() / 2f;

        float screenTop =
            GameContext.I().viewport.getCamera().position.y
                + GameContext.I().viewport.getWorldHeight() / 2f;

        float drawY =
            y - (height - SlotMachine.CELL_H) / 2f;

        float bottom = drawY;
        float top = drawY + height;

        /*
         * BOTTOM WALL
         */
        if (bottom < screenBottom) {
            float overlap = screenBottom - bottom;

            y += overlap;

            velocityY =
                Math.abs(velocityY)
                    * randomBounce();

            rotationVelocity +=
                MathUtils.random(-90f, 90f);
        }

        /*
         * TOP WALL
         */
        if (top > screenTop) {
            float overlap = top - screenTop;

            y -= overlap;

            velocityY =
                -Math.abs(velocityY)
                    * randomBounce();

            rotationVelocity +=
                MathUtils.random(-90f, 90f);
        }
    }

    private void updateScale() {
        /*
         * Small spawn pop.
         */
        if (lifeTime < 0.12f) {
            float progress =
                lifeTime / 0.12f;

            scale =
                MathUtils.lerp(
                    1.15f,
                    1f,
                    progress
                );

            return;
        }

        /*
         * Normal size for most of the lifetime.
         */
        if (lifeTime < MAX_LIFETIME - FADE_DURATION) {
            scale = 1f;
            return;
        }

        /*
         * Shrink before disappearing.
         */
        float fadeProgress =
            (lifeTime - (MAX_LIFETIME - FADE_DURATION))
                / FADE_DURATION;

        scale =
            MathUtils.lerp(
                1f,
                0f,
                fadeProgress
            );
    }

    private float randomBounce() {
        return MathUtils.random(
            MIN_BOUNCE,
            MAX_BOUNCE
        );
    }

    private float getWidth() {
        return SlotMachine.CELL_W
            * 0.75f
            * scale
            * pulseEffect.getScale();
    }

    private float getHeight() {
        return SlotMachine.CELL_H
            * 0.75f
            * scale
            * pulseEffect.getScale();
    }

    public void draw() {
        float width = getWidth();
        float height = getHeight();

        float centerX = x + SlotMachine.CELL_W / 2f;
        float centerY = y + SlotMachine.CELL_H / 2f;

        float drawX = centerX - width / 2f;
        float drawY = centerY - height / 2f;

        float glowScale = 3f;

        float glowWidth = width * glowScale;
        float glowHeight = height * glowScale;

        float glowX = centerX - glowWidth / 2f;
        float glowY = centerY - glowHeight / 2f;

        float finalRotation =
            rotation + pulseEffect.getRotation();

        // White silhouette / glow
        Pencil.I().addDrawing(
            new TextureDrawing(
                whiteTexture,
                glowX,
                glowY,
                glowWidth,
                glowHeight,
                0.35f,
                finalRotation,
                ZIndex.SLOT_MACHINE
            )
        );

        // Actual symbol
        Pencil.I().addDrawing(
            new TextureDrawing(
                texture,
                drawX,
                drawY,
                width,
                height,
                1f,
                finalRotation,
                ZIndex.SLOT_MACHINE
            )
        );
    }

    public boolean isFinished() {
        return finished;
    }

    /*
     * Collision helpers for symbol-to-symbol collisions.
     */

    public float getCenterX() {
        return x + SlotMachine.CELL_W / 2f;
    }

    public float getCenterY() {
        return y + SlotMachine.CELL_H / 2f;
    }

    public float getRadius() {
        return Math.min(
            getWidth(),
            getHeight()
        ) * 0.42f;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void addRotationVelocity(float amount) {
        rotationVelocity += amount;
    }
}
