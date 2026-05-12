package com.avaricious;

import com.avaricious.components.texts.CreditText;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CreditScoreWithText extends CreditScore {

    private final CreditText creditText;

    public CreditScoreWithText(Vector2 staringPos, float sizeRatio, float offset) {
        super(new Rectangle(staringPos.x + 2.5f, staringPos.y, 7 / sizeRatio, 11 / sizeRatio), offset);

        creditText = new CreditText(new Vector2(staringPos.x, staringPos.y), sizeRatio, 0.1f, ZIndex.PATTERN_DISPLAY);
        creditText.setColor(Assets.I().yellow());
    }

    @Override
    public void draw(float delta) {
        super.draw(delta);
//        creditText.draw(delta);
    }

}
