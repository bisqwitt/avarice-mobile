package com.avaricious.utility;

import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pencil {

    private static Pencil instance;

    public static Pencil I() {
        return instance == null ? instance = new Pencil() : instance;
    }

    private Pencil() {
    }

    private final TextureRegion blackTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private boolean darkenEverythingBehindWindow = false;

    public void drawInColor(SpriteBatch batch, Color color, Runnable draw) {
        batch.setColor(color);
        draw.run();
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void toggleDarkenEverythingBehindWindow() {
        darkenEverythingBehindWindow = !darkenEverythingBehindWindow;
    }

    public void setDarkenEverythingBehindWindow(boolean value) {
        darkenEverythingBehindWindow = value;
    }

    public void drawDarkenWindow(SpriteBatch batch) {
        if (!darkenEverythingBehindWindow) return;
        drawInColor(batch, Assets.I().shadowColor(),
            () -> batch.draw(blackTexture, -1f, -1f,
                ScreenManager.getViewport().getWorldWidth() + 2,
                ScreenManager.getViewport().getWorldHeight() + 2));
    }

}
