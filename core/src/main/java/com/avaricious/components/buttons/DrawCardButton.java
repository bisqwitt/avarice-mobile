package com.avaricious.components.buttons;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Rectangle;

public class DrawCardButton extends DisablableButton {

    public DrawCardButton(Runnable onButtonPressedRunnable, Rectangle buttonRectangle, int key) {
        super(onButtonPressedRunnable,
            Assets.I().get(AssetKey.DRAW_CARD_BUTTON),
            Assets.I().get(AssetKey.DRAW_CARD_BUTTON_PRESSED),
            Assets.I().get(AssetKey.DRAW_CARD_BUTTON),
            buttonRectangle, key, ZIndex.BUTTON_BOARD);
    }

    @Override
    boolean disabled() {
        return false;
    }
}
