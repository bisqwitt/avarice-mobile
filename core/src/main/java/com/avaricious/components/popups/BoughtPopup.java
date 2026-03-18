package com.avaricious.components.popups;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BoughtPopup extends TextPopup {

    public BoughtPopup(Vector2 pos, ZIndex z) {
        super(Assets.I().get(AssetKey.BOUGHT_TXT), new Rectangle(pos.x, pos.y, 55 / 15f, 13 / 15f), z);
    }
}
