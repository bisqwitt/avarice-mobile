package com.avaricious;

import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CreditNumber extends DigitalNumber {

    private final TextureRegion dollarSymbol;

    public CreditNumber(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, Assets.I().yellow(), rectangle, offset);

        dollarSymbol = Assets.I().get(AssetKey.DOLLAR_SYMBOL);
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        super.draw(batch, delta);

        float x = rectangle.x + (numberTextures.size() * offset) + 0.05f;
        float y = calcHoverY();

        Pencil.I().addDrawing(new TextureDrawing(
            dollarSymbol,
            new Rectangle(x, y, rectangle.width, rectangle.height),
            0, color));
    }
}
