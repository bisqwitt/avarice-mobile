package com.avaricious;

public class DevTools {

    private static final boolean active = true;

    public static boolean audioMuted() {
        return active && false;
    }

    public static boolean enableProfiler() {
        return active && false;
    }

    public static boolean testRings() {
        return active && false;
    }

    public static boolean allCardsInDeck() {
        return active && false;
    }

    public static boolean unlimitedMoney() {
        return active && true;
    }

    public static boolean deleteGameStateSaveFile() {
        return active && false;
    }

    public static boolean showMouseLocation() {
        return active && false;
    }

    public static boolean opponentDefaultValues() {
        return active && true;
    }

    public static boolean rewriteSaveFile() {
        return active && true;
    }

}
