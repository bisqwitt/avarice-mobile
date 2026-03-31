package com.avaricious.components.bars;

import com.avaricious.items.upgrades.Deck;
import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class JokerUpgradeBar extends UpgradeBar {

    private final TextureRegion jokerCard;
    private final TextureRegion jokerCardShadow;

    public JokerUpgradeBar(List<? extends AbstractCard> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        super(upgrades, cardRectangle, offset, tooltipOnTop);

        jokerCard = new TextureRegion(Assets.I().get(AssetKey.JOKER_CARD));
        jokerCardShadow = new TextureRegion(Assets.I().get(AssetKey.JOKER_CARD_SHADOW));
    }

    @Override
    protected void onUpgradeClicked(AbstractUpgrade clickedUpgrade) {
        Deck.I().addCardToDeck((AbstractCard) clickedUpgrade);
        cardBounds.remove(clickedUpgrade);
//        cards.remove(clickedUpgrade);
    }

    @Override
    protected TextureRegion getTexture(AbstractUpgrade upgrade) {
        return jokerCard;
    }

    @Override
    protected TextureRegion getShadow(AbstractUpgrade upgrade) {
        return jokerCardShadow;
    }
}
