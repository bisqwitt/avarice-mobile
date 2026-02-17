package com.avaricious.components.bars;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.stats.statupgrades.StatusRelic;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collections;
import java.util.List;

public class StatusUpgradeBar extends UpgradeBar {

    public StatusUpgradeBar(List<? extends Upgrade> upgrades, Rectangle bounds) {
        super(upgrades, bounds, 1.5f, false);
    }

    @Override
    protected void onUpgradeClicked(Upgrade clickedUpgrade) {
        cardBounds.keySet().retainAll(Collections.singleton(clickedUpgrade));
        cardAnimationManagers.keySet().retainAll(Collections.singleton(clickedUpgrade));

        PopupManager.I().spawnPercentage(
            ((StatusRelic) clickedUpgrade).getAdditionalPercentage(),
            Assets.I().green(),
            cardBounds.get(clickedUpgrade).getX() + 1f,
            cardBounds.get(clickedUpgrade).getY()).setOnFinished(() -> onCardClicked.run());

        ((StatusRelic) clickedUpgrade).apply();
    }

    @Override
    protected TextureRegion getTexture(Upgrade upgrade) {
        return ((StatusRelic) upgrade).getStat().getTexture();
    }

    @Override
    protected TextureRegion getShadow(Upgrade upgrade) {
        return ((StatusRelic) upgrade).getStat().getShadowTexture();
    }
}
