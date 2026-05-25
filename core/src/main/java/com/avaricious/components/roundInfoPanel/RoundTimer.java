package com.avaricious.components.roundInfoPanel;

import com.avaricious.components.DigitalNumber;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;

public class RoundTimer {

    private static RoundTimer instance;
    public static RoundTimer I() {
        return instance == null ? instance = new RoundTimer() : instance;
    }

    private final DigitalNumber roundTimer = new DigitalNumber(30, Assets.I().lightColor(), 2,
        new Rectangle(0.75f, 18.35f, 7 / 23f, 11 / 23f), 0.5f);

    private boolean timerEnded = false;

    private RoundTimer() {}

    public void draw(float delta) {
        roundTimer.draw(delta);
    }

    public void startTimer() {
        timerEnded = false;
        roundTimer.setValue(30);

        tickTimer();
    }

    private void tickTimer() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                roundTimer.setValue(roundTimer.getValue() - 1);

                if(roundTimer.getValue() == 0) onTimerEnd();
                else tickTimer();
            }
        }, 1);
    }

    private void onTimerEnd() {
        timerEnded = true;
        if(SlotMachine.I().isStale()) ScreenManager.I().getScreen(SlotScreen.class).onRoundEnd();
    }

    public boolean timerEnded() {
        return timerEnded;
    }

}
