package com.avaricious.components;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.components.buttons.DisablableButton;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ButtonBoard {

    private final float BOARD_X = 0.7f;
    private final float BOARD_Y = 6.5f;

    private final TextureRegion buttonBoard = Assets.I().get(AssetKey.BUTTON_BOARD);
    private final TextureRegion buttonBoardShadow = Assets.I().get(AssetKey.BUTTON_BOARD_SHADOW);

    private final DisablableButton spinAgainButton;
    private final DisablableButton cashoutButton;

    private boolean visible;

    public ButtonBoard(Runnable onSpinButtonPressed, Runnable onCashoutButtonPressed) {
        spinAgainButton = new DisablableButton(onSpinButtonPressed,
            Assets.I().get(AssetKey.SPIN_AGAIN_BUTTON),
            Assets.I().get(AssetKey.SPIN_AGAIN_BUTTON_PRESSED),
            Assets.I().get(AssetKey.SPIN_AGAIN_BUTTON),
            Assets.I().get(AssetKey.SPIN_AGAIN_BUTTON_DISABLED),
            new Rectangle(BOARD_X + 4.35f, BOARD_Y + 0.6f, 79 / 25f, 25 / 25f), Input.Keys.SPACE);

        cashoutButton = new DisablableButton(onCashoutButtonPressed,
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            Assets.I().get(AssetKey.CASHOUT_BUTTON_PRESSED),
            Assets.I().get(AssetKey.CASHOUT_BUTTON),
            Assets.I().get(AssetKey.CASHOUT_BUTTON_DISABLED),
            new Rectangle(BOARD_X + 0.3f, BOARD_Y + 0.6f, 79 / 25f, 25 / 25f), Input.Keys.ENTER);
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed) {
        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        cashoutButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

    public void draw(SpriteBatch batch, float delta) {
        if (visible) {
//            batch.setColor(1f, 1f, 1f, 0.25f);
//            batch.draw(buttonBoardShadow, 5f, 1.05f, 206 / 35f, 53 / 35f);
//            batch.setColor(1f, 1f, 1f, 1f);
//            batch.draw(buttonBoard, BOARD_X, BOARD_Y, 206f / 30f, 53f / 30f);
        }
        spinAgainButton.draw(batch, delta);
        cashoutButton.draw(batch, delta);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        spinAgainButton.setDisabled(!visible);
        cashoutButton.setDisabled(!visible);
    }

}
