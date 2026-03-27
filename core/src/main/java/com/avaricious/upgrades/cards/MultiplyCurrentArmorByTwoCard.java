package com.avaricious.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MultiplyCurrentArmorByTwoCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.MADNESS);
    private int armor = 0;

    @Override
    public String description() {
        return "Multiply your current Armor by 2";
    }

    @Override
    public IUpgradeType type() {
        return CardType.DEFENCE;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    protected void onApply() {
        armor = HealthUi.I().getArmor();
        HealthUi.I().addArmor(armor);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(armor, pos, Assets.I().silver()));
    }
}
