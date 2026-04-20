package com.avaricious.components.shop;

import com.avaricious.CreditNumber;
import com.avaricious.components.RingBar;
import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.rings.AbstractRing;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RingShopItem extends AbstractShopItem {

    private final float ITEM_Y_OFFSET = 0.4f;

    public RingShopItem(Rectangle buyBounds, Vector2 initialPos) {
        super(buyBounds, initialPos);
    }

    @Override
    protected void load(Vector2 initialPos) {
        upgrade = AbstractRing.randomRing();
        Rectangle bounds = new Rectangle(
            initialPos.x, initialPos.y + ITEM_Y_OFFSET, 1.5f, 1.5f
        );
        upgrade.addBody(bounds);
        priceTag = new CreditNumber(upgrade.price(), new Rectangle(
            bounds.x + 0.5f, bounds.y, 7 / 20f, 11 / 20f),
            0.4f);
    }

    @Override
    protected void acquireItem() {
        RingBar.I().addRing(AbstractUpgrade.instantiateItem(((AbstractRing) upgrade).getClass()));
    }

    @Override
    public void setY(float y) {
        upgrade.getBody().getPos().y = y + ITEM_Y_OFFSET;
    }

    @Override
    protected float getPriceTagYOffset() {
        return 1.7f;
    }
}
