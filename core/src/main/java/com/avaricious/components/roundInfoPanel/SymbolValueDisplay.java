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
        Pencil.I().addDrawing(new TextureDrawing(Assets.I().get(Symbol.LEMON.assetKey()),
            new Rectangle(1.25f, 12.5f, 1f, 1f), ZIndex.ROUND_INFO_PANEL_UNFOLDED));
        Pencil.I().addDrawing(new TextureDrawing(colon,
            new Rectangle(2.4f, 12.7f, 7 / 20f, 11 / 20f), ZIndex.ROUND_INFO_PANEL_UNFOLDED));
        Pencil.I().addDrawing(new TextureDrawing(Assets.I().getDigitalNumber(Symbol.LEMON.baseValue()),
            new Rectangle(3f, 12.7f, 7 / 20f, 11 / 20f),
            ZIndex.ROUND_INFO_PANEL_UNFOLDED, Assets.I().blue()));

    }

}
