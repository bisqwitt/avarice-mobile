package com.avaricious.components.buttons;

import com.avaricious.components.HandUi;
import com.avaricious.components.roundInfoPanel.RoundTimer;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;

public class PlayCardButton extends DisablableButton {

    public PlayCardButton(Runnable onButtonPressedRunnable, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable,
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            Assets.I().get(AssetKey.CASHOUT_BUTTON_PRESSED),
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            buttonRectangle, key, ZIndex.BUTTON_BOARD);
    }

    @Override
    boolean disabled() {
        return !HandUi.I().cardIsSelected() || RoundTimer.I().timerEnded();
    }
}
