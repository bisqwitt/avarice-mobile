package com.avaricious.components.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SpinButton extends DisablableButton {

    private boolean slotMachineIsRunning = false;

    public SpinButton(Runnable onButtonPressedRunnable, TextureRegion defaultButtonTexture, TextureRegion pressedButtonTexture, TextureRegion hoveredButtonTexture, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable, defaultButtonTexture, pressedButtonTexture, hoveredButtonTexture, buttonRectangle, key);
    }

    @Override
    boolean disabled() {
        return slotMachineIsRunning;
    }

    public void setSlotMachineIsRunning(boolean slotMachineIsRunning) {
        this.slotMachineIsRunning = slotMachineIsRunning;
    }
}
