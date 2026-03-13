package com.avaricious.components;

import com.avaricious.components.buttons.CashoutButton;
import com.avaricious.components.buttons.SpinButton;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ButtonBoard {

    private final float BOARD_X = 0.75f;
    private final float BOARD_Y = 9f;

    private final float BUTTON_W = 79 / 27f;
    private final float BUTTON_H = 25 / 27f;

    private final SpinButton spinAgainButton;
    private final CashoutButton cashoutButton;

    public ButtonBoard(Runnable onSpinButtonPressed, Runnable onCashoutButtonPressed) {
        spinAgainButton = new SpinButton(onSpinButtonPressed,
            new Rectangle(BOARD_X + 4.35f, BOARD_Y + 0.6f, BUTTON_W, BUTTON_H), Input.Keys.SPACE);

        cashoutButton = new CashoutButton(onCashoutButtonPressed,
            new Rectangle(BOARD_X + 0.3f, BOARD_Y + 0.6f, BUTTON_W, BUTTON_H), Input.Keys.ENTER);
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed) {
        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        cashoutButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

    public void draw(SpriteBatch batch, float delta) {
        spinAgainButton.update(delta);
        cashoutButton.update(delta);

        spinAgainButton.draw();
        cashoutButton.draw();
    }


    public void setVisible(boolean visible) {
        spinAgainButton.setVisibleAnimated(visible);
        cashoutButton.setVisibleAnimated(visible);
    }

    public void setSlotMachineIsSpinning(boolean slotMachineIsSpinning) {
        spinAgainButton.setSlotMachineIsRunning(slotMachineIsSpinning);
        cashoutButton.setSlotMachineIsRunning(slotMachineIsSpinning);
    }
}
