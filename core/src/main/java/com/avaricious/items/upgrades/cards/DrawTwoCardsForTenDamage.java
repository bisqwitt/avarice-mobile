package com.avaricious.items.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawTwoCardsForTenDamage extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.MIME_CARD);

    @Override
    public String description() {
        return "Draw two Cards, deals 20 damage";
    }

    @Override
    protected void onApply() {
        HealthUi.I().damage(10);
        Hand.I().drawCards(2);
    }

    @Override
    public IUpgradeType type() {
        return CardType.UTILITY;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
        };
    }
}
