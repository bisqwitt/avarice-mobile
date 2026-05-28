package com.avaricious.screens;

import com.avaricious.CreditManager;
import com.avaricious.DevTools;
import com.avaricious.Main;
import com.avaricious.Profiler;
import com.avaricious.TaskScheduler;
import com.avaricious.audio.AudioManager;
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
import com.avaricious.components.slot.Body;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.components.texts.WaitingForOpponentToFinishRoundText;
import com.avaricious.effects.EffectManager;
import com.avaricious.effects.TextureEcho;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.IUpgradeWithActionOnSpinButtonPressed;
import com.avaricious.items.upgrades.rings.OneMoreCardAtStartOfRoundRing;
import com.avaricious.items.upgrades.rings.triggerable.AbstractTriggerableRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.PointsPerPatternHit;
import com.avaricious.network.NetworkController;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.GameContext;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RunManager;
import com.avaricious.utility.Seq;
import com.avaricious.utility.runData.RunDataFileManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
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

    private final ScreenShake screenShake;
    private final Shop shop = new Shop(this::onReturnedFromShop);

    private final ButtonBoard buttonBoard = ButtonBoard.I()
        .init(this::onSpinButtonPressed, this::onPlayButtonPressed);

    private final TextureRegion charcoalPixel = Assets.I().get(AssetKey.CHARCOAL_PIXEL);


    private final VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

    private final Vector2 mouse = new Vector2();
    private boolean leftClickWasPressed = false;
    private int symbolsHitLastSpin = 0;
    private boolean isFirstStreakIncrease = true;

    private final WaitingForOpponentToFinishRoundText waitingForOpponentText = new WaitingForOpponentToFinishRoundText();
    private boolean showWaitingForOpponentText = false;

    public SlotScreen(Main app) {
        this.app = app;
        Pencil.I().setBatch(app.getBatch());

        screenShake = ScreenShake.I().setCameras(app.getViewport().getCamera(), app.getUiViewport().getCamera());
        vfxManager.addEffect(new OldTvEffect());
        SlotMachine.I().setOnLastReelFinished(this::runResult);

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
//        bossLootWindow.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        if (shop.isShowing()) shop.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        else if (!bossLootWindow.isShown()) {
        RingBar.I().handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        if (!buttonBoard.handleInput(mouse, leftClickPressed, leftClickWasPressed))
            HandUi.I().handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        }
