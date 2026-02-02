package com.avaricious.components.bars;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SlotScreenJokerBar {

    private final TextureRegion jokerTexture = Assets.I().get(AssetKey.JOKER_CARD);

    private final Map<Upgrade, Rectangle> jokerBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Rectangle> jokerAnimationManagers = new LinkedHashMap<>();

    public SlotScreenJokerBar() {

        UpgradesManager.I().onDeckChange(this::loadJokers);
    }

    private void loadJokers(List<? extends Upgrade> upgrades) {
        jokerBounds.clear();
        jokerAnimationManagers.clear();

        upgrades.forEach(upgrade -> {

        });
    }

}
