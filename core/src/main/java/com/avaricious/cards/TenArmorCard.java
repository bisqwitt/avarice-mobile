package com.avaricious.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class TenArmorCard extends Card {
    @Override
    public String description() {
        return "Gain " + Assets.I().silverText("10 Armor");
    }

    @Override
    protected void onApply() {
        ScreenManager.I().getScreen(SlotScreen.class).getArmorBar().heal(10);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(10, pos, Assets.I().silver()));
    }
}
