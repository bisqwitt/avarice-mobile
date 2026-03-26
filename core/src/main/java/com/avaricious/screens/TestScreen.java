package com.avaricious.screens;

import com.avaricious.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TestScreen extends ScreenAdapter {

    private final Main app;

    private Texture icon;
    private FrameBuffer pixelBuffer;
    private TextureRegion pixelBufferRegion;

    private static final int PIXEL_BUFFER_SIZE = 32;

    public TestScreen(Main app) {
        this.app = app;
    }

    @Override
    public void show() {
        icon = new Texture("test.png");
        icon.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        pixelBuffer = new FrameBuffer(
            Pixmap.Format.RGBA8888,
            PIXEL_BUFFER_SIZE,
            PIXEL_BUFFER_SIZE,
            false
        );

        pixelBuffer.getColorBufferTexture().setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        pixelBufferRegion = new TextureRegion(pixelBuffer.getColorBufferTexture());
        pixelBufferRegion.flip(false, true);
    }

    private void renderIconToBuffer() {
        SpriteBatch batch = ScreenManager.getBatch();

        pixelBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, PIXEL_BUFFER_SIZE, PIXEL_BUFFER_SIZE));

        batch.begin();
        batch.draw(icon, 0, 0, PIXEL_BUFFER_SIZE, PIXEL_BUFFER_SIZE);
        batch.end();

        pixelBuffer.end();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = ScreenManager.getBatch();
        FitViewport uiViewport = (FitViewport) ScreenManager.getUiViewport();

        renderIconToBuffer();

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();
        batch.draw(pixelBufferRegion, 100, 100, 186 * 3, 176 * 3);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getViewport().update(width, height, true);
        ((FitViewport) ScreenManager.getUiViewport()).update(width, height, true);
    }

    @Override
    public void dispose() {
        if (icon != null) icon.dispose();
        if (pixelBuffer != null) pixelBuffer.dispose();
    }
}
