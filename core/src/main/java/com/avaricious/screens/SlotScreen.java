package com.avaricious.screens;

import com.avaricious.DevTools;
import com.avaricious.Main;
import com.avaricious.Profiler;
import com.avaricious.bosses.OneLessCardBoss;
import com.avaricious.components.ButtonBoard;
import com.avaricious.components.HandUi;
import com.avaricious.components.RingBar;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.PlayerHealths;
import com.avaricious.components.roundInfoPanel.PlayerScores;
import com.avaricious.components.roundInfoPanel.RoundInfoPanel;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.components.shop.Shop;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.SlotMachineResultRunner;
import com.avaricious.components.texts.WaitingForOpponentToFinishRoundText;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.rings.OneMoreCardAtStartOfRoundRing;
import com.avaricious.network.NetworkController;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.GameContext;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RunManager;
import com.avaricious.utility.runData.RunDataFileManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.OldTvEffect;

public class SlotScreen extends ScreenAdapter {

    private final Main app;

    private final ScreenShake screenShake;
    private final Shop shop = new Shop(this::onReturnedFromShop);

    private final ButtonBoard buttonBoard = ButtonBoard.I()
        .init(this::onSpinButtonPressed, this::onPlayButtonPressed);

    private final TextureRegion charcoalPixel = Assets.I().get(AssetKey.CHARCOAL_PIXEL);


    private final VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;
    private int symbolsHitLastSpin = 0;

    private final WaitingForOpponentToFinishRoundText waitingForOpponentText = new WaitingForOpponentToFinishRoundText();
    private boolean showWaitingForOpponentText = false;

    public SlotScreen(Main app) {
        this.app = app;
        Pencil.I().setBatch(app.getBatch());

        screenShake = ScreenShake.I().setCameras(app.getViewport().getCamera(), app.getUiViewport().getCamera());
        vfxManager.addEffect(new OldTvEffect());
        SlotMachine.I().setOnLastReelFinished(() -> SlotMachineResultRunner.I().runResult(SlotMachine.I().findMatches()));

        if (DevTools.enableProfiler()) Profiler.start();
    }

    @Override
    public void show() {
        RunManager.I().newRun();

        if (RunManager.I().getRoundsManager().getCurrentRound() == 1)
            drawStartingHand();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                buttonBoard.setVisible(true);
            }
        }, 1);
