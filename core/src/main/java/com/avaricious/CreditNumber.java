package com.avaricious;

import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CreditNumber extends DigitalNumber {

    private final TextureRegion dollarSymbol;

    public CreditNumber(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, Assets.I().yellow(), rectangle, offset);

        dollarSymbol = Assets.I().get(AssetKey.DOLLAR_SYMBOL);
    }

    @Override
    public void draw(float delta) {
        super.draw(delta);
        draw(delta, calcScale(), calcRotation());
    }

    @Override
    public void draw(float delta, float scale, float rotation) {
        super.draw(delta, scale, rotation);
        float x = firstDigitBounds.x + (numberTextures.size() * offset) + 0.05f;
        float y = calcNumberY();

        Pencil.I().addDrawing(new TextureDrawing(
            dollarSymbol,
            new Rectangle(x, y, firstDigitBounds.width, firstDigitBounds.height),
            scale, rotation,
            getZIndex(), color));
    }
}
