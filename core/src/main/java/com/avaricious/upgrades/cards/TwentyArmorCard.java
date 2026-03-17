package com.avaricious.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TwentyArmorCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.DUSK_CARD);

    @Override
    public String description() {
        return "Gain " + Assets.I().silverText("20 Armor");
    }

    @Override
    protected void onApply() {
        HealthUi.I().addArmor(20);
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(20, pos, Assets.I().silver()));
    }
}
