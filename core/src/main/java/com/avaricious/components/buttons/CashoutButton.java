package com.avaricious.components.buttons;

import com.avaricious.components.displays.PatternDisplay;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CashoutButton extends DisablableButton {

    private boolean slotMachineIsRunning = false;

    public CashoutButton(Runnable onButtonPressedRunnable, TextureRegion defaultButtonTexture, TextureRegion pressedButtonTexture, TextureRegion hoveredButtonTexture, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable, defaultButtonTexture, pressedButtonTexture, hoveredButtonTexture, buttonRectangle, key);
    }

    @Override
    boolean disabled() {
        return PatternDisplay.I().isEmpty() || slotMachineIsRunning;
    }

    public void setSlotMachineIsRunning(boolean slotMachineIsRunning) {
        this.slotMachineIsRunning = slotMachineIsRunning;
    }
}
