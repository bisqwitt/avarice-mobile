package com.avaricious.upgrades;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RingKey;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RandomRelic {

    private final Upgrade upgrade = new RandomUpgrade();
    private boolean dragging = false;

    private final DragableSlot slot = new DragableSlot(
        new Rectangle(3.75f, 7.75f, 32 / 23f, 32 / 23f));

    private int currentTextureIndex = 0;
    private float lastTextureChange = 0f;

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        update(delta);
        slot.update(delta);

        if (touching && !wasTouching && slot.getBounds().contains(mouse)) {
            this.dragging = true;
            slot.targetScale = 1.3f;
            slot.beginDrag(mouse.x, mouse.y, 0);

            PopupManager.I().createTooltip(upgrade, slot.getRenderPos(new Vector2()));
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
            slot.endDrag(0);
            slot.targetScale = 1f;
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

        final Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            getShadowTexture(),
            new Rectangle(
                position.x, position.y - (dragging ? 0.3f : 0.2f),
                bounds.width, bounds.height
            ),
            scale, rotation,
            15, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            getTexture(),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation,
            15, new Color(1f, 1f, 1f, alpha)
        ));
    }

    private void update(float delta) {
        lastTextureChange += delta;

        float textureChangeSpeed = 0.1f;
        if (lastTextureChange > textureChangeSpeed) {
            currentTextureIndex = currentTextureIndex == RingKey.values().length - 1 ? 0 : currentTextureIndex + 1;
            lastTextureChange = 0f;
        }
    }

    private TextureRegion getTexture() {
        return Assets.I().get(RingKey.values()[currentTextureIndex].getAssetKey());
    }

    private TextureRegion getShadowTexture() {
        return Assets.I().get(RingKey.values()[currentTextureIndex].getShadowKey());
    }

}
