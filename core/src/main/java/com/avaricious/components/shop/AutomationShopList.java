package com.avaricious.components.shop;

import com.avaricious.components.automations.Automations;
import com.avaricious.components.texts.AutoSpinCapacityDescriptionText;
import com.avaricious.components.texts.AutoSpinCapacityText;
import com.avaricious.components.texts.AutoSpinText;
import com.avaricious.components.texts.HandCapacityDescriptionText;
import com.avaricious.components.texts.HandCapacityText;
import com.avaricious.utility.Seq;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class AutomationShopList {

    private final List<AutomationShopItem> items = new ArrayList<>();

    private final Rectangle bounds;

    private float scrollOffset = 0f;

    private final float itemSpacing = 3.5f;
    private final float scrollSpeed = 1.5f;

    private boolean dragging = false;
    private float lastTouchY;

    public AutomationShopList(Rectangle bounds) {
        this.bounds = bounds;
        items.add(new AutomationShopItem(
            new AutoSpinText(),
            3, Automations.I().getAutoSpin()));
        items.add(new AutomationShopItem(
            new AutoSpinCapacityText(),
            new AutoSpinCapacityDescriptionText(),
            3, Automations.I().getAutoSpinCapacity()));
        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));

        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));
        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));
        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));
        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));
        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));
        items.add(new AutomationShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));

        updateItemPositions();
    }

    public void draw(float delta) {
        Seq.of(items).forEach(item -> item.draw(delta));
    }

    public void handleInput(
        Vector2 mouse,
        boolean touching,
        boolean wasTouching
    ) {
        boolean justTouched = touching && !wasTouching;
        boolean justReleased = !touching && wasTouching;

        if (!bounds.contains(mouse) && !dragging) {
            return;
        }

        if (justTouched) {
            dragging = true;
            lastTouchY = mouse.y;
        }

        if (dragging && touching) {
            float deltaY = mouse.y - lastTouchY;

            scrollOffset -= deltaY;

            clampScroll();
            updateItemPositions();

            lastTouchY = mouse.y;
        }

        if (justReleased) {
            dragging = false;
        }

        Seq.of(items).forEach(item ->
            item.handleInput(mouse, touching, wasTouching)
        );
    }

    private void updateItemPositions() {
        float y = bounds.y + bounds.height + scrollOffset;

        for (AutomationShopItem item : items) {
            item.setY(y);
            y -= itemSpacing;
        }
    }

    private void clampScroll() {
        float contentHeight = items.size() * itemSpacing;
        float maxScroll = Math.max(0f, contentHeight - bounds.height);

        scrollOffset = Math.max(-maxScroll, Math.min(scrollOffset, 0f));
    }

    public void setY(float y) {
        bounds.y = y;
        updateItemPositions();
    }

    public Rectangle getBounds() {
        return bounds;
    }

}
