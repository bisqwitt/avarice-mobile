package com.avaricious.components;

import static com.badlogic.gdx.math.MathUtils.lerp;

import com.avaricious.components.buttons.CashoutButton;
import com.avaricious.components.buttons.SpinButton;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ButtonBoard {

    private static ButtonBoard instance;

    public static ButtonBoard I() {
        return instance == null ? instance = new ButtonBoard() : instance;
    }

    private final float BOARD_X = 0.75f;
    private final float BOARD_Y = 0f;

    private final float BUTTON_W = 79 / 27f;
    private final float BUTTON_H = 25 / 27f;

    private SpinButton spinAgainButton;
    private CashoutButton cashoutButton;

    private boolean isMoving = false;

    private final float moveDistance = 5f;
    private final float moveDuration = 0.45f;

    private float moveTime = 0f;

    private float startY;
    private float targetY;

    private ButtonBoard() {
    }

    public ButtonBoard init(Runnable onSpinButtonPressed, Runnable onCashoutButtonPressed) {
        spinAgainButton = new SpinButton(onSpinButtonPressed,
            new Rectangle(BOARD_X + 4.35f, BOARD_Y + 0.6f, BUTTON_W, BUTTON_H), Input.Keys.SPACE);

        cashoutButton = new CashoutButton(onCashoutButtonPressed,
            new Rectangle(BOARD_X + 0.3f, BOARD_Y + 0.6f, BUTTON_W, BUTTON_H), Input.Keys.ENTER);
        return this;
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed) {
        spinAgainButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        cashoutButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

    public void draw(float delta) {
        updateMovement(delta);

        spinAgainButton.update(delta);
        cashoutButton.update(delta);

        spinAgainButton.draw();
        cashoutButton.draw();
    }

    public void moveOut() {
        startY = spinAgainButton.getBounds().y;
        targetY = startY - moveDistance;
        moveTime = 0f;
        isMoving = true;
    }

    public void moveIn() {
        startY = spinAgainButton.getBounds().y;
        targetY = startY + moveDistance;
        moveTime = 0f;
        isMoving = true;
    }

    private void updateMovement(float delta) {
        if (!isMoving) return;

        moveTime += delta;
        float progress = Math.min(moveTime / moveDuration, 1f);
        float eased = Interpolation.smooth.apply(progress);

        spinAgainButton.getBounds().y = lerp(startY, targetY, eased);
        cashoutButton.getBounds().y = lerp(startY, targetY, eased);

        if (progress >= 1f) {
            spinAgainButton.getBounds().y = targetY;
            cashoutButton.getBounds().y = targetY;
            isMoving = false;
        }
    }

    public void setVisible(boolean visible) {
        spinAgainButton.setVisibleAnimated(visible);
        cashoutButton.setVisibleAnimated(visible);
    }
}
