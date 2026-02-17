package com.avaricious.components.background;

import com.avaricious.screens.ScreenManager;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class WarpBackground {

    private final ShaderProgram shader;
    private float time;

    private final TextureRegion whiteTexture;

    public WarpBackground() {
        ShaderProgram.pedantic = false;

        whiteTexture = Assets.I().get(AssetKey.WHITE_PIXEL);
        shader = new ShaderProgram(
            Gdx.files.internal("shader/warp.vert"),
            Gdx.files.internal("shader/warp.frag")
        );
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Shader compile error:\n" + shader.getLog());
        }
    }

    public void render(SpriteBatch batch, float delta) {
        time += delta / 2;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ScreenManager.I().getUiViewport().apply();
        batch.setProjectionMatrix(ScreenManager.I().getUiViewport().getCamera().combined);

        batch.setShader(shader);
        batch.begin();

        shader.setUniformf("u_time", time);

        // Draw full-screen quad / texture
        batch.draw(whiteTexture,
            0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();
        batch.setShader(null);
    }
}
