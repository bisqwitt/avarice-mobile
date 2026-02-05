package com.avaricious.components.buttons;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Button {

    protected final Rectangle buttonRectangle;

    protected final TextureRegion defaultButtonTexture;
    private final TextureRegion pressedButtonTexture;
    private final TextureRegion hoveredButtonTexture;
    private final TextureRegion buttonShadow = Assets.I().get(AssetKey.BUTTON_SHADOW);

    private final int key;
    private final Runnable onButtonPressedRunnable;

    protected TextureRegion currentTexture;
    protected boolean wasHovered;
    private boolean spaceWasPressed;
    private boolean showShadow = true;

    public Button(Runnable onButtonPressedRunnable,
                  TextureRegion defaultButtonTexture,
                  TextureRegion pressedButtonTexture,
                  TextureRegion hoveredButtonTexture,
                  Rectangle buttonRectangle,
                  int key) {
        this.onButtonPressedRunnable = onButtonPressedRunnable;

        this.defaultButtonTexture = defaultButtonTexture;
        this.pressedButtonTexture = pressedButtonTexture;
        this.hoveredButtonTexture = hoveredButtonTexture;
        this.key = key;

        currentTexture = this.defaultButtonTexture;
        this.buttonRectangle = buttonRectangle;
    }

    public void draw(SpriteBatch batch, float delta) {
        drawAt(batch, buttonRectangle.x, buttonRectangle.y, buttonRectangle.width, buttonRectangle.height);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        boolean hovering = buttonRectangle.contains(mouse.x, mouse.y);
        if (hovering && !wasHovered) currentTexture = hoveredButtonTexture;
        else if (!hovering && wasHovered) currentTexture = defaultButtonTexture;

        if (pressed && !wasPressed) {
            if (buttonRectangle.contains(mouse.x, mouse.y))
                currentTexture = pressedButtonTexture;
        } else if (!pressed && wasPressed) {
            currentTexture = hovering ? hoveredButtonTexture : defaultButtonTexture;
            if (hovering) onButtonPressed();
        }

        boolean spacePressed = Gdx.input.isKeyPressed(key);
        if (spacePressed && !spaceWasPressed) currentTexture = pressedButtonTexture;
        if (!spacePressed && spaceWasPressed) {
            currentTexture = defaultButtonTexture;
            onButtonPressed();
        }

        wasHovered = hovering;
        spaceWasPressed = spacePressed;
    }

    protected void drawAt(SpriteBatch batch, float x, float y, float w, float h) {
        if (showShadow) {
            batch.setColor(Assets.I().shadowColor());
            batch.draw(buttonShadow, x + 0.1f, y - 0.1f, w, h);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.draw(currentTexture, x, y, w, h);
    }

    protected void onButtonPressed() {
        onButtonPressedRunnable.run();
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
    }

}
