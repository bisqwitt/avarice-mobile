package com.avaricious.items.upgrades.cards.newgen.pointsOnSymbol;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PointsOnIronCard extends AbstractPointsOnSymbolCard {

    @Override
    Symbol symbol() {
        return Symbol.IRON;
    }

    @Override
    public TextureRegion texture() {
        return Assets.I().get(AssetKey.MISPRINT_CARD);
    }
}
