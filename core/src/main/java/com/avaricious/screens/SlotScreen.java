package com.avaricious.screens;

import com.avaricious.CreditManager;
import com.avaricious.CreditScore;
import com.avaricious.DevTools;
import com.avaricious.Main;
import com.avaricious.Profiler;
import com.avaricious.RoundsManager;
import com.avaricious.TaskScheduler;
import com.avaricious.XpBar;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.ButtonBoard;
import com.avaricious.components.DeckUi;
import com.avaricious.components.HandUi;
import com.avaricious.components.HealthUi;
import com.avaricious.components.RelicBag;
import com.avaricious.components.RingBar;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.Shop;
import com.avaricious.components.StatusUpgradeWindow;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.effects.BorderPulseMesh;
import com.avaricious.effects.EffectManager;
import com.avaricious.effects.TextureEcho;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.screens.mainscreen.BackgroundLayer;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.rings.IRelicWithActionAfterSpin;
import com.avaricious.upgrades.rings.triggerable.AbstractTriggerableRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.PointsPerPatternHit;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.OldTvEffect;

import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final XpBar xpBar;

    private final ScoreDisplay scoreDisplay = new ScoreDisplay();
    private final PatternDisplay patternDisplay = PatternDisplay.I();
//    private final LightBulbBorder lightBulbBorder = new LightBulbBorder();
//    private final LightBulbBorderShader bulbBorderShader = new LightBulbBorderShader();

    private final StatusUpgradeWindow statusUpgradeWindow = new StatusUpgradeWindow(() -> {
    });

    private final ScreenShake screenShake;

    private final BackgroundLayer backgroundLayer = new BackgroundLayer();

    private final ButtonBoard buttonBoard = new ButtonBoard(this::onSpinButtonPressed, this::onCashoutButtonPressed);
    private final HandUi handUi = new HandUi();
    private final DeckUi deckUi = DeckUi.I();
    private final RingBar ringBar = RingBar.I();
    private final HealthUi healthUi = HealthUi.I();

    private final TextureRegion backgroundDarkest = Assets.I().get(AssetKey.BACKGROUND_DARKEST);
    private final TextureRegion backgroundPixel = Assets.I().get(AssetKey.BACKGROUND_PIXEL);
    private final TextureRegion backgroundPixelDarker = Assets.I().get(AssetKey.BACKGROUND_DARKER);
    private final TextureRegion backgroundWhite = Assets.I().get(AssetKey.BACKGROUND_WHITE);
    private final TextureRegion backgroundBrighter = Assets.I().get(AssetKey.BACKGROUND_DARKEST);

    private final CreditScore creditScore = new CreditScore(0,
        new Rectangle(1f, 7.5f, 0.32f * 1.5f, 0.56f * 1.5f), 0.35f * 1.5f);

    private final Shop shop = new Shop(this::onReturnedFromShop);

    private final VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

    private final RoundsManager roundsManager = RoundsManager.I();
    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;
    private int symbolsHitLastSpin = 0;

    public SlotScreen(Main app) {
        this.app = app;
        Pencil.I().setBatch(app.getBatch());
        slotMachine = SlotMachine.I();
        xpBar = new XpBar(statusUpgradeWindow::show);

        screenShake = ScreenShake.I().setCamera(app.getViewport().getCamera());
        vfxManager.addEffect(new OldTvEffect());

        scoreDisplay.setOnInternalScoreDisplayed(() -> {
            AudioManager.I().endPayout();
            if (scoreDisplay.targetScoreReached()) onTargetScoreReached();
        });

        if (DevTools.enableProfiler) Profiler.start();
    }

    @Override
    public void show() {
        backgroundLayer.init();
        slotMachine.setOnLastReelFinished(() -> {
            buttonBoard.setSlotMachineIsSpinning(false);
            runResult();
        });

        drawStartingHand();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                buttonBoard.setVisible(true);
            }
        }, 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        SpriteBatch batch = app.getBatch();
        handleInput(delta);

        app.getViewport().apply();
        Camera camera = app.getViewport().getCamera();

        screenShake.update(delta);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        // Clear capture buffer to transparent
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        scoreDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
        buttonBoard.draw(batch, delta);
        ringBar.draw();

//        lightBulbBorder.draw(1.5f, 15.75f, 6f, 1f, delta);
        healthUi.draw(batch, delta);
        xpBar.draw(batch);      // 5
//        jokerBar.draw(batch, delta);
        deckUi.draw();
        RelicBag.I().draw(batch);

        ParticleManager.I().draw(batch, delta);
        slotMachine.draw(app, delta);   // 10

        handUi.draw(batch, delta);

