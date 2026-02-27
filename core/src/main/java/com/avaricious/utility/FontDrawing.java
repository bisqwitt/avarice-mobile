package com.avaricious.utility;

import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FontDrawing implements Drawing {

    private final int layer;

    private final BitmapFont font;
    private final GlyphLayout text;
    private final Vector2 pos;

    public FontDrawing(BitmapFont font, GlyphLayout txt, Vector2 pos, int layer) {
        this.font = font;
        this.text = txt;
        this.pos = pos;
        this.layer = layer;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setProjectionMatrix(ScreenManager.getUiViewport().getCamera().combined);
        font.draw(batch, text, pos.x, pos.y);
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
    }

    @Override
    public int getLayer() {
        return layer;
    }
}
