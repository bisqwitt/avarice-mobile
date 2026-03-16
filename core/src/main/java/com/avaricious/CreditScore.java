package com.avaricious;

import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;

public class CreditScore extends CreditNumber {

    private final AutoCloseable creditListener;

    public CreditScore(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, rectangle, offset);

        creditListener = CreditManager.I().onChange(this::setScore);
    }

    @Override
    protected ZIndex getLayer() {
        return ZIndex.CREDIT_SCORE;
    }
}
