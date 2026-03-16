package com.avaricious.upgrades.rings;

import com.avaricious.PackOpening;
import com.avaricious.components.RelicBag;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class RingPack extends PackOpening {

    private AbstractRing resultRing;

    public RingPack(Rectangle buyBounds) {
        super(new Rectangle(2.65f, 8.5f, 32 / 23f, 32 / 23f), buyBounds);
    }

    @Override
    protected TextureRegion getTexture() {
        if (ripped) return Assets.I().get(resultRing.keySet().getTextureKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getTextureKey());
    }

    @Override
    protected TextureRegion getShadowTexture() {
        if (ripped) return Assets.I().get(resultRing.keySet().getShadowKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getShadowKey());
    }

    @Override
    protected TextureRegion getWhiteTexture() {
        if (ripped) return Assets.I().get(resultRing.keySet().getWhiteKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getWhiteKey());
    }

    @Override
    protected int getTextureAmount() {
        return RingAssetKeys.values().length;
    }

    @Override
    protected float getTooltipYOffset() {
        return 1.85f;
    }

    @Override
    protected Upgrade getResult() {
        return resultRing = RelicBag.I().randomRelic();
    }
}
