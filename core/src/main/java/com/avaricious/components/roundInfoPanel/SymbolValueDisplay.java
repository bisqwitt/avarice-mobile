package com.avaricious.components.roundInfoPanel;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SymbolValueDisplay {

    private final TextureRegion colon = Assets.I().get(AssetKey.COLON);

    public void draw(float delta) {
        int index = 0;
        for (Symbol symbol : Symbol.values()) {
            float x = 0.5f;
            float y = 12.5f - index * 1f;

            if (y < 9.5f) {
                x += 2.25f;
                y += 4f;
            }

            Pencil.I().addDrawing(new TextureDrawing(Assets.I().get(symbol.textureKey()),
                new Rectangle(x, y, 1f, 1f), ZIndex.ROUND_INFO_PANEL_UNFOLDED));
            Pencil.I().addDrawing(new TextureDrawing(colon,
                new Rectangle(x + 1, y + 0.2f, 7 / 20f, 11 / 20f), ZIndex.ROUND_INFO_PANEL_UNFOLDED));
            Pencil.I().addDrawing(new TextureDrawing(Assets.I().getDigitalNumber(symbol.baseValue()),
                new Rectangle(x + 1.5f, y + 0.2f, 7 / 20f, 11 / 20f),
                ZIndex.ROUND_INFO_PANEL_UNFOLDED, Assets.I().blue()));

            index++;
        }
    }

}
