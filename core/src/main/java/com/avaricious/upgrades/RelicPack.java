package com.avaricious.upgrades;

import com.avaricious.PackOpening;
import com.avaricious.components.RelicBag;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class RelicPack extends PackOpening {

    private Relic resultRelic;

    public RelicPack(Rectangle buyBounds) {
        super(new Rectangle(2.75f, 7f, 32 / 23f, 32 / 23f), buyBounds);
    }

    @Override
    protected TextureRegion getTexture() {
        if (ripped) return Assets.I().get(resultRelic.ringKey().getAssetKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getAssetKey());
    }

    @Override
    protected TextureRegion getShadowTexture() {
        if (ripped) return Assets.I().get(resultRelic.ringKey().getShadowKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getShadowKey());
    }

    @Override
    protected TextureRegion getWhiteTexture() {
        if (ripped) return Assets.I().get(resultRelic.ringKey().getWhiteKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getWhiteKey());
    }

    @Override
    protected int getTextureAmount() {
        return RingAssetKeys.values().length;
    }

    @Override
    protected void getResult() {
        resultRelic = RelicBag.I().randomRelic();
    }
}
