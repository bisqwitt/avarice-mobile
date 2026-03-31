package com.avaricious.items.upgrades.cards;

import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.GameStateLogger;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawTwoCardsDisabledOnZeroDefence extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.HACK);

    @Override
    public String description() {
        return "Draw 2 cards. For the rest of this round, your cards are disabled while you have no Armor";
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
    protected void onApply() {
        Hand.I().drawCards(2);
        GameStateLogger.I().disableCardsOnNoArmor();
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
        };
    }
}
