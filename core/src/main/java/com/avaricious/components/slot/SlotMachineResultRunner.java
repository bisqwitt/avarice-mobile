package com.avaricious.components.slot;

import com.avaricious.CreditManager;
import com.avaricious.TaskScheduler;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.RingBar;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.RoundInfoPanel;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.effects.EffectManager;
import com.avaricious.effects.TextureEcho;
import com.avaricious.items.upgrades.rings.triggerable.AbstractTriggerableRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.PointsPerPatternHit;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RunManager;
import com.avaricious.utility.Seq;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class SlotMachineResultRunner {

    private static SlotMachineResultRunner instance;

    public static SlotMachineResultRunner I() {
        return instance == null ? instance = new SlotMachineResultRunner() : instance;
    }

    private boolean isFirstStreakIncrease = true;

    private SlotMachineResultRunner() {
    }

    public void runResult(List<PatternHitContext> matches) {
        SlotScreen slotScreen = ScreenManager.I().getScreen(SlotScreen.class);
        SlotMachine slotMachine = SlotMachine.I();
        RingBar ringBar = RingBar.I();

        if (matches.isEmpty()) {
            if (RoundInfoPanel.I().getSpins() < 0) isFirstStreakIncrease = true;
//            buttonBoard.setVisible(true);
            slotMachine.setStale(true);
            slotScreen.onSpinButtonPressed();
            if (RunManager.I().getRoundsManager().getRoundTimer().timerEnded())
                slotScreen.onRoundEnd();
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

                    EffectManager.create(Assets.I().getSymbol(patternHitContext.getMatch().getSymbol()),
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
            slotScreen.onSpinButtonPressed();

            if (RunManager.I().getRoundsManager().getRoundTimer().timerEnded())
                slotScreen.onRoundEnd();
//            buttonBoard.setVisible(true);
        });

        scheduler.runTasks();
    }

    private void triggerSeparateSlots(List<PatternHitContext> matches, PatternHitContext patternHitContext, TaskScheduler scheduler) {
        SlotScreen slotScreen = ScreenManager.I().getScreen(SlotScreen.class);
        slotScreen.setSymbolsHitLastSpin(0);
        for (Body body : patternHitContext.getSlots()) {
            scheduler.schedule(() -> {
                slotScreen.addSymbolsHitLastSpin();

                body.pulse();
                ScreenShake.I().addTrauma(0.2f);

                boolean criticalHit = PlayerStats.I().rollChance(CriticalHitChance.class);
                int points = criticalHit ? patternHitContext.getMatch().getSymbol().baseValue() * PlayerStats.I().getStat(CriticalHitChance.class).criticalHitMultiplier() : patternHitContext.getMatch().getSymbol().baseValue();

                PopupManager.I().spawnNumber(points, Assets.I().blue(),
                    body.getPos().x + 1.5f, body.getPos().y + 1f, true);
                if (criticalHit)
                    PopupManager.I().spawnStatisticHit(PlayerStats.I().getStat(CriticalHitChance.class).getTexture(),
                        body.getPos().x + 1.5f, body.getPos().y + 2f);
                ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.POINTS, points);

                EffectManager.create(Assets.I().getSymbol(patternHitContext.getMatch().getSymbol()),
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

    public void setIsFirstStreakIncrease() {
        isFirstStreakIncrease = true;
    }

}