//        Timer.schedule(new Timer.Task() {
//            @Override
//            public void run() {
//                onSpinButtonPressed();
//            }
//        }, 1.5f);
    }

    @Override
    public void render(float delta) {
        RunDataFileManager.I().update(delta);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        SpriteBatch batch = app.getBatch();
        handleInput(delta);

        app.getViewport().apply();
        Camera camera = app.getViewport().getCamera();

        screenShake.update(delta);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        Pencil.I().drawDarkenWindow();
        RoundInfoPanel.I().draw(delta);
        PlayerScores.I().draw(delta);
        PlayerHealths.I().draw(delta);
        ScoreDisplay.I().draw(delta);
        RunManager.I().getRoundsManager().getRoundTimer().draw(delta);
        buttonBoard.draw(delta);
        RingBar.I().draw(delta);

//        deckUi.draw();
//        ItemBag.I().draw(delta);

        ParticleManager.I().draw(batch, delta);
        SlotMachine.I().draw(delta);   // 10

        HandUi.I().draw(delta);

//        TextureGlow.draw(batch, delta, TextureGlow.Type.NUMBER);

        shop.draw(delta);
//        bossLootWindow.draw(delta);
        PopupManager.I().draw(delta);

//        vfxManager.cleanUpBuffers();
//        vfxManager.beginInputCapture();

//        feltBackground.render(delta);
//        slotScreenBackground.render(delta, 0, SlotMachine.originY - 0.2f, 9f, 6.25f);

//        background.render(batch, delta);

//        ScreenUtils.clear(0.95f, 0.93f, 0.89f, 1f);
        batch.begin();
        batch.draw(charcoalPixel, -3, -3, 15, 26);
//        batch.draw(charcoalPixel, -3f, 17.75f, 15f, 6f);
//        batch.draw(charcoalPixel, -3f, SlotMachine.windowBounds.y + SlotMachine.windowBounds.height - 3f, 15f, 6.15f);
//        batch.draw(charcoalPixel, -3f, -3f, 15f, 13f);
//        batch.draw(charcoalPixel, -3f, SlotMachine.originY - 0.4f, 15f, 6.5f);
        Pencil.I().draw(batch, delta);
        batch.end();
//        bulbBorderShader.update(delta);
//        bulbBorderShader.draw(camera.combined, 1.5f, 15.75f, 6f, 1f);

//        vfxManager.endInputCapture();
//        vfxManager.applyEffects();
//        vfxManager.renderToScreen(
//            app.getViewport().getScreenX(),
//            app.getViewport().getScreenY(),
//            app.getViewport().getScreenWidth(),
//            app.getViewport().getScreenHeight()
//        );
    }

    private void handleInput(float delta) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);
        boolean leftClickPressed = Gdx.input.isTouched();
        if (shop.isShowing()) {
            shop.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
            return;
        }
        SlotMachine.I().handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        RingBar.I().handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        if (!buttonBoard.handleInput(mouse, leftClickPressed, leftClickWasPressed))
            HandUi.I().handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        leftClickWasPressed = leftClickPressed;
    }

    public void onPlayButtonPressed() {
        HandUi.I().applySelectedCard();
    }

    public void onSpinButtonPressed() {
//        if (RoundInfoPanel.I().getSpins() == 0 || !SlotMachine.I().isStale()) return;
//
//        SlotMachine.I().setAlpha(1f);
//        SlotMachine.I().spin();
//        RoundInfoPanel.I().minusSpin();
//
//        for (IUpgradeWithActionOnSpinButtonPressed relicWithActionAfterSpin : Hand.I().getUpgradesOfClass(IUpgradeWithActionOnSpinButtonPressed.class)) {
//            relicWithActionAfterSpin.onSpinButtonPressed();
//        }
    }

    public void onRoundEnd() {
        SlotMachineResultRunner.I().setIsFirstStreakIncrease();

        if (NetworkController.I().getSocketClient().isConnected())
            NetworkController.I().match().sendRoundEnded();
        else onBothPlayersEndedRound();
    }

    public void onBothPlayersEndedRound() {
        PlayerScores playerScores = PlayerScores.I();
        PlayerHealths playerHealths = PlayerHealths.I();

        if (playerScores.getPlayerScore() > playerScores.getEnemyScore()) {
            playerHealths.setEnemyHealth((int) playerHealths.getEnemyHealth() - 20);
        } else {
            playerHealths.setPlayerHealth((int) playerHealths.getPlayerHealth() - 20);
        }

        playerScores.setPlayerScoreNumber(0);
        playerScores.setEnemyScoreNumber(0);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                shop.show();
            }
        }, 1);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        GameContext.I().viewport.update(width, height, true);
        GameContext.I().viewport.update(width, height, true);
        vfxManager.resize(width, height);
    }

    private void onReturnedFromShop() {
        RunManager.I().getRoundsManager().nextRound();
        ScreenManager.I().getScreen(SlotScreen.class).onSpinButtonPressed();
    }

    private void drawStartingHand() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                int drawAmount = 3;
                if (RunManager.I().getRoundsManager().getBoss() instanceof OneLessCardBoss)
                    drawAmount--;
                if (RingBar.I().ringOwned(OneMoreCardAtStartOfRoundRing.class)) drawAmount++;
                Hand.I().drawCards(drawAmount);
            }
        }, 0.25f);
    }

    public int getSymbolsHitLastSpin() {
        return symbolsHitLastSpin;
    }

    public void showWaitingForOpponentText() {
        showWaitingForOpponentText = true;
    }

    public void setSymbolsHitLastSpin(int symbolsHitLastSpin) {
        this.symbolsHitLastSpin = symbolsHitLastSpin;
    }

    public void addSymbolsHitLastSpin() {
        symbolsHitLastSpin++;
    }
}
