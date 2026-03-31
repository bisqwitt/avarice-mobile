package com.avaricious.items.upgrades.quests;

import com.avaricious.CreditManager;
import com.avaricious.components.ItemBag;
import com.avaricious.utility.Assets;

public class PlaySevenCardsInOneSpinQuest extends AbstractQuest {

    @Override
    public String description() {
        return "Play 7 cards in one round\n"
            + "Reward: " + Assets.I().yellowText("10$");
    }

    @Override
    public int price() {
        return 5;
    }

    @Override
    public void claim() {
        ItemBag.I().removeItem(this);
        CreditManager.I().gain(10);
    }
}
