package com.avaricious.components.popups;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CreditNumberPopup extends NumberPopup {

    private final TextureRegion dollarSymbol = Assets.I().get(AssetKey.DOLLAR_SYMBOL);

    public CreditNumberPopup(int number, float x, float y, boolean asPercentage, boolean manualHold) {
        super(number, Assets.I().yellow(), x, y, asPercentage, manualHold);
    }

    public CreditNumberPopup(int number, Rectangle bounds, boolean asPercentage, boolean manualHold) {
        super(number, Assets.I().yellow(), bounds, asPercentage, manualHold);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);

        float scale = getScale();
        float rotation = getRotation();
        float alpha = getAlpha();

        Color color = Assets.I().yellow();
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(
            dollarSymbol,
            bounds.x + (numberOffset * digitalNumberTextures.size()), bounds.y,
            bounds.width / 2f, bounds.height / 2f,
            bounds.width, bounds.height,
            scale, scale,
            rotation);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
