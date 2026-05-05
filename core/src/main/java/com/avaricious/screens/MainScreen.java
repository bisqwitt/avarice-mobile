package com.avaricious.screens;

import com.avaricious.Main;
import com.avaricious.components.background.WarpBackground;
import com.avaricious.components.buttons.Button;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainScreen extends ScreenAdapter {

    private final Main app;

    private final WarpBackground background = new WarpBackground();
    private final TextureRegion avariceTxt = Assets.I().get(AssetKey.AVARICE_TXT);

    private final Button newRunButton = new Button(this::onNewRunButtonPressed,
        Assets.I().get(AssetKey.NEW_RUN_BUTTON),
        Assets.I().get(AssetKey.NEW_RUN_BUTTON_PRESSED),
        Assets.I().get(AssetKey.NEW_RUN_BUTTON),
        new Rectangle(2.5f, 7f, 79 / 20f, 25 / 20f),
        Input.Keys.ENTER, ZIndex.SLOT_MACHINE);
    private final Button continueButton = new Button(this::onContinueButtonPressed,
        Assets.I().get(AssetKey.CONTINUE_BUTTON),
        Assets.I().get(AssetKey.CONTINUE_BUTTON_PRESSED),
        Assets.I().get(AssetKey.CONTINUE_BUTTON),
        new Rectangle(2.5f, 5f, 79 / 20f, 25 / 20f),
        Input.Keys.ENTER, ZIndex.SLOT_MACHINE);

    private boolean leftClickWasPressed = false;

    public MainScreen(Main app) {
        this.app = app;


    }

    private void handleInput(float delta) {
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);
        boolean leftClickPressed = Gdx.input.isTouched();

        newRunButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        continueButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);

        leftClickWasPressed = leftClickPressed;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        SpriteBatch batch = app.getBatch();
        handleInput(delta);

        app.getViewport().apply();
        Camera camera = app.getViewport().getCamera();

        background.render(batch, delta);

        batch.setProjectionMatrix(app.getViewport().getCamera().combined);
        batch.begin();

        batch.draw(avariceTxt,
            2f, 16f, 53 / 10f, 11 / 10f);

        newRunButton.draw();
        continueButton.draw();

        Pencil.I().draw(batch);

        batch.end();
    }

    private void onNewRunButtonPressed() {
        ScreenManager.I().setScreen(SlotScreen.class);
    }

    private void onContinueButtonPressed() {

    }
}
