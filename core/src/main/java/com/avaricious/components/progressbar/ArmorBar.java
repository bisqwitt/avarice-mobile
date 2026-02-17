package com.avaricious.components.progressbar;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;

public class ArmorBar extends HealthBar {

    public ArmorBar(float maxHealth) {
        super(maxHealth, 7.3f, Assets.I().get(AssetKey.ARMOR_ICON), Assets.I().get(AssetKey.SILVER_PIXEL));
    }

}
