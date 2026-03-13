package com.avaricious.components.buttons;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;

public class SpinButton extends DisablableButton {

    private boolean slotMachineIsRunning = false;

    public SpinButton(Runnable onButtonPressedRunnable, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable,
            Assets.I().get(AssetKey.SPIN_BUTTON),
            Assets.I().get(AssetKey.SPIN_BUTTON_PRESSED),
            Assets.I().get(AssetKey.SPIN_BUTTON),
            buttonRectangle, key, ZIndex.BUTTON_BOARD);
    }

    @Override
    boolean disabled() {
        return slotMachineIsRunning;
    }

    public void setSlotMachineIsRunning(boolean slotMachineIsRunning) {
        this.slotMachineIsRunning = slotMachineIsRunning;
    }
}
