package com.avaricious.components;

import com.avaricious.effects.IdleFloatEffect;
import com.avaricious.effects.IdleSwayEffect;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundText {

    private final List<TextureRegion> roundLetters = new ArrayList<>();

    private final Map<TextureRegion, IdleFloatEffect> letterFloatEffects = new HashMap<>();
    private final Map<TextureRegion, IdleSwayEffect> letterSwayEffects = new HashMap<>();

    public RoundText() {
        roundLetters.add(Assets.I().get(AssetKey.R));
        roundLetters.add(Assets.I().get(AssetKey.o));
        roundLetters.add(Assets.I().get(AssetKey.n));
        roundLetters.add(Assets.I().get(AssetKey.d));

        roundLetters.forEach(letter -> {
            letterFloatEffects.put(letter, new IdleFloatEffect());
            letterSwayEffects.put(letter, new IdleSwayEffect());
        });
    }

    public void draw(float delta) {
        letterFloatEffects.values().forEach(floatEffect -> floatEffect.update(delta));
        letterSwayEffects.values().forEach(swayEffect -> swayEffect.update(delta));

        roundLetters.forEach(this::drawLetter);
    }

    private void drawLetter(TextureRegion letter) {
//        Pencil.I().addDrawing(new TextureDrawing(letter,
//            new Rectangle()));
    }

}
