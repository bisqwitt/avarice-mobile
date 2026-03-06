package com.avaricious.utility;

import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
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

    private SpriteBatch batch;

    private final List<Drawing> drawings = new ArrayList<>();

    private final TextureRegion blackTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private Rectangle scissors;
    private Runnable beforeDrawing;

    private boolean dimBackground = false;
    private ZIndex dimLayer = null;
    private float dimAlpha = 0f;
    private float targetDimAlpha = 0f;
    private final float dimSpeed = 15f; // higher = faster fade

    public void draw(SpriteBatch batch) {
        updateDarkenAnimation(Gdx.graphics.getDeltaTime());

        drawings.sort(Comparator.comparingInt(drawing -> drawing.getZIndex().index()));
        for (Drawing drawing : drawings) {
            drawing.draw(batch);
        }
        drawings.clear();
    }

    private void updateDarkenAnimation(float delta) {
        targetDimAlpha = dimBackground ? 0.25f : 0f;

        // smooth movement toward target
        dimAlpha = Interpolation.fade.apply(
            dimAlpha,
            targetDimAlpha,
            Math.min(1f, delta * dimSpeed)
        );
    }

    public void addDrawing(Drawing drawing) {
        if (beforeDrawing != null && drawing instanceof TextureDrawing) {
            ((TextureDrawing) drawing).setBeforeDrawing(beforeDrawing);
            beforeDrawing = null;
        }
        drawings.add(drawing);
    }

    public void toggleDarkenEverythingBehindWindow(ZIndex layer) {
        dimBackground = !dimBackground;
        dimLayer = layer;
    }

    public void drawDarkenWindow() {
        if (dimAlpha <= 0.01f) return;

        Color base = Assets.I().shadowColor();
        Color animatedColor = new Color(base.r, base.g, base.b, dimAlpha);

        Pencil.I().addDrawing(new TextureDrawing(
            blackTexture,
            new Rectangle(
                -1f,
                -1f,
                ScreenManager.getViewport().getWorldWidth() + 2,
                ScreenManager.getViewport().getWorldHeight() + 2
            ),
            dimLayer,
            animatedColor
        ));
    }

    public void startScissors(Camera cam, Matrix4 matrix, Rectangle area) {
        scissors = new Rectangle();
        beforeDrawing = () -> {
            ScissorStack.calculateScissors(cam, matrix, area, scissors);
            batch.flush();
            ScissorStack.pushScissors(scissors);
        };
    }

    public void endScissors() {
        Drawing drawing = drawings.get(drawings.size() - 1);
        if (drawing instanceof TextureDrawing)
            ((TextureDrawing) drawing).setAfterDrawing(() -> {
                batch.flush();
                ScissorStack.popScissors();
            });
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }
}
