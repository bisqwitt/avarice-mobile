package com.avaricious.components;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
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

    private final Rectangle bagBounds = new Rectangle(4.75f, 0.4f, 42 / 25f, 48 / 25f);

    private final TextureRegion bagTexture = Assets.I().get(AssetKey.BAG);
    private final TextureRegion shadowTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    public void draw(SpriteBatch batch) {
        Pencil.I().addDrawing(new TextureDrawing(
            shadowTexture,
            new Rectangle(bagBounds.x - 0.2f, bagBounds.y - 0.2f, bagBounds.width + 0.4f, bagBounds.height + 0.4f),
            7, Assets.I().shadowColor()));

        Pencil.I().addDrawing(new TextureDrawing(
            bagTexture,
            bagBounds,
            7
        ));
    }

}
