package com.avaricious.items.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ArmorForEverySymbolHitLastSpin extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.SPLASH);
    private int armor = 0;

    @Override
    public String description() {
        return Assets.I().silverText("+5 Armor") + " for every symbol hit last spin\n"
            + "(" + ScreenManager.I().getScreen(SlotScreen.class).getSymbolsHitLastSpin() + ")";
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
        armor = ScreenManager.I().getScreen(SlotScreen.class).getSymbolsHitLastSpin() * 5;
        HealthUi.I().addArmor(armor);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(armor, pos, Assets.I().silver()));
    }
}
