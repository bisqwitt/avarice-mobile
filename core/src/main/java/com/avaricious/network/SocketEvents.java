package com.avaricious.network;

public class SocketEvents {

    private SocketEvents() {
    }

    // Matchmaking
    public static final String JOIN_QUEUE = "joinQueue";
    public static final String QUEUE_JOINED = "queueJoined";
    public static final String MATCH_FOUND = "matchFound";
    public static final String OPPONENT_LEFT = "opponentLeft";

    // Match
    public static final String ROUND_SCORE_SUBMIT = "roundScoreSubmit";
    public static final String ROUND_END_WAITING = "roundEndWaiting";
    public static final String ROUND_END_RESULT = "roundEndResult";

}
