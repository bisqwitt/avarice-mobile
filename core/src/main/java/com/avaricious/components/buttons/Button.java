package com.avaricious.components.buttons;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Button {

    protected final Rectangle buttonRectangle;

    protected final TextureRegion defaultButtonTexture;
    protected final TextureRegion pressedButtonTexture;
    private final TextureRegion hoveredButtonTexture;
    private final TextureRegion buttonShadow = Assets.I().get(AssetKey.BUTTON_SHADOW);

    private final int key;
    private final Runnable onButtonPressedRunnable;

    protected TextureRegion currentTexture;
    protected boolean wasHovered;
    private boolean spaceWasPressed;
    protected boolean showShadow = true;

    private int layer = 3;

    private boolean pressDownIsOnButton = false;

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

    public void draw() {
        drawAt(buttonRectangle);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed) {
        boolean hovering = buttonRectangle.contains(mouse.x, mouse.y);
        if (hovering && !wasHovered) currentTexture = hoveredButtonTexture;
        else if (!hovering && wasHovered) currentTexture = defaultButtonTexture;

        if (pressed && !wasPressed) {
            pressDownIsOnButton = hovering;
            if (hovering)
                currentTexture = pressedButtonTexture;
        } else if (!pressed && wasPressed) {
            currentTexture = hovering ? hoveredButtonTexture : defaultButtonTexture;
            if (hovering && pressDownIsOnButton) onButtonPressed();
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

    protected void drawAt(Rectangle bounds) {
        if (showShadow) {
            Rectangle shadowBounds = new Rectangle(bounds).setY(bounds.y - 0.1f);
            Pencil.I().addDrawing(new TextureDrawing(
                buttonShadow, shadowBounds,
                layer, Assets.I().shadowColor()
            ));
        }
        Pencil.I().addDrawing(new TextureDrawing(
            currentTexture, bounds,
            layer
        ));
    }

    protected void onButtonPressed() {
        onButtonPressedRunnable.run();
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
    }

    public Button setLayer(int layer) {
        this.layer = layer;
        return this;
    }
}
