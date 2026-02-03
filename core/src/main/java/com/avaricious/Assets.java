package com.avaricious;

import com.avaricious.components.slot.Symbol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Assets instance;

    public static Assets I() {
        return instance == null ? instance = new Assets() : instance;
    }

    private Assets() {
    }

    private final AssetManager manager = new AssetManager();

    private Map<AssetKey, TextureRegion> cachedTextures = new HashMap<>();
    private BitmapFont bigFont;
    private BitmapFont smallFont;

    public void load() {
        manager.load("atlases.atlas", TextureAtlas.class);
        manager.finishLoading();
        cache(manager.get("atlases.atlas", TextureAtlas.class));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/m6x11plus.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 92;
        bigFont = generator.generateFont(param);
        bigFont.setUseIntegerPositions(false);
        bigFont.getData().markupEnabled = true;
        param.size = 70;
        smallFont = generator.generateFont(param);
        smallFont.setUseIntegerPositions(false);
        smallFont.getData().markupEnabled = true;
        generator.dispose();
    }

    private void cache(TextureAtlas atlas) {
        for (AssetKey key : AssetKey.values()) {
            TextureRegion r = atlas.findRegion(key.path()); // or key.nameInAtlas
            Gdx.app.log("ATLAS", key.path());
            if (r == null) {
                throw new IllegalStateException(
                    "Missing region in atlas: key=" + key + " regionName=" + key.path()
                );
            }
            cachedTextures.put(key, r);
        }
    }


    public TextureRegion get(AssetKey key) {
        return cachedTextures.get(key);
    }

    public TextureRegion getSymbol(Symbol symbol) {
        return get(symbol.assetKey());
    }

    private static final AssetKey[] DIGITS = {
        AssetKey.ZERO_NUMBER, AssetKey.ONE_NUMBER, AssetKey.TWO_NUMBER, AssetKey.THREE_NUMBER, AssetKey.FOUR_NUMBER,
        AssetKey.FIVE_NUMBER, AssetKey.SIX_NUMBER, AssetKey.SEVEN_NUMBER, AssetKey.EIGHT_NUMBER, AssetKey.NINE_NUMBER
    };

    public TextureRegion getDigitalNumber(int number) {
        if (number < 0 || number > 9) number = 0;
        return get(DIGITS[number]);
    }

    public BitmapFont getBigFont() {
        return bigFont;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public Color blue() {
        return new Color(0.1647f, 0.5412f, 0.7843f, 1f);
    }

    public String blueText(String txt) {
        return "[#2A8AC8]" + txt + "[]";
    }

    public Color red() {
        return new Color(0.7922f, 0.3765f, 0.3333f, 1f);
    }

    public String redText(String txt) {
        return "[#CA6055]" + txt + "[]";
    }

    public Color green() {
        return new Color(0.2980f, 0.7098f, 0.4470f, 1f);
    }

    public String greenText(String txt) {
        return "[#4CB572]" + txt + "[]";
    }

    public Color yellow() {
        return new Color(218f / 255f, 172f / 255f, 83f / 255f, 1f);
    }

    public String yellowText(String txt) {
        return "[#daad53]" + txt + "[]";
    }

    public Color lightColor() {
        return new Color(0.992156f, 0.992156f, 0.992156f, 1f);
    }

}

