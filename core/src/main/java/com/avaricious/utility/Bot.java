package com.avaricious.utility;

import com.avaricious.RoundsManager;

public class Bot {

    public static int health = 1000;

    public static int getRoundEndScore() {
        return SeededRandomizer.nextInt(100, 300) * RoundsManager.I().getCurrentRound();
    }

}
