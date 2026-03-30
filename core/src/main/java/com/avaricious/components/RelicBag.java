package com.avaricious.components;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class RelicBag {

    private static RelicBag instance;

    public static RelicBag I() {
        return instance == null ? instance = new RelicBag() : instance;
    }

    private RelicBag() {
    }

    private final Rectangle bagBounds = new Rectangle(7.2f, 5.75f, 42 / 30f, 48 / 30f);

    private final TextureRegion bagTexture = Assets.I().get(AssetKey.BAG);

    public void draw(SpriteBatch batch) {
        Pencil.I().addDrawing(new TextureDrawing(
            bagTexture,
            bagBounds,
            ZIndex.RELIC_BAG
        ));
    }
}
