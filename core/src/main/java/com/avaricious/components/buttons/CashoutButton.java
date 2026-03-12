package com.avaricious.components.buttons;

import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CashoutButton extends DisablableButton {

    private boolean slotMachineIsRunning = false;

    public CashoutButton(Runnable onButtonPressedRunnable, TextureRegion defaultButtonTexture, TextureRegion pressedButtonTexture, TextureRegion hoveredButtonTexture, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable, defaultButtonTexture, pressedButtonTexture, hoveredButtonTexture, buttonRectangle, key, ZIndex.BUTTON_BOARD);
    }

    @Override
    boolean disabled() {
        return ScoreDisplay.I().isClear() || slotMachineIsRunning;
    }

    public void setSlotMachineIsRunning(boolean slotMachineIsRunning) {
        this.slotMachineIsRunning = slotMachineIsRunning;
    }
}
