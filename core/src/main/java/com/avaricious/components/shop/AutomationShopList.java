package com.avaricious.components.shop;

import com.avaricious.components.automations.Automations;
import com.avaricious.components.texts.AutoSpinCapacityDescriptionText;
import com.avaricious.components.texts.AutoSpinCapacityText;
import com.avaricious.components.texts.AutoSpinText;
import com.avaricious.components.texts.HandCapacityDescriptionText;
import com.avaricious.components.texts.HandCapacityText;
import com.avaricious.components.texts.SpinBuyerSpeedText;
import com.avaricious.components.texts.SpinBuyerText;
import com.badlogic.gdx.math.Rectangle;

public class AutomationShopList extends ShopList {

    public AutomationShopList(Rectangle bounds) {
        super(bounds);
        items.add(new ShopItem(
            new AutoSpinText(),
            3, Automations.I().getAutoSpin()));
        items.add(new ShopItem(
            new AutoSpinCapacityText(),
            new AutoSpinCapacityDescriptionText(),
            3, Automations.I().getAutoSpinCapacity()));
        items.add(new ShopItem(
            new SpinBuyerText(),
            3, Automations.I().getSpinBuyer()
        ));
        items.add(new ShopItem(
            new SpinBuyerSpeedText(),
            3, Automations.I().getSpinBuyerSpeed()
        ));


        items.add(new ShopItem(
            new HandCapacityText(),
            new HandCapacityDescriptionText(),
            3, Automations.I().getHandCapacity()));

        updateItemPositions();
    }


}
