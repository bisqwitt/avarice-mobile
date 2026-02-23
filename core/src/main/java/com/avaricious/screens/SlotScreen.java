package com.avaricious.screens;

import com.avaricious.CreditManager;
import com.avaricious.CreditScore;
import com.avaricious.DevTools;
import com.avaricious.Main;
import com.avaricious.Profiler;
import com.avaricious.RoundsManager;
import com.avaricious.TaskScheduler;
import com.avaricious.TextureEcho;
import com.avaricious.XpBar;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.ButtonBoard;
import com.avaricious.components.CameraShaker;
import com.avaricious.components.DeckUi;
import com.avaricious.components.HandUi;
import com.avaricious.components.Shop;
import com.avaricious.components.StatusUpgradeWindow;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.progressbar.Health;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.effects.EffectManager;
import com.avaricious.effects.RainbowBorderPulseMesh;
import com.avaricious.effects.TextureGlow;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.effects.particle.ParticleType;
import com.avaricious.screens.mainscreen.BackgroundLayer;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.RelicWithActionAfterSpin;
import com.avaricious.upgrades.multAdditions.MultAdditionRelic;
import com.avaricious.upgrades.pointAdditions.PointsPerConsecutiveHit;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SymbolValueStackRelic;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.OldTvEffect;

import java.util.List;

public class SlotScreen extends ScreenAdapter {

    private final Main app;
    private final SlotMachine slotMachine;
    private final XpBar xpBar;
    private final Health health = Health.I();

    private final ScoreDisplay scoreDisplay = new ScoreDisplay();
    private final PatternDisplay patternDisplay = PatternDisplay.I();

    private final StatusUpgradeWindow statusUpgradeWindow = new StatusUpgradeWindow(() -> {
    });

    private final BackgroundLayer backgroundLayer = new BackgroundLayer();

    private final ButtonBoard buttonBoard = new ButtonBoard(this::onSpinButtonPressed, this::onCashoutButtonPressed);
    private final HandUi handUi = new HandUi();
    private final DeckUi deckUi = new DeckUi();

    private final TextureRegion blackBluePixel = Assets.I().get(AssetKey.BLACK_BLUE_PIXEL);

    private final CreditScore creditScore = new CreditScore(0,
        new Rectangle(1f, 7.5f, 0.32f * 1.5f, 0.56f * 1.5f), 0.35f * 1.5f);

    private final Shop shop = new Shop(this::onReturnedFromShop);

    private final VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
    private final CameraShaker cameraShaker;

    private final RoundsManager roundsManager = RoundsManager.I();
    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;
    private int symbolsHitLastSpin = 0;

    public SlotScreen(Main app) {
        this.app = app;
        slotMachine = SlotMachine.I();
        xpBar = new XpBar(statusUpgradeWindow::show);

        cameraShaker = new CameraShaker(app);
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
        SpriteBatch batch = app.getBatch();
        handleInput(delta);
        app.getViewport().apply();
        cameraShaker.render(delta);

        Camera camera = app.getViewport().getCamera();
        batch.setProjectionMatrix(camera.combined);

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        // Clear capture buffer to transparent
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        TextureEcho.draw(batch, delta);

        batch.draw(blackBluePixel, 0, 6, 10, 10);

        ParticleManager.I().draw(batch, delta);

        slotMachine.draw(app, delta);
        scoreDisplay.draw(batch, delta);
        patternDisplay.draw(batch, delta);
        buttonBoard.draw(batch, delta);
        health.draw(batch);
        xpBar.draw(batch);
//        jokerBar.draw(batch, delta);
        deckUi.draw(batch);

        ParticleManager.I().drawBehindCardLayer(batch, delta);

        handUi.draw(batch, delta);

        TextureGlow.draw(batch, delta, TextureGlow.Type.NUMBER);
        shop.draw(batch, delta);
        statusUpgradeWindow.draw(batch, delta);
        PopupManager.I().draw(batch, delta);
        ParticleManager.I().drawTopLayer(batch, delta);

        RainbowBorderPulseMesh.I().render(batch, delta);
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
//        jokerBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        handUi.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {
        List<SlotMatch> matches = slotMachine.findMatches();
        if (matches.isEmpty()) {
            health.damage(20);
            return;
        }

        TaskScheduler scheduler = new TaskScheduler(0.4f);
        scheduler.schedule(() -> slotMachine.setRunningResults(true), 0f);

        for (SlotMatch slotMatch : matches) {
            List<Slot> slots = slotMatch.getSlots();
            Slot middleSlot = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));