//        backgroundLayer.handleInput();
//        jokerBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        deckUi.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        ItemBag.I().handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);

        leftClickWasPressed = leftClickPressed;
    }

    private void runResult() {
        SlotMachine slotMachine = SlotMachine.I();
        RingBar ringBar = RingBar.I();

        List<PatternHitContext> matches = slotMachine.findMatches();
        if (matches.isEmpty()) {
            if (RoundInfoPanel.I().getSpins() < 0) isFirstStreakIncrease = true;
//            buttonBoard.setVisible(true);
            slotMachine.setStale(true);
            onSpinButtonPressed();
            if (RunManager.I().getRoundsManager().getRoundTimer().timerEnded()) onRoundEnd();
            return;
        }

        TaskScheduler scheduler = TaskScheduler.I();
        scheduler.schedule(() -> slotMachine.setRunningResults(true), 0f);

        for (PatternHitContext patternHitContext : matches) {
            List<Body> slots = patternHitContext.getSlots();
            Body middleBody = slots.get(slots.size() / 2 - (slots.size() % 2 == 0 ? 1 : 0));

            scheduler.scheduleNoDelay(() -> {
                if (ringBar.ringOwned(PointsPerPatternHit.class))
                    ringBar.getRingByClass(PointsPerPatternHit.class).onPatternHit();

                for (Body body : slots) {
                    body.beginPatternHit();
                }
            });

            triggerSeparateSlots(matches, patternHitContext, scheduler);
            if (PlayerStats.I().rollChance(DoubleHitChance.class)) {
                scheduler.schedule(() -> PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(DoubleHitChance.class).getTexture(),
                    middleBody.getPos().x + 1f, middleBody.getPos().y + 1f));
                triggerSeparateSlots(matches, patternHitContext, scheduler);
            }

            scheduler.schedule(() -> {
                PopupManager.I().releaseHoldingNumbers();

                for (Body body : slots) {
                    body.pulse();

                    EffectManager.create(Assets.I().getSymbol(patternHitContext.getSymbol()),
                        new Rectangle(body.getPos().x, body.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                        TextureEcho.Type.SLOT);
                }
                ScreenShake.I().addTrauma(0.3f);

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int multi = criticalHit ? slots.size() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : slots.size();

                PopupManager.I().spawnNumber(multi, Assets.I().red(),
                    middleBody.getPos().x + ((slots.size() % 2 == 0) ? 2f : 1.5f), middleBody.getPos().y + 1f,
                    true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        middleBody.getPos().x + 2.5f, middleBody.getPos().y + 1f);

                ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.MULTI, multi);

                AudioManager.I().playHit(EffectManager.streak);
            });

            Seq.of(ringBar.getRingsOfType(AbstractTriggerableRing.class))
                .filter(ring -> ring.triggerableOn() == AbstractTriggerableRing.TriggerablePer.PATTERN)
                .forEach(ring -> ring.scheduleTrigger(matches, patternHitContext, false));

            scheduler.schedule(() -> {
                if (matches.indexOf(patternHitContext) != matches.size() - 1)
                    EffectManager.increaseStreak();
                for (Body body : slots) {
                    body.endPatternHit();
                    PopupManager.I().releaseHoldingNumbers();
                }
            });
        }

        int index = 0;
        for (AbstractTriggerableRing triggerableRing : ringBar.getRingsOfType(AbstractTriggerableRing.class)) {
            if (triggerableRing.triggerableOn() == AbstractTriggerableRing.TriggerablePer.SPIN)
                triggerableRing.scheduleTrigger(matches, null, index == 0);
            index++;
        }

        scheduler.schedule(() -> {
            if (isFirstStreakIncrease) isFirstStreakIncrease = false;
            else
                ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.STREAK, 0.25f);
            slotMachine.setRunningResults(false);
            slotMachine.setStale(true);
            EffectManager.endStreak();
            onSpinButtonPressed();

            if (RunManager.I().getRoundsManager().getRoundTimer().timerEnded()) onRoundEnd();
//            buttonBoard.setVisible(true);
        });

        scheduler.runTasks();
    }

    private void triggerSeparateSlots(List<PatternHitContext> matches, PatternHitContext patternHitContext, TaskScheduler scheduler) {
        symbolsHitLastSpin = 0;
        for (Body body : patternHitContext.getSlots()) {
            scheduler.schedule(() -> {
                symbolsHitLastSpin++;

                body.pulse();
                ScreenShake.I().addTrauma(0.2f);

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int points = criticalHit ? patternHitContext.getSymbol().baseValue() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : patternHitContext.getSymbol().baseValue();

                PopupManager.I().spawnNumber(points, Assets.I().blue(),
                    body.getPos().x + 1.5f, body.getPos().y + 1f, true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        body.getPos().x + 1.5f, body.getPos().y + 2f);
                ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.POINTS, points);

                EffectManager.create(Assets.I().getSymbol(patternHitContext.getSymbol()),
                    new Rectangle(body.getPos().x, body.getPos().y, SlotMachine.CELL_W, SlotMachine.CELL_H),
                    TextureEcho.Type.SLOT);

                AudioManager.I().playHit(EffectManager.streak);
            });

            Seq.of(RingBar.I().getRingsOfType(AbstractTriggerableRing.class))
                .filter(ring -> ring.triggerableOn() == AbstractTriggerableRing.TriggerablePer.SLOT)
                .forEach(ring -> ring.scheduleTrigger(matches, patternHitContext, true));

            if (PlayerStats.I().rollChance(CreditSpawnChance.class)) {
                scheduler.schedule(() -> {
                    float x = body.getPos().x + 1f;
                    float y = body.getPos().y + 1f;
                    PopupManager.I().spawnNumber(1, Assets.I().yellow(), x, y, false);
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CreditSpawnChance.class).getTexture(), x + 1f, y);
                    CreditManager.I().gain(1);
                });
            }
        }
    }

    public void onPlayButtonPressed() {
        HandUi.I().applySelectedCard();
    }

    public void onSpinButtonPressed() {
        if (RoundInfoPanel.I().getSpins() == 0 || !SlotMachine.I().isStale()) return;

        SlotMachine.I().setAlpha(1f);
        SlotMachine.I().spin();
        RoundInfoPanel.I().minusSpin();

        for (IUpgradeWithActionOnSpinButtonPressed relicWithActionAfterSpin : Hand.I().getUpgradesOfClass(IUpgradeWithActionOnSpinButtonPressed.class)) {
            relicWithActionAfterSpin.onSpinButtonPressed();
        }
    }

    public void onRoundEnd() {
        isFirstStreakIncrease = true;

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
}