//        TextureGlow.draw(batch, delta, TextureGlow.Type.NUMBER);

        Pencil.I().drawDarkenWindow();

        shop.draw(batch, delta);
        statusUpgradeWindow.draw(batch, delta);     // 15
        PopupManager.I().draw(batch, delta);

        batch.begin();
        batch.draw(backgroundDarkest, 0, 6, 10, 10);
        batch.draw(backgroundPixel, -3, 14.5f, 15, 6);
        batch.draw(backgroundPixel, -3, -3, 15, 11.75f);
        batch.draw(backgroundPixelDarker, -3, 3, 15, 3.85f);

        batch.draw(backgroundBrighter, -3, 17.9f, 15, 0.1f);
//        batch.draw(backgroundWhiteDarker, -3, 17.85f, 15, 0.05f);
//        batch.draw(backgroundWhite, -3, 14.4f, 15, 0.1f);
////        batch.draw(backgroundWhiteDarker, -3, 14.35f, 15, 0.05f);
//        batch.draw(backgroundWhite, -3, 8.65f, 15, 0.1f);
//        batch.draw(backgroundWhiteDarker, -3, 8.6f, 15, 0.05f);
        batch.draw(backgroundBrighter, -3, 2.9f, 15, 0.1f);
//        batch.draw(backgroundWhiteDarker, -3, 2.8f, 15, 0.05f);
        batch.draw(backgroundBrighter, -3, 6.8f, 15, 0.1f);
//        batch.draw(backgroundWhiteDarker, -3, 6.75f, 15, 0.05f);
        BorderPulseMesh.I().render(batch, delta);
        Pencil.I().draw(batch);
        batch.end();

//        bulbBorderShader.update(delta);
//        bulbBorderShader.draw(camera.combined, 1.5f, 15.75f, 6f, 1f);

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen(
            app.getViewport().getScreenX(),
            app.getViewport().getScreenY(),
            app.getViewport().getScreenWidth(),
            app.getViewport().getScreenHeight()
        );
    }

    private void handleInput(float delta) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        app.getViewport().unproject(mouse);
        boolean leftClickPressed = Gdx.input.isTouched();

        if (shop.isShowing()) {
            shop.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
            leftClickWasPressed = leftClickPressed;
            return;
        }
        statusUpgradeWindow.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        buttonBoard.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        backgroundLayer.handleInput();
