package com.avaricious;

import com.avaricious.network.NetworkController;
import com.avaricious.screens.InQueueScreen;
import com.avaricious.screens.ScreenManager;
import com.avaricious.utility.Assets;
import com.avaricious.utility.DeviceInfo;
import com.avaricious.utility.GameContext;
import com.avaricious.utility.SeededRandomizer;
import com.avaricious.utility.gameState.GameStateManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    private SpriteBatch batch;
    private FitViewport viewport;
    private FitViewport uiViewport;
    private final DeviceInfo deviceInfo;

    public Main(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Override
    public void create() {
        if (DevTools.deleteGameStateSaveFile()) {
            FileHandle file = Gdx.files.local(GameStateManager.GAME_STATE_FILE_NAME);
            if (file.exists()) file.delete();
        }

        Assets.I().load();
        SeededRandomizer.setSeed(12345);

        batch = new SpriteBatch();
        viewport = new FitViewport(9, 20);
        uiViewport = new FitViewport(900, 2000);

        GameContext.init(batch, viewport, uiViewport, deviceInfo);
        NetworkController.I().connect();

        ScreenManager.create(this).setScreen(InQueueScreen.class);
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public FitViewport getUiViewport() {
        return uiViewport;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);

        Gdx.app.log("V", "width: " + viewport.getWorldWidth() + " height: " + viewport.getWorldHeight());
        Gdx.app.log("SV", "width: " + uiViewport.getWorldWidth() + " height: " + uiViewport.getWorldHeight());
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
