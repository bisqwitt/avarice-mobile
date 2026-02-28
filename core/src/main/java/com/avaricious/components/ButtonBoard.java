package com.avaricious.components;

import com.avaricious.components.buttons.CashoutButton;
import com.avaricious.components.buttons.SpinButton;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ButtonBoard {

    private final float BOARD_X = 0.7f;
    private final float BOARD_Y = 6.7f;

    private final float BUTTON_W = 79 / 25f;
    private final float BUTTON_H = 25 / 25f;

    private final SpinButton spinAgainButton;
    private final CashoutButton cashoutButton;

    public ButtonBoard(Runnable onSpinButtonPressed, Runnable onCashoutButtonPressed) {
        spinAgainButton = new SpinButton(onSpinButtonPressed,
            Assets.I().get(AssetKey.SPIN_BUTTON),
            Assets.I().get(AssetKey.SPIN_BUTTON_PRESSED),
            Assets.I().get(AssetKey.SPIN_BUTTON),
            new Rectangle(BOARD_X + 4.35f, BOARD_Y + 0.6f, BUTTON_W, BUTTON_H), Input.Keys.SPACE);

        cashoutButton = new CashoutButton(onCashoutButtonPressed,
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            Assets.I().get(AssetKey.CASHOUT_BUTTON_PRESSED),
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
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
