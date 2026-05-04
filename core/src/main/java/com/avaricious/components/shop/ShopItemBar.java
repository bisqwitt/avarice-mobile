package com.avaricious.components.shop;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class ShopItemBar {

    private final Rectangle buyBounds;
    private final Vector2 firstUpgradePos = new Vector2(1.55f, 13.15f);
    private final float UPGRADE_OFFSET = 2f;

    private final List<AbstractShopItem> shopItems = new ArrayList<>();

    public ShopItemBar(Rectangle buyBounds) {
        this.buyBounds = buyBounds;
        load();
    }

    public void load() {
        shopItems.clear();
        for (int i = 0; i < 3; i++) {
            Vector2 pos = new Vector2(firstUpgradePos.x + i * UPGRADE_OFFSET, firstUpgradePos.y);
            int random = MathUtils.random(1, 100);
            if (random < 75) shopItems.add(new CardShopItem(buyBounds, pos));
            else if (random < 90) shopItems.add(new RingShopItem(buyBounds, pos));
            else {
                shopItems.add(MathUtils.random(0, 1) == 0 ? new QuestShopItem(buyBounds, pos) : new PotionShopItem(buyBounds, pos));
            }
        }
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        shopItems.forEach(item -> item.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta));
    }

    public void draw(float delta) {
        shopItems.forEach(item -> item.draw(delta));
    }

    public void setY(float y) {
        shopItems.forEach(item -> item.setY(y));
    }

    public boolean isDragging() {
        return shopItems.stream().anyMatch(AbstractShopItem::isDragging);
    }

    public boolean isSelected() {
        return shopItems.stream().anyMatch(AbstractShopItem::isSelected);
    }

}
