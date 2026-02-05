package com.avaricious.screens;

import com.avaricious.Assets;
import com.avaricious.CreditManager;
import com.avaricious.CreditScore;
import com.avaricious.DevTools;
import com.avaricious.EffectManager;
import com.avaricious.Main;
import com.avaricious.ParticleManager;
import com.avaricious.ParticleType;
import com.avaricious.Profiler;
import com.avaricious.RoundsManager;
import com.avaricious.TaskScheduler;
import com.avaricious.TextureEcho;
import com.avaricious.TextureGlow;
import com.avaricious.XpBar;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.ButtonBoard;
import com.avaricious.components.CameraShaker;
import com.avaricious.components.Shop;
import com.avaricious.components.StatusUpgradeWindow;
import com.avaricious.components.bars.SlotScreenJokerBar;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.progressbar.HealthBar;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.screens.mainscreen.BackgroundLayer;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.upgrades.UpgradeWithActionAfterSpin;
import com.avaricious.upgrades.UpgradesManager;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;
import com.avaricious.upgrades.pointAdditions.PointsPerConsecutiveHit;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SymbolValueStackUpgrade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.OldTvEffect;

import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final HealthBar healthBar = new HealthBar(100f);
    private final XpBar xpBar;

    private final ScoreDisplay scoreDisplay = new ScoreDisplay();
    private final PatternDisplay patternDisplay = new PatternDisplay();

    private final StatusUpgradeWindow statusUpgradeWindow = new StatusUpgradeWindow(() -> {
//        if (scoreDisplay.targetScoreReached()) onTargetScoreReached();
    });

    private final BackgroundLayer backgroundLayer = new BackgroundLayer();

    private final ButtonBoard buttonBoard = new ButtonBoard(this::onSpinButtonPressed, this::onCashoutButtonPressed);

    private final SlotScreenJokerBar jokerBar = new SlotScreenJokerBar();

    private final CreditScore creditScore = new CreditScore(0,
        new Rectangle(1f, 7.5f, 0.32f * 1.5f, 0.56f * 1.5f), 0.35f * 1.5f);

    private final Shop shop = new Shop();

    private final VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
    private final CameraShaker cameraShaker;

    private final RoundsManager roundsManager = RoundsManager.I();
    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;

    public SlotScreen(Main app) {
        this.app = app;
        slotMachine = new SlotMachine(app.getViewport().getWorldWidth(), app.getViewport().getWorldHeight());
        xpBar = new XpBar(statusUpgradeWindow::show);

        cameraShaker = new CameraShaker(app);
        vfxManager.addEffect(new OldTvEffect());

        scoreDisplay.setOnInternalScoreDisplayed(() -> {
            AudioManager.I().endPayout();
            if (scoreDisplay.targetScoreReached()) onTargetScoreReached();
            else onSpinButtonPressed();
        });

        if (DevTools.enableProfiler) Profiler.start();
    }

    @Override
    public void show() {
        buttonBoard.setVisible(false);

        backgroundLayer.init();

        healthBar.setCurrentHealth(healthBar.getMaxHealth());
        slotMachine.getReels().get(slotMachine.getReels().size() - 1).setOnSpinFinished(this::runResult);


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                statusUpgradeWindow.show();
//                shop.show();
            }
        }, 1);

        onSpinButtonPressed();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        handleInput(delta);
        app.getViewport().apply();
        cameraShaker.render(delta);

        Camera camera = app.getViewport().getCamera();
        batch.setProjectionMatrix(camera.combined);

        // Clear the actual screen once
        Gdx.gl.glClearColor(35 / 255f, 29 / 255f, 30 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        // Clear capture buffer to transparent
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        TextureEcho.draw(batch, delta);
        ParticleManager.I().draw(batch, delta);

        slotMachine.draw(app, delta);
        scoreDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
        buttonBoard.draw(batch, delta);
        healthBar.draw(batch);
        xpBar.draw(batch);
        jokerBar.draw(batch);

        TextureGlow.draw(batch, delta, "number");
        shop.draw(batch, delta);
        statusUpgradeWindow.draw(batch, delta);
        PopupManager.I().draw(batch, delta);
        ParticleManager.I().drawTopLayer(batch, delta);
        batch.end();

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
        jokerBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {
        List<SlotMatch> matches = slotMachine.findMatches();
        if (matches.isEmpty()) {
            healthBar.damage(20);
            patternDisplay.reset();
            if (healthBar.getCurrentHealth() <= 0) {
                ScreenManager.restartGame();
            }

            onSpinButtonPressed();
            return;
        }

        TaskScheduler scheduler = new TaskScheduler(0.325f);
        scheduler.schedule(() -> slotMachine.setRunningResults(true), 0f);

        matches.forEach((slotMatch -> {
            List<Slot> slots = slotMatch.getSlots();
            Slot middleSlot = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));

            scheduler.scheduleImmediate(() -> slots.forEach(slot -> {
                slot.targetScale = 1.25f;
                slot.setInPatternHit(true);
            }));

            triggerSeparateSlots(slotMatch, scheduler);
            if (PlayerStats.I().rollChance(DoubleHitChance.class)) {
                scheduler.schedule(() -> PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(DoubleHitChance.class).getTexture(),
                    middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f));
                triggerSeparateSlots(slotMatch, scheduler);
            }

            scheduler.schedule(() -> {
                PopupManager.I().releaseHoldingNumbers();

                slots.forEach(slot -> {
                    slot.wobble();
                    slot.pulse();

                    EffectManager.create(Assets.I().getSymbol(slotMatch.getSymbol()),
                        new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                        "slot", new Color(1f, 1f, 1f, 1f));
                });

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int mult = criticalHit ? slots.size() * 2 : slots.size();

                PopupManager.I().spawnNumber(mult, Assets.I().red(),
                    middleSlot.getPos().x + ((slots.size() % 2 == 0) ? 2f : 1.5f), middleSlot.getPos().y + 1f,
                    false);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        middleSlot.getPos().x + 2.5f, middleSlot.getPos().y + 1f);
                patternDisplay.addMulti(mult);

                AudioManager.I().playHit(EffectManager.streak);
            });

            UpgradesManager.I().getUpgradesOfClass(MultAdditionUpgrade.class)
                .filter(upgrade -> upgrade.condition(null, slotMatch.getSlots().size()))
                .forEach(upgrade -> scheduler.schedule(() -> {
                    int multi = upgrade.getMulti();
                    Slot cardSlot = jokerBar.getSlotByUpgrade(upgrade);
                    cardSlot.pulse();
                    cardSlot.wobble();
                    patternDisplay.addMulti(multi);
                    PopupManager.I().spawnNumber(multi, Assets.I().red(),
                        cardSlot.getPos().x + 1.1f, cardSlot.getPos().y + 1.6f,
                        false);
                }));

            scheduler.schedule(() -> {
                EffectManager.increaseStreak();
                slots.forEach(slot -> {
                    slot.targetScale = 1f;
                    slot.setInPatternHit(false);
                });
            });
        }));

        scheduler.schedule(() -> {
            patternDisplay.addStreak(1);
            buttonBoard.setVisible(true);
//            slotMachine.setAlpha(0.25f);
            slotMachine.setRunningResults(false);
            EffectManager.endStreak();

            UpgradesManager.I().getUpgradesOfClass(UpgradeWithActionAfterSpin.class)
                .forEach(UpgradeWithActionAfterSpin::onSpinEnded);
        });

        if (DevTools.autoSpin) {
            scheduler.schedule(this::onCashoutButtonPressed);
            scheduler.schedule(this::onSpinButtonPressed);
        }
        scheduler.runTasks();
    }

    private void triggerSeparateSlots(SlotMatch slotMatch, TaskScheduler scheduler) {
        slotMatch.getSlots().forEach(slot -> {
            scheduler.schedule(() -> {
                slot.wobble();
                slot.pulse();

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int points = criticalHit ? slotMatch.getSymbol().baseValue() * 2 : slotMatch.getSymbol().baseValue();

                PopupManager.I().spawnNumber(points, Assets.I().blue(),
                    slot.getPos().x + 1.5f, slot.getPos().y + 1f, true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        slot.getPos().x + 1.5f, slot.getPos().y + 2f);
                patternDisplay.addPoints(points);

                EffectManager.create(Assets.I().getSymbol(slotMatch.getSymbol()),
                    new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                    "slot", new Color(1f, 1f, 1f, 1f));

                AudioManager.I().playHit(EffectManager.streak);

                UpgradesManager.I().getUpgradesOfClass(PointsPerConsecutiveHit.class)
                    .forEach(upgrade -> {
                        int pointAddition = upgrade.getPoints();
                        Slot jokerSlot = jokerBar.getSlotByUpgrade(upgrade);
                        jokerSlot.pulse();
                        jokerSlot.wobble();
                        patternDisplay.addPoints(pointAddition);
                        PopupManager.I().spawnNumber(pointAddition, Assets.I().blue(),
                            jokerSlot.getPos().x + 1.1f, jokerSlot.getPos().y + 1.6f, false);
                    });

                xpBar.addXp(points);
            });

            if (PlayerStats.I().rollChance(CreditSpawnChance.class)) {
                scheduler.schedule(() -> {
                    float x = slot.getPos().x + 1f;
                    float y = slot.getPos().y + 1f;
                    PopupManager.I().spawnNumber(1, Assets.I().yellow(), x, y, false);
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CreditSpawnChance.class).getTexture(), x + 1f, y);
                    CreditManager.I().gain(1);
                });
            }

            UpgradesManager.I().getUpgradesOfClass(SymbolValueStackUpgrade.class)
                .filter(upgrade -> upgrade.getSymbol() == slotMatch.getSymbol())
                .forEach(upgrade -> {
                    Slot upgradeSlot = jokerBar.getSlotByUpgrade(upgrade);
                    scheduler.scheduleImmediate(() -> {
                        upgradeSlot.wobble();
                        upgradeSlot.pulse();
                        PopupManager.I().spawnNumber(1, Assets.I().green(),
                            upgradeSlot.getPos().x + 1.1f, upgradeSlot.getPos().y + 1.6f, false);
                    });
                    if (upgrade.addStacks(1)) {
                        scheduler.schedule(() -> {
                            upgradeSlot.wobble();
                            upgradeSlot.pulse();
                            PopupManager.I().spawnNumber(1, Assets.I().blue(),
                                upgradeSlot.getPos().x + 1.1f, upgradeSlot.getPos().y + 1.6f, false);
                        });
                    }
                });
        });
    }

    private void onSpinButtonPressed() {
        slotMachine.setAlpha(1f);
        buttonBoard.setVisible(false);
        slotMachine.spin();
    }

    private void onCashoutButtonPressed() {
        int score = Math.round(patternDisplay.getPoints() * patternDisplay.getMulti() * patternDisplay.getXMulti());
        scoreDisplay.addToScore(score);
//        healthBar.heal(PlayerStats.I().getStat(Omnivamp.class).getPercentage().multiply(BigDecimal.valueOf(score)).intValue());

        patternDisplay.spawnEcho();
        patternDisplay.reset();
        buttonBoard.setVisible(false);
    }

    private void onTargetScoreReached() {
        RoundsManager.I().nextRound();
        CreditManager.I().roundEnd();
        ParticleManager.I().createTopLayer(0, 0, ParticleType.RAINBOW);
        ParticleManager.I().createTopLayer(9, 0, ParticleType.RAINBOW);
        ParticleManager.I().createTopLayer(0, 16, ParticleType.RAINBOW);
        ParticleManager.I().createTopLayer(9, 16, ParticleType.RAINBOW);
        healthBar.fullHeal();
        shop.show();
    }
}