//        jokerBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        ringBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        handUi.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        deckUi.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {
        List<PatternHitContext> matches = slotMachine.findMatches();
        if (matches.isEmpty()) {
            healthUi.damage(20);
            buttonBoard.setVisible(true);
            return;
        }

        TaskScheduler scheduler = TaskScheduler.I();
        scheduler.schedule(() -> slotMachine.setRunningResults(true), 0f);

        for (PatternHitContext patternHitContext : matches) {
            List<Slot> slots = patternHitContext.getSlots();
            Slot middleSlot = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));

            scheduler.scheduleNoDelay(() -> {
                if (ringBar.ringOwned(PointsPerPatternHit.class))
                    ringBar.getRingByClass(PointsPerPatternHit.class).onPatternHit();

                for (Slot slot : slots) {
                    slot.beginPatternHit();
                }
            });

            triggerSeparateSlots(matches, patternHitContext, scheduler);
            if (PlayerStats.I().rollChance(DoubleHitChance.class)) {
                scheduler.schedule(() -> PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(DoubleHitChance.class).getTexture(),
                    middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f));
                triggerSeparateSlots(matches, patternHitContext, scheduler);
            }

            ringBar.getRingsOfType(AbstractTriggerableRing.class).stream()
                .filter(ring -> ring.triggerableOn() == AbstractTriggerableRing.TriggerablePer.PATTERN)
                .forEach(ring -> ring.scheduleTrigger(matches, patternHitContext, false));

            scheduler.scheduleNoDelay(() -> PopupManager.I().releaseHoldingNumbers());

            scheduler.schedule(() -> {
                for (Slot slot : slots) {
                    slot.pulse();
                    slot.wobble();

                    EffectManager.create(Assets.I().getSymbol(patternHitContext.getSymbol()),
                        new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                        TextureEcho.Type.SLOT);
                }
                ScreenShake.I().addTrauma(0.3f);

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int mult = criticalHit ? slots.size() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : slots.size();

                PopupManager.I().spawnNumber(mult, Assets.I().red(),
                    middleSlot.getPos().x + ((slots.size() % 2 == 0) ? 2f : 1.5f), middleSlot.getPos().y + 1f,
                    true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        middleSlot.getPos().x + 2.5f, middleSlot.getPos().y + 1f);
                patternDisplay.addTo(PatternDisplay.Type.MULTI, mult);

                AudioManager.I().playHit(EffectManager.streak);
            });

            scheduler.schedule(() -> {
                if(matches.indexOf(patternHitContext) != matches.size() -1) EffectManager.increaseStreak();
                for (Slot slot : slots) {
                    slot.endPatternHit();
                    PopupManager.I().releaseHoldingNumbers();
                }
            });
        }

        int index = 0;
        for (AbstractTriggerableRing triggerableRing : ringBar.getRingsOfType(AbstractTriggerableRing.class)) {
            if(triggerableRing.triggerableOn() == AbstractTriggerableRing.TriggerablePer.SPIN) triggerableRing.scheduleTrigger(matches, null, index == 0);
            index++;
        }

        scheduler.schedule(() -> {
            patternDisplay.addTo(PatternDisplay.Type.STREAK, 1);
            slotMachine.setRunningResults(false);
            EffectManager.endStreak();
            buttonBoard.setVisible(true);

            for (IRelicWithActionAfterSpin relicWithActionAfterSpin : Hand.I().getUpgradesOfClass(IRelicWithActionAfterSpin.class)) {
                relicWithActionAfterSpin.onSpinEnded();
            }
        });

        if (DevTools.autoSpin) {
            scheduler.schedule(this::onCashoutButtonPressed);
            scheduler.schedule(this::onSpinButtonPressed);
        }
        scheduler.runTasks();
    }

    private void triggerSeparateSlots(List<PatternHitContext> matches, PatternHitContext patternHitContext, TaskScheduler scheduler) {
        symbolsHitLastSpin = 0;
        for (Slot slot : patternHitContext.getSlots()) {
            scheduler.schedule(() -> {
                symbolsHitLastSpin++;

                slot.pulse();
                slot.wobble();
                ScreenShake.I().addTrauma(0.2f);

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int points = criticalHit ? patternHitContext.getSymbol().baseValue() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : patternHitContext.getSymbol().baseValue();

                PopupManager.I().spawnNumber(points, Assets.I().blue(),
                    slot.getPos().x + 1.5f, slot.getPos().y + 1f, true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        slot.getPos().x + 1.5f, slot.getPos().y + 2f);
                patternDisplay.addTo(PatternDisplay.Type.POINTS, points);

                EffectManager.create(Assets.I().getSymbol(patternHitContext.getSymbol()),
                    new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                    TextureEcho.Type.SLOT);

                AudioManager.I().playHit(EffectManager.streak);

                xpBar.addXp(points);
            });

            ringBar.getRingsOfType(AbstractTriggerableRing.class).stream()
                .filter(ring -> ring.triggerableOn() == AbstractTriggerableRing.TriggerablePer.SLOT)
                .forEach(ring -> ring.scheduleTrigger(matches, patternHitContext, true));

            if (PlayerStats.I().rollChance(CreditSpawnChance.class)) {
                scheduler.schedule(() -> {
                    float x = slot.getPos().x + 1f;
                    float y = slot.getPos().y + 1f;
                    PopupManager.I().spawnNumber(1, Assets.I().yellow(), x, y, false);
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CreditSpawnChance.class).getTexture(), x + 1f, y);
                    CreditManager.I().gain(1);
                });
            }
        }
    }

    private void onSpinButtonPressed() {
        slotMachine.setAlpha(1f);
        buttonBoard.setSlotMachineIsSpinning(true);
        buttonBoard.setVisible(false);

        slotMachine.spin();
    }

    private void onCashoutButtonPressed() {
        int score = patternDisplay.calcScore();
        scoreDisplay.addToScore(score);
        patternDisplay.clearNumbers();
    }

    private void onTargetScoreReached() {
        RoundsManager.I().nextRound();
        CreditManager.I().roundEnd();
        healthUi.healHealth();
        shop.show();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        ScreenManager.getViewport().update(width, height, true);
        ScreenManager.getUiViewport().update(width, height, true);
        vfxManager.resize(width, height);
    }

    private void onReturnedFromShop() {
        scoreDisplay.resetScore();
        drawStartingHand();
    }

    private void drawStartingHand() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Hand hand = Hand.I();
                hand.queueActions(
                    hand::drawCard,
                    hand::drawCard,
                    hand::drawCard
                );
            }
        }, 0.25f);
    }

    public int getSymbolsHitLastSpin() {
        return symbolsHitLastSpin;
    }
}
