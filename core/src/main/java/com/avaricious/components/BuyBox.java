package com.avaricious.components;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class BuyBox {

    private final Rectangle bounds = new Rectangle(0.3f, 3.45f, 303 / 36f, 108 / 36f);

    private final TextureRegion buyTxt = Assets.I().get(AssetKey.BUY_TXT);
    private final TextureRegion whiteBackground = Assets.I().get(AssetKey.BUY_BOX_WHITE);
    private final TextureRegion blackTexture = Assets.I().get(AssetKey.BUY_BOX_BLACK);
    private final TextureRegion texture = Assets.I().get(AssetKey.BUY_BOX);

    private float stateTime = 0f;

    private boolean visible = false;
    private float alpha = 0f;
    private float fadeSpeed = 8f; // alpha units per second

    public void draw(float delta) {
        stateTime += delta;
        updateVisibility(delta);

        if (alpha <= 0.001f) {
            return;
        }

        float inset = getPulseInset();
        Rectangle greenBoxBounds = getInsetBounds(bounds, inset);
        Rectangle whiteBoxBounds = getInsetBounds(bounds, -inset);
        float blackAlpha = getPulseAlpha() * alpha;

        Pencil.I().addDrawing(new TextureDrawing(
            whiteBackground,
            whiteBoxBounds,
            ZIndex.SHOP,
            new Color(1f, 1f, 1f, alpha)
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            texture,
            greenBoxBounds,
            ZIndex.SHOP,
            new Color(1f, 1f, 1f, alpha)
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            blackTexture,
            greenBoxBounds,
            ZIndex.SHOP,
            new Color(1f, 1f, 1f, Math.min(alpha, blackAlpha))
        ));

        float txtWidth = 22 / 10f;
        float txtHeight = (14 / 10f) - 0.2f;
        float txtX = (bounds.x + bounds.width / 2f) - txtWidth / 2f;
        float txtY = (bounds.y + bounds.height / 2f) - txtHeight / 2f;
        Pencil.I().addDrawing(new TextureDrawing(
            buyTxt,
            new Rectangle(txtX, txtY, txtWidth, txtHeight),
            ZIndex.SHOP,
            new Color(1f, 1f, 1f, alpha)
        ));
    }

    private void updateVisibility(float delta) {
        float targetAlpha = visible ? 1f : 0f;
        alpha = MathUtils.lerp(alpha, targetAlpha, 1f - (float) Math.exp(-fadeSpeed * delta));

        if (Math.abs(alpha - targetAlpha) < 0.01f) {
            alpha = targetAlpha;
        }
    }

    private float getPulseInset() {
        float pulseSpeed = 4f;
        float maxInset = 0.08f;

        return ((1f - MathUtils.cos(stateTime * pulseSpeed)) / 2f) * maxInset;
    }

    private float getPulseAlpha() {
        float pulseSpeed = 4f;
        float maxAlpha = 0.25f;

        return ((1f - MathUtils.cos(stateTime * pulseSpeed)) / 2f) * maxAlpha;
    }

    private Rectangle getInsetBounds(Rectangle base, float inset) {
        return new Rectangle(
            base.x + inset,
            base.y + inset,
            base.width - inset * 2f,
            base.height - inset * 2f
        );
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
