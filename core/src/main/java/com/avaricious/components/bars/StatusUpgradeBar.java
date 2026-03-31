package com.avaricious.components.bars;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.rings.applicable.StatusRing;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collections;
import java.util.List;

public class StatusUpgradeBar extends UpgradeBar {

    public StatusUpgradeBar(List<? extends AbstractUpgrade> upgrades, Rectangle bounds) {
        super(upgrades, bounds, 1.5f, false);
    }

    @Override
    protected void onUpgradeClicked(AbstractUpgrade clickedUpgrade) {
        cardBounds.keySet().retainAll(Collections.singleton(clickedUpgrade));
//        cards.keySet().retainAll(Collections.singleton(clickedUpgrade));

        PopupManager.I().spawnPercentage(
            ((StatusRing) clickedUpgrade).getAdditionalPercentage(),
            Assets.I().green(),
            cardBounds.get(clickedUpgrade).getX() + 1f,
            cardBounds.get(clickedUpgrade).getY()).setOnFinished(() -> onCardClicked.run());

        ((StatusRing) clickedUpgrade).apply();
    }

    @Override
    protected TextureRegion getTexture(AbstractUpgrade upgrade) {
        return ((StatusRing) upgrade).getStat().getTexture();
    }

    @Override
    protected TextureRegion getShadow(AbstractUpgrade upgrade) {
        return ((StatusRing) upgrade).getStat().getShadowTexture();
    }
}
