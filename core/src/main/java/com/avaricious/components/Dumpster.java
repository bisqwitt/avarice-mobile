package com.avaricious.components;

import com.avaricious.utility.AssetAnimationKey;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Dumpster {
    public enum State {CLOSED, OPENING, OPEN, CLOSING}

    public static final Rectangle bounds = new Rectangle(6.5f, 0.5f, 70 / 35f, 80 / 35f);
    public static final Rectangle hitBox = new Rectangle(6.25f, 0.25f, bounds.width + 0.5f, bounds.height + 0.5f);

    // How far offscreen (to the right) it starts
    private final float slideOffsetX = 2.5f; // tune this

    private float slide = 0f; // 0=offscreen, 1=onscreen

    private final float frameDuration = 0.075f;
    private final Animation<TextureRegion> openAnim =
        Assets.I().getAnimation(AssetAnimationKey.DUMPSTER_OPEN, frameDuration, Animation.PlayMode.NORMAL);
    private final Animation<TextureRegion> closeAnim =
        Assets.I().getAnimation(AssetAnimationKey.DUMPSTER_CLOSE, frameDuration, Animation.PlayMode.NORMAL);

    private State state = State.CLOSED;
    private float stateTime = 0f;

    private final TextureRegion closedFrame = Assets.I().get(AssetKey.DUMPSTER_CLOSED);
    private final TextureRegion openFrame = Assets.I().get(AssetKey.DUMPSTER_OPENED);
    private final TextureRegion dumpsterShadow = Assets.I().get(AssetKey.DUMPSTER_SHADOW);

    private final float restBoundsX = bounds.x;
    private final float restHitX = hitBox.x;

    private boolean cardIsDiscarding = false;


    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void update(float delta, Vector2 cardCenterPos, boolean cardIsDragging) {
        if(!cardIsDiscarding) {
            // 1) slide in/out
            slide = approach(slide, cardIsDragging ? 1f : 0f, 4f, delta); // speed=10 -> tune
            float x = restBoundsX + getCurrentSlideValue();

            bounds.x = x;
            hitBox.x = restHitX + getCurrentSlideValue();
        }

        // 2) only “interact” once mostly visible (prevents opening while still offscreen)
        boolean active = slide > 0.75f;

        if (active && hitBox.contains(cardCenterPos) && state != State.OPEN && state != State.OPENING) {
            state = State.OPENING;
            stateTime = 0f;
        } else if ((!active || !hitBox.contains(cardCenterPos)) && state != State.CLOSED && state != State.CLOSING) {
            state = State.CLOSING;
            stateTime = 0f;
        }

        if (state == State.OPENING || state == State.CLOSING) stateTime += delta;

        if (state == State.OPENING && openAnim.isAnimationFinished(stateTime)) {
            state = State.OPEN;
            stateTime = 0f;
        } else if (state == State.CLOSING && closeAnim.isAnimationFinished(stateTime)) {
            state = State.CLOSED;
            stateTime = 0f;
        }
    }

    public void draw(SpriteBatch batch) {
        if (slide <= 0.001f) return; // fully hidden

        batch.setColor(Assets.I().shadowColor());
        batch.draw(dumpsterShadow, bounds.x + 0.15f, bounds.y - 0.25f, bounds.width, bounds.height);
        batch.setColor(1f, 1f, 1f, 1f);

        TextureRegion frame =
            state == State.OPENING ? openAnim.getKeyFrame(stateTime, false) :
                state == State.CLOSING ? closeAnim.getKeyFrame(stateTime, false) :
                    state == State.OPEN ? openFrame : closedFrame;

        batch.setColor(1f, 1f, 1f, slide);
        batch.draw(frame, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private float approach(float current, float target, float speed, float delta) {
        float diff = target - current;
        float step = speed * delta;
        if (Math.abs(diff) <= step) return target;
        return current + Math.signum(diff) * step;
    }

    public float getCurrentSlideValue() {
        return (1f - slide) * slideOffsetX;
    }

}
