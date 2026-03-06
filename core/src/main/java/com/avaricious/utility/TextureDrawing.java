package com.avaricious.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class TextureDrawing implements Drawing {

    private final ZIndex layer;

    private final boolean additionalValues;
    private final TextureRegion textureRegion;
    private final Rectangle bounds;

    private float scale;
    private float rotation;

    private boolean usePosAsOrigin = false;

    private Runnable beforeDrawing;
    private Runnable afterDrawing;

    private Color color;

    public TextureDrawing(TextureRegion textureRegion, Rectangle bounds, ZIndex layer) {
        this.textureRegion = textureRegion;
        this.bounds = bounds;
        this.layer = layer;
        additionalValues = false;
    }

    public TextureDrawing(TextureRegion textureRegion, Rectangle bounds, ZIndex layer, Color color) {
        this.textureRegion = textureRegion;
        this.bounds = bounds;
        this.layer = layer;
        this.color = color;
        additionalValues = false;
    }

    public TextureDrawing(TextureRegion textureRegion, Rectangle bounds, float scale, float rotation, ZIndex layer) {
        this.textureRegion = textureRegion;
        this.bounds = bounds;
        this.scale = scale;
        this.rotation = rotation;
        this.layer = layer;
        additionalValues = true;
    }

    public TextureDrawing(TextureRegion textureRegion, Rectangle bounds, float scale, float rotation, ZIndex layer, Color color) {
        this.textureRegion = textureRegion;
        this.bounds = bounds;
        this.scale = scale;
        this.rotation = rotation;
        this.layer = layer;
        this.color = color;
        additionalValues = true;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (beforeDrawing != null) {
            beforeDrawing.run();
        }
        if (color != null) batch.setColor(color);

        if (additionalValues) {
            batch.draw(textureRegion,
                bounds.x, bounds.y,
                bounds.width / 2f, usePosAsOrigin ? 0 : bounds.height / 2f,
                bounds.width, bounds.height,
                scale, scale,
                rotation);
        } else {
            batch.draw(textureRegion,
                bounds.x, bounds.y,
                bounds.width, bounds.height);
        }

        if (color != null) batch.setColor(1f, 1f, 1f, 1f);
        if (afterDrawing != null) {
            afterDrawing.run();
        }
    }

    @Override
    public ZIndex getZIndex() {
        return layer;
    }

    public TextureDrawing setBeforeDrawing(Runnable beforeDrawing) {
        this.beforeDrawing = beforeDrawing;
        return this;
    }

    public TextureDrawing setAfterDrawing(Runnable afterDrawing) {
        this.afterDrawing = afterDrawing;
        return this;
    }

    public TextureDrawing usePosAsOrigin() {
        usePosAsOrigin = true;
        return this;
    }
}
