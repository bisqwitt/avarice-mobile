package com.avaricious.components.buttons;

import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;

public class CashoutButton extends DisablableButton {

    private boolean slotMachineIsRunning = false;

    public CashoutButton(Runnable onButtonPressedRunnable, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable,
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            Assets.I().get(AssetKey.CASHOUT_BUTTON_PRESSED),
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            buttonRectangle, key, ZIndex.BUTTON_BOARD);
    }

    @Override
    boolean disabled() {
        return ScoreDisplay.I().isClear() || slotMachineIsRunning;
    }

    public void setSlotMachineIsRunning(boolean slotMachineIsRunning) {
        this.slotMachineIsRunning = slotMachineIsRunning;
    }
}
