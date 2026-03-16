package com.avaricious;

import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;

public class CreditScore extends CreditNumber {

    public CreditScore(int initialScore, Rectangle rectangle, float offset) {
        super(initialScore, rectangle, offset);

        CreditManager.I().onChange(this::setScore);
        setZIndex(ZIndex.CREDIT_SCORE);
    }
}
