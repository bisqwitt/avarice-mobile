package com.avaricious.upgrades;

import com.avaricious.components.RelicBag;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.effects.particle.ParticleType;
import com.avaricious.screens.ScreenManager;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RingKey;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.Timer;

public class RandomRelic {

    private final Rectangle buyBounds;

    private Relic relic = new RandomUpgrade();
    private boolean dragging = false;

    private final Rectangle relicBounds = new Rectangle(3.75f, 7.75f, 32 / 23f, 32 / 23f);
    private DragableSlot slot = new DragableSlot(relicBounds);

    private int currentTextureIndex = 0;
    private float lastTextureChange = 0f;
    private boolean bought = false;

    private float whiten = 0f;
    private boolean whitening = false;

    // Shake / charge
    private boolean charging = false;
    private float charge = 0f;              // 0..1
    private float chargeDuration = 3.5f;    // seconds until rip
    private float shakeTime = 0f;

    private final Vector2 shakeOffset = new Vector2();
    private float shakeRotDeg = 0f;

    // Rip / reveal
    private boolean ripped = false;

    public RandomRelic(Rectangle buyBounds) {
        this.buyBounds = buyBounds;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        update(delta);
        slot.update(delta);
        if (bought) return;

        if (touching && !wasTouching && slot.getBounds().contains(mouse)) {
            this.dragging = true;
            slot.targetScale = 1.3f;
            slot.beginDrag(mouse.x, mouse.y, 0);

            PopupManager.I().createTooltip(relic, slot.getRenderPos(new Vector2()));
        }

        if (touching && dragging) {
            Vector2 cardRenderPos = slot.getRenderPos(new Vector2());

            slot.dragTo(mouse.x, mouse.y, 0);
            PopupManager.I().updateTooltip(
                new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + 2.85f),
                true
            );
        }

        if (!touching && wasTouching && dragging) {
            if (buyBounds.contains(slot.getCardCenter())) {
                buy();
            } else {
                slot.targetScale = 1f;
                slot.endDrag(0);
            }
            dragging = false;
            PopupManager.I().killTooltip();
        }
    }

    public void draw() {
        Rectangle bounds = slot.getBounds();

        final Vector2 position = slot.getRenderPos(new Vector2());
        final float alpha = slot.getAlpha();
        final float scale = slot.pulseScale() * slot.wobbleScale() * slot.getTargetScale();
        final float rotation = slot.wobbleAngleDeg() + slot.getDragTiltDeg();
        final Vector2 drawPos = new Vector2(position).add(shakeOffset);
        final float drawRot = rotation + shakeRotDeg;

        final Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            getShadowTexture(),
            new Rectangle(
                drawPos.x, drawPos.y - 0.2f * slot.targetScale,
                bounds.width, bounds.height
            ),
            scale, drawRot,
            15, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            getTexture(),
            new Rectangle(drawPos.x, drawPos.y, bounds.width, bounds.height),
            scale, drawRot,
            15, new Color(1f, 1f, 1f, alpha)
        ));

        if (whiten > 0f && !ripped) {
            float easedWhiten = whiten * whiten * (3f - 2f * whiten);
            Pencil.I().addDrawing(new TextureDrawing(
                getWhiteTexture(),
                new Rectangle(drawPos.x, drawPos.y, bounds.width, bounds.height),
                scale, drawRot,
                15, new Color(1f, 1f, 1f, alpha * easedWhiten)
            ));
        }

//        if(ripped) PopupManager.I().updateTooltip(slot.getRenderPos(new Vector2()), true);
    }

    private void update(float delta) {
        lastTextureChange += delta;

        float textureChangeSpeed = 0.1f;
        if (lastTextureChange > textureChangeSpeed) {
            currentTextureIndex = currentTextureIndex == RingKey.values().length - 1 ? 0 : currentTextureIndex + 1;
            lastTextureChange = 0f;
        }

        if (whitening) {
            float speed = 0.5f;
            whiten = Math.min(1f, whiten + delta * speed);
            if (whiten >= 1f) whitening = false;
        }

        shakeTime += delta;

        // --- CHARGE (shake grows) ---
        if (charging && !ripped) {
            charge = Math.min(1f, charge + delta / chargeDuration);

            // Grow non-linearly so it starts subtle and ends violent
            float intensity = charge * charge; // ease-in

            // Frequency increases slightly too
            float freq = 40f + 60f * intensity; // Hz-ish (tweak)

            // Amplitude in your world units (your relic is ~1.39 wide)
            float ampPos = 0.02f + 0.18f * intensity;  // position shake
            float ampRot = 0.4f + 6.0f * intensity;  // degrees shake

            // Deterministic "random-looking" shake using sin/cos
            shakeOffset.set(
                (float) Math.sin(shakeTime * freq * 1.7f) * ampPos,
                (float) Math.cos(shakeTime * freq * 2.1f) * ampPos
            );
            shakeRotDeg =
                (float) Math.sin(shakeTime * freq * 1.3f) * ampRot;

            if (charge >= 1f) {
                beginRip();
            }
        } else {
            shakeOffset.setZero();
            shakeRotDeg = 0f;
        }
    }

    private void buy() {
        bought = true;
        Vector2 pos = slot.getRenderPos(new Vector2());
        Rectangle initalBounds = new Rectangle(pos.x, pos.y, relicBounds.width, relicBounds.height);
        slot = new DragableSlot(initalBounds).setMoveToSpeed(3).setTargetScaleSpeed(5f);

        float screenWidth = ScreenManager.getViewport().getWorldWidth();
        float screenHeight = ScreenManager.getViewport().getWorldHeight();
        slot.moveTo(new Vector2(screenWidth / 2f - (relicBounds.width / 2f), screenHeight / 2f - (relicBounds.width / 2f)));
        slot.targetScale = 2f;
        startWhitening();
        startCharging();
    }

    private void startWhitening() {
        whitening = true;
        whiten = 0f;
    }

    private void startCharging() {
        charging = true;
        ripped = false;
        charge = 0f;
        shakeTime = 0f;
    }

    private void beginRip() {
        ripped = true;

        relic = RelicBag.I().randomRelic();
        Vector2 centerPos = slot.getCardCenter().sub(1, 1);
        ParticleManager.I().create(centerPos.x, centerPos.y, ParticleType.RAINBOW, 0.04f, 15);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                PopupManager.I().createTooltip(relic, slot.getRenderPos(new Vector2()), 16);
            }
        }, 0.25f);
    }

    private TextureRegion getTexture() {
        if (ripped) return Assets.I().get(relic.ringKey().getAssetKey());
        return Assets.I().get(RingKey.values()[currentTextureIndex].getAssetKey());
    }

    private TextureRegion getShadowTexture() {
        if (ripped) return Assets.I().get(relic.ringKey().getShadowKey());
        return Assets.I().get(RingKey.values()[currentTextureIndex].getShadowKey());
    }

    private TextureRegion getWhiteTexture() {
        if (ripped) return Assets.I().get(relic.ringKey().getWhiteKey());
        return Assets.I().get(RingKey.values()[currentTextureIndex].getWhiteKey());
    }

    public boolean isDragging() {
        return dragging;
    }

    public Vector2 getRelicCenter() {
        return slot.getCardCenter();
    }
}
