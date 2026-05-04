package com.avaricious.components.shop;

import com.avaricious.CreditNumber;
import com.avaricious.components.ItemBag;
import com.avaricious.items.AbstractItem;
import com.avaricious.items.potions.AbstractPotion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PotionShopItem extends AbstractShopItem {

    public PotionShopItem(Rectangle buyBounds, Vector2 initialPos) {
        super(buyBounds, initialPos);
    }

    @Override
    protected void load(Vector2 pos) {
        upgrade = AbstractPotion.randomPotion();
        Rectangle bounds = new Rectangle(
            pos.x + 0.2f, pos.y, upgrade.getTextureWidth() / 15, upgrade.getTextureHeight() / 15
        );
        upgrade.addBody(bounds);
        priceTag = new CreditNumber(upgrade.price(), new Rectangle(
            bounds.x + 0.25f, bounds.y, 7 / 20f, 11 / 20f), 0.4f);
    }

    @Override
    protected void acquireItem() {
        ItemBag.I().addItem(AbstractItem.instantiateItem(upgrade.getClass()));
    }

    @Override
    protected float getPriceTagYOffset() {
        return 2.6f;
    }
}