            scheduler.scheduleImmediate(() -> {
                for (Slot slot : slots) {
                    slot.targetScale = 1.25f;
                    slot.setInPatternHit(true);
                }
            });

            triggerSeparateSlots(slotMatch, scheduler);
            if (PlayerStats.I().rollChance(DoubleHitChance.class)) {
                scheduler.schedule(() -> PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(DoubleHitChance.class).getTexture(),
                    middleSlot.getPos().x + 1f, middleSlot.getPos().y + 1f));
                triggerSeparateSlots(slotMatch, scheduler);
            }

            scheduler.schedule(() -> {
                PopupManager.I().releaseHoldingNumbers();

                for (Slot slot : slots) {
                    slot.pulse();
                    slot.wobble();

                    EffectManager.create(Assets.I().getSymbol(slotMatch.getSymbol()),
                        new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                        TextureGlow.Type.SLOT, Assets.I().red());
                }

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int mult = criticalHit ? slots.size() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : slots.size();

                PopupManager.I().spawnNumber(mult, Assets.I().red(),
                    middleSlot.getPos().x + ((slots.size() % 2 == 0) ? 2f : 1.5f), middleSlot.getPos().y + 1f,
                    true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        middleSlot.getPos().x + 2.5f, middleSlot.getPos().y + 1f);
                patternDisplay.addMulti(mult);

                AudioManager.I().playHit(EffectManager.streak);
            });

            MultAdditionRelic multAdditionUpgrade = Hand.I().getUpgradeOfClass(MultAdditionRelic.class);
            if (multAdditionUpgrade != null && multAdditionUpgrade.condition(null, slotMatch.getSlots().size())) {
                int multi = multAdditionUpgrade.getMulti();
//                Slot cardSlot = jokerBar.getSlotByUpgrade(multAdditionUpgrade);
//                cardSlot.pulse();
//                cardSlot.wobble();
//                patternDisplay.addMulti(multi);
//                PopupManager.I().spawnNumber(multi, Assets.I().red(),
//                    cardSlot.getPos().x + 1.1f, cardSlot.getPos().y + 1.6f,
//                    false);
            }

            scheduler.schedule(() -> {
                EffectManager.increaseStreak();
                for (Slot slot : slots) {
                    slot.targetScale = 1f;
                    slot.setInPatternHit(false);
                    PopupManager.I().releaseHoldingNumbers();
                }
            });
        }

        scheduler.schedule(() -> {
            patternDisplay.addStreak(1);
//            slotMachine.setAlpha(0.25f);
            slotMachine.setRunningResults(false);
            EffectManager.endStreak();

            for (RelicWithActionAfterSpin relicWithActionAfterSpin : Hand.I().getUpgradesOfClass(RelicWithActionAfterSpin.class)) {
                relicWithActionAfterSpin.onSpinEnded();
            }
        });

        if (DevTools.autoSpin) {
            scheduler.schedule(this::onCashoutButtonPressed);
            scheduler.schedule(this::onSpinButtonPressed);
        }
        scheduler.runTasks();
    }

    private void triggerSeparateSlots(SlotMatch slotMatch, TaskScheduler scheduler) {
        symbolsHitLastSpin = 0;
        for (Slot slot : slotMatch.getSlots()) {
            scheduler.schedule(() -> {
                symbolsHitLastSpin++;

                slot.pulse();
                slot.wobble();

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int points = criticalHit ? slotMatch.getSymbol().baseValue() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : slotMatch.getSymbol().baseValue();

                PopupManager.I().spawnNumber(points, Assets.I().blue(),
                    slot.getPos().x + 1.5f, slot.getPos().y + 1f, true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        slot.getPos().x + 1.5f, slot.getPos().y + 2f);
                patternDisplay.addPoints(points);

                EffectManager.create(Assets.I().getSymbol(slotMatch.getSymbol()),
                    new Rectangle(slot.getPos().x, slot.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                    TextureGlow.Type.SLOT, Assets.I().blue());

                AudioManager.I().playHit(EffectManager.streak);

                PointsPerConsecutiveHit pointsPerConsecutiveHitUpgrade = Hand.I().getUpgradeOfClass(PointsPerConsecutiveHit.class);
                if (pointsPerConsecutiveHitUpgrade != null) {
                    int pointAddition = pointsPerConsecutiveHitUpgrade.getPoints();
//                    Slot jokerSlot = jokerBar.getSlotByUpgrade(pointsPerConsecutiveHitUpgrade);
//                    jokerSlot.pulse();
//                    jokerSlot.wobble();
//                    patternDisplay.addPoints(pointAddition);
//                    PopupManager.I().spawnNumber(pointAddition, Assets.I().blue(),
//                        jokerSlot.getPos().x + 1.1f, jokerSlot.getPos().y + 1.6f, false);
                }

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

            List<SymbolValueStackRelic> symbolValueStackUpgrades = Hand.I().getUpgradesOfClass(SymbolValueStackRelic.class);
            SymbolValueStackRelic symbolValueStackUpgrade = null;
            for (SymbolValueStackRelic upgrade : symbolValueStackUpgrades) {
                if (upgrade.getSymbol() == slotMatch.getSymbol()) {
                    symbolValueStackUpgrade = upgrade;
                    break;
                }
            }
            if (symbolValueStackUpgrade != null) {
//                Slot upgradeSlot = jokerBar.getSlotByUpgrade(symbolValueStackUpgrade);
//                scheduler.scheduleImmediate(() -> {
//                    upgradeSlot.pulse();
//                    upgradeSlot.wobble();
//                    PopupManager.I().spawnNumber(1, Assets.I().green(),
//                        upgradeSlot.getPos().x + 1.1f, upgradeSlot.getPos().y + 1.6f, false);
//                });
//                if (symbolValueStackUpgrade.addStacks(1)) {
//                    scheduler.schedule(() -> {
//                        upgradeSlot.pulse();
//                        upgradeSlot.wobble();
//                        PopupManager.I().spawnNumber(1, Assets.I().blue(),
//                            upgradeSlot.getPos().x + 1.1f, upgradeSlot.getPos().y + 1.6f, false);
//                    });
//                }
            }
        }
    }

    private void onSpinButtonPressed() {
        slotMachine.setAlpha(1f);
        buttonBoard.setSlotMachineIsSpinning(true);

        slotMachine.spin();
    }

    private void onCashoutButtonPressed() {
        int score = Math.round(patternDisplay.getPoints() * patternDisplay.getMulti() * patternDisplay.getXMulti());
        scoreDisplay.addToScore(score);
//        healthBar.heal(PlayerStats.I().getStat(Omnivamp.class).getPercentage().multiply(BigDecimal.valueOf(score)).intValue());

        patternDisplay.spawnEcho();
        patternDisplay.reset();
    }

    private void onTargetScoreReached() {
        RoundsManager.I().nextRound();
        CreditManager.I().roundEnd();
        ParticleManager.I().createTopLayer(0, 0, ParticleType.RAINBOW);
        ParticleManager.I().createTopLayer(9, 0, ParticleType.RAINBOW);
        ParticleManager.I().createTopLayer(0, 16, ParticleType.RAINBOW);
        ParticleManager.I().createTopLayer(9, 16, ParticleType.RAINBOW);
        health.getHealthBar().fullHeal();
        shop.show();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
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
