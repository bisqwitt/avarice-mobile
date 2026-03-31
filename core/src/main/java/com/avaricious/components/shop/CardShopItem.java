package com.avaricious.components.shop;

import com.avaricious.CreditNumber;
import com.avaricious.items.upgrades.Deck;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CardShopItem extends AbstractShopItem {

    public CardShopItem(Rectangle buyBounds, Vector2 initialPos) {
        super(buyBounds, initialPos);
    }

    @Override
    protected void load(Vector2 initialPos) {
        upgrade = AbstractCard.randomCard();
        Rectangle bounds = new Rectangle(
            initialPos.x, initialPos.y, 142 / 80f, 190 / 80f
        );
        upgrade.addBody(bounds);
        priceTag = new CreditNumber(upgrade.price(), new Rectangle(bounds.x + 0.5f, bounds.y, 7 / 20f, 11 / 20f), 0.4f);
    }

    @Override
    protected void acquireItem() {
        Deck.I().addCardToDeck((AbstractCard) upgrade);
    }

    @Override
    protected float getPriceTagYOffset() {
        return 2.6f;
    }

    @Override
    protected float getTooltipYOffset() {
        return 3;
    }
}
