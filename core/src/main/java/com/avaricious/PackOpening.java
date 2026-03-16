package com.avaricious;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.effects.particle.ParticleType;
import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Rarity;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.rings.AbstractRing;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RingAssetKeys;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public abstract class PackOpening {

    private final AbstractRing randomRing = new AbstractRing() {
        @Override
        public RingAssetKeys keySet() {
            return null;
        }

        @Override
        public String description() {
            return "Random Relic";
        }
    };

    private final Rectangle bounds;
    private DragableSlot body;

    private final Rectangle buyBounds;

    protected int currentTextureIndex = 0;
    private float lastTextureChange = 0f;
    private boolean bought = false;

    private float whiten = 0f;
    private boolean whitening = false;

    private boolean charging = false;
    private float charge = 0f;
    private float chargeDuration = 3.5f;

    private float shakeTime = 0f;
    private final Vector2 shakeOffset = new Vector2();
    private float shakeRotDeg = 0f;

    private final TextureRegion whitePixel = Assets.I().get(AssetKey.WHITE_PIXEL);
    private float beamTime = 0f;
    private float beamLength = 10f;      // in world units (relative to your card size)
    private float beamWidth = 0.22f;     // thickness
    private float beamSpinDeg = 0f;

    private boolean flashActive = false;
    private float flashTime = 0f;
    private float flashDuration = 0.45f;

    private float screenWhiteAlpha = 0f;
    private float coreGlowAlpha = 0f;
    private float coreGlowScale = 0f;

    private static final int BEAM_COUNT = 8;
    private final Rarity[] beamRarity = new Rarity[BEAM_COUNT];

    protected boolean ripped = false;
    private boolean dragging = false;

    public PackOpening(Rectangle bounds, Rectangle buyBounds) {
        this.bounds = bounds;
        body = new DragableSlot(bounds);

        this.buyBounds = buyBounds;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        update(delta);
        body.update(delta);
        if (bought) return;

        if (touching && !wasTouching && body.getBounds().contains(mouse)) {
            this.dragging = true;
            body.targetScale = 1.3f;
            body.beginDrag(mouse.x, mouse.y, 0);

            PopupManager.I().createTooltip(randomRing, body.getRenderPos(new Vector2()));
        }

        if (touching && dragging) {
            Vector2 cardRenderPos = body.getRenderPos(new Vector2());

            body.dragTo(mouse.x, mouse.y, 0);
            PopupManager.I().updateTooltip(
                new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + getTooltipYOffset()),
                true
            );
        }

        if (!touching && wasTouching && dragging) {
            if (buyBounds.contains(body.getCardCenter())) {
                buy();
            } else {
                body.targetScale = 1f;
                body.endDrag(0);
            }
            dragging = false;
            PopupManager.I().killTooltip();
        }
    }

    public void draw() {
        Rectangle bounds = body.getBounds();

        final Vector2 position = body.getRenderPos(new Vector2());
        final float alpha = body.getAlpha();
        final float scale = body.pulseScale() * body.wobbleScale() * body.getTargetScale();
        final float rotation = body.wobbleAngleDeg() + body.getDragTiltDeg();
        final Vector2 drawPos = new Vector2(position).add(shakeOffset);
        final float drawRot = rotation + shakeRotDeg;
        final ZIndex layer = dragging || charging || ripped ? ZIndex.PACK_OPENING_SELECTED : ZIndex.SHOP;

        final Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            getShadowTexture(),
            new Rectangle(
                drawPos.x, drawPos.y - 0.2f * body.targetScale,
                bounds.width, bounds.height
            ), scale, drawRot,
            layer, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));

        if (charging || ripped)
            drawBeamsOrdered(getCenter(), bounds, scale, drawRot, alpha, layer);

        Pencil.I().addDrawing(new TextureDrawing(
            getTexture(),
            new Rectangle(drawPos.x, drawPos.y, bounds.width, bounds.height),
            scale, drawRot,
            layer, new Color(1f, 1f, 1f, alpha)
        ));

        if (whiten > 0f && !ripped) {
            float easedWhiten = whiten * whiten * (3f - 2f * whiten);
            Pencil.I().addDrawing(new TextureDrawing(
                getWhiteTexture(),
                new Rectangle(drawPos.x, drawPos.y, bounds.width, bounds.height),
                scale, drawRot,
                layer, new Color(1f, 1f, 1f, alpha * easedWhiten)
            ));
        }

        if (flashActive) {
            drawCoreGlow(layer);
            drawScreenFlash(layer);
        }
    }

    private void drawBeamsOrdered(Vector2 drawPos, Rectangle bounds, float scale, float drawRot, float alpha, ZIndex layer) {
        float beamsDoneAt = 0.875f;
        float t = Math.min(1f, charge / beamsDoneAt);

        float aBase = alpha * 0.9f;

        for (int i = 0; i < BEAM_COUNT; i++) {
            float beamProgress = t * BEAM_COUNT - i;
            if (beamProgress <= 0f) continue;

            beamProgress = Math.min(1f, beamProgress);

            float angle = (90f + i * 135f + beamSpinDeg) % 360f;

            float flicker = 0.85f + 0.15f * (float) Math.sin(beamTime * 10f + i * 2.3f);
            float a = aBase * flicker;

            float length = beamLength * beamProgress;

//            Color color = Assets.I().getRarityColor(beamRarity[ripped ? beamRarity.length - 1 : i]);
            Color color = new Color(1f, 1f, 1f, a);
            Pencil.I().addDrawing(new TextureDrawing(
                whitePixel,
                new Rectangle(drawPos.x - beamWidth / 2f, drawPos.y, beamWidth, length),
                scale,
                angle,
                layer,
                new Color(color.r, color.g, color.b, a)
            ).usePosAsOrigin());
        }
    }

    private void drawCoreGlow(ZIndex layer) {
        Vector2 center = body.getCardCenter();
        float width = bounds.width * coreGlowScale;
        float height = bounds.height * coreGlowScale;

        Pencil.I().addDrawing(new TextureDrawing(
            getWhiteTexture(),
            new Rectangle(
                center.x - width / 2f,
                center.y - height / 2f,
                width, height
            ),
            1f,
            0f,
            layer,
            new Color(1f, 1f, 1f, coreGlowAlpha)
        ).setBeforeDrawing(() -> ScreenManager.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE))
            .setAfterDrawing(() -> ScreenManager.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)));
    }

    private void drawScreenFlash(ZIndex layer) {
        float w = ScreenManager.getViewport().getWorldWidth();
        float h = ScreenManager.getViewport().getWorldHeight();

        Pencil.I().addDrawing(new TextureDrawing(
            whitePixel,
            new Rectangle(0f, 0f, w, h),
            1f,
            0f,
            layer,
            new Color(1f, 1f, 1f, screenWhiteAlpha)
        ));
    }

    private void update(float delta) {
        lastTextureChange += delta;

        beamTime += delta;
        float targetSpeed = 75 * charge * charge;
        beamSpinDeg += delta * targetSpeed;
        beamSpinDeg %= 360f;

        float textureChangeSpeed = 0.1f;
        if (lastTextureChange > textureChangeSpeed) {
            currentTextureIndex = currentTextureIndex == getTextureAmount() - 1 ? 0 : currentTextureIndex + 1;
            lastTextureChange = 0f;
        }

        if (whitening) {
            float speed = 0.35f;
            whiten = Math.min(1f, whiten + delta * speed);
            if (whiten >= 1f) whitening = false;
        }

        shakeTime += delta;

        if (charging && !ripped) {
            charge = Math.min(1f, charge + delta / chargeDuration);

            float intensity = charge * charge;
            float freq = 40f + 60f * intensity;
            float ampPos = 0.02f + 0.18f * intensity;
            float ampRot = 0.4f + 6f * intensity;

            shakeOffset.set(
                (float) Math.sin(shakeTime * freq * 1.7f) * ampPos,
                (float) Math.cos(shakeTime * freq * 2.1f) * ampPos
            );
            shakeRotDeg = (float) Math.sin(shakeTime * freq * 1.3f) * ampRot;

            if (charge >= 1f && !flashActive) {
                startFlash();
            }
        } else {
            shakeOffset.setZero();
            shakeRotDeg = 0f;
        }

        updateFlash(delta);
    }

    private void updateFlash(float delta) {
        if (!flashActive) return;

        flashTime += delta;
        float t = Math.min(1f, flashTime / flashDuration);

        // Smooth growth
        float eased = t * t * (3f - 2f * t); // smoothstep

        // Core glow starts strong and expands quickly
        coreGlowAlpha = Math.min(1f, eased * 1.2f);
        coreGlowScale = 0.6f + eased * 5.0f;

        // Fullscreen white stays modest, then ramps hard near the end
        float late = Math.max(0f, (t - 0.45f) / 0.55f);
        late = late * late * late; // aggressive end burst
        screenWhiteAlpha = Math.min(1f, late * 1.15f);

        if (t >= 1f) {
            flashActive = false;
            screenWhiteAlpha = 1f; // optional short white hold
            ripOpen();
        }
    }

    private void buy() {
        bought = true;
        Vector2 pos = body.getRenderPos(new Vector2());
        Rectangle initialBounds = new Rectangle(pos.x, pos.y, bounds.width, bounds.height);
        body = new DragableSlot(initialBounds).setMoveToSpeed(3).setTargetScaleSpeed(5);

        float screenWidth = ScreenManager.getViewport().getWorldWidth();
        float screenHeight = ScreenManager.getViewport().getWorldHeight();
        body.moveTo(new Vector2(screenWidth / 2f - (bounds.width / 2f), screenHeight / 2f - (bounds.height / 2f)));
        body.targetScale = 2f;

        whitening = true;
        whiten = 0f;

        ripped = false;
        charging = true;
        shakeTime = 0f;

        float rarityUpgradeChance = 0.50f; // 5%
        for (int i = 0; i < beamRarity.length; i++) {
            boolean upgrade = Math.random() < rarityUpgradeChance;
            Rarity rarity;
            if (i == 0) rarity = upgrade ? Rarity.UNCOMMON : Rarity.COMMON;
            else rarity = upgrade ? beamRarity[i - 1].next() : beamRarity[i - 1];
            beamRarity[i] = rarity;
        }

        Pencil.I().toggleDarkenEverythingBehindWindow(ZIndex.PACK_OPENING_BACKGROUND);
    }

    private void startFlash() {
        flashActive = true;
        flashTime = 0f;
    }

    protected void ripOpen() {
        ripped = true;
        Upgrade upgrade = getResult();
        Vector2 centerPos = body.getCardCenter().sub(1, 1);
        ParticleManager.I().create(centerPos.x, centerPos.y, ParticleType.RAINBOW, 0.05f, ZIndex.PACK_OPENING);
//        ParticleManager.I().create(centerPos.x, centerPos.y, ParticleType.WHITE, 0.05f, 17);

        body.pulse();
        body.wobble();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Vector2 renderPos = body.getRenderPos(new Vector2());
                PopupManager.I().createTooltip(upgrade, new Vector2(
                        renderPos.x - 2f, renderPos.y + getTooltipYOffset() + 0.85f),
                    ZIndex.PACK_OPENING_SELECTED).setVisible(true);
            }
        }, 0.2f);
    }

    public DragableSlot getBody() {
        return body;
    }

    protected abstract TextureRegion getTexture();

    protected abstract TextureRegion getShadowTexture();

    protected abstract TextureRegion getWhiteTexture();

    protected abstract int getTextureAmount();

    protected abstract Upgrade getResult();

    protected abstract float getTooltipYOffset();

    public boolean isDragging() {
        return dragging;
    }

    public Vector2 getCenter() {
        return body.getCardCenter();
    }

}
