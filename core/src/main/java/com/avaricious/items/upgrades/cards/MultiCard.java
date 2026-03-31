package com.avaricious.items.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MultiCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.MYSTIC_SUMMIT_CARD);

    @Override
    public String description() {
        return Assets.I().redText("+2 Multi");
    }

    @Override
    public void onApply() {
        ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.MULTI, 2);
    }

    @Override
    public IUpgradeType type() {
        return CardType.ATTACK;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(2, pos, Assets.I().red()));
    }
}
