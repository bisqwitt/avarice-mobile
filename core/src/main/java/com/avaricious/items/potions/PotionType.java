package com.avaricious.items.potions;

import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum PotionType implements IUpgradeType {
    DEFAULT;

    @Override
    public TextureRegion getTypeBox() {
        return Assets.I().get(AssetKey.RARITY_BOX_COMMON);
    }
}
