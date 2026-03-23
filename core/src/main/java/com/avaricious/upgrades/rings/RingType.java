package com.avaricious.upgrades.rings;

import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum RingType implements IUpgradeType {
    ACTIVE(AssetKey.CARD_TYPE_BOX_RED),
    PASSIVE(AssetKey.CARD_TYPE_BOX_BLUE);

    RingType(AssetKey typeBoxAssetKey) {
        this.typeBoxAssetKey = typeBoxAssetKey;
    }

    private final AssetKey typeBoxAssetKey;

    @Override
    public TextureRegion getTypeBox() {
        return Assets.I().get(typeBoxAssetKey);
    }
}
