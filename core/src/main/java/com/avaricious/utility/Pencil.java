package com.avaricious.utility;

import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Pencil {

    private static Pencil instance;

    public static Pencil I() {
        return instance == null ? instance = new Pencil() : instance;
    }

    private Pencil() {
    }

    private final List<Drawing> drawings = new ArrayList<>();

    private final TextureRegion blackTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);
    private boolean darkenEverythingBehindWindow = false;

    private Rectangle scissors;
    private Runnable beforeDrawing;
    private Runnable afterDrawing;

    public void draw(SpriteBatch batch) {
        drawings.sort(Comparator.comparingInt(Drawing::getLayer));
        for (Drawing drawing : drawings) {
            drawing.draw(batch);
        }
        drawings.clear();
    }

    public void addDrawing(Drawing drawing) {
        if (beforeDrawing != null && TextureDrawing.class.isInstance(drawing)) {
            ((TextureDrawing) drawing).setBeforeDrawing(beforeDrawing);
            beforeDrawing = null;
        }
        drawings.add(drawing);
    }

    public void toggleDarkenEverythingBehindWindow() {
        darkenEverythingBehindWindow = !darkenEverythingBehindWindow;
    }

    public void drawDarkenWindow(SpriteBatch batch) {
        if (!darkenEverythingBehindWindow) return;
        Pencil.I().addDrawing(new TextureDrawing(
            blackTexture,
            new Rectangle(-1f, -1f,
                ScreenManager.getViewport().getWorldWidth() + 2,
                ScreenManager.getViewport().getWorldHeight() + 2),
            20, Assets.I().shadowColor()
        ));
    }

    public void startScissors(Camera cam, Matrix4 matrix, Rectangle area) {
        scissors = new Rectangle();
        beforeDrawing = () -> {
            ScissorStack.calculateScissors(cam, matrix, area, scissors);
            ScissorStack.pushScissors(scissors);
        };
    }

    public void endScissors() {
        Drawing drawing = drawings.get(drawings.size() - 1);
        if (drawing instanceof TextureDrawing)
            ((TextureDrawing) drawing).setAfterDrawing(ScissorStack::popScissors);
    }

}
