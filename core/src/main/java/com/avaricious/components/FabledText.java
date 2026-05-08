package com.avaricious.components;

import com.avaricious.effects.IdleFloatEffect;
import com.avaricious.effects.IdleSwayEffect;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabledText {

    private final List<TextureRegion> letterTextures = new ArrayList<>();
    private final List<TextureRegion> letterShadowTextures = new ArrayList<>();

    private final Map<TextureRegion, Float> lettersX = new HashMap<>();

    private final Vector2 startingPos;
    private final float sizeRatio;
    private final float spacing;
    private final ZIndex zIndex;

    private final List<IdleFloatEffect> floatEffects = new ArrayList<>();
    private final List<IdleSwayEffect> swayEffects = new ArrayList<>();

    protected FabledText(List<TextureRegion> letterTextures, List<TextureRegion> letterShadowTextures,
                         Vector2 startingPos, float sizeRatio, float spacing, ZIndex zIndex) {
        this.letterTextures.addAll(letterTextures);
        this.letterShadowTextures.addAll(letterShadowTextures);
        this.startingPos = startingPos;
        this.sizeRatio = sizeRatio;
        this.spacing = spacing;
        this.zIndex = zIndex;

        letterTextures.forEach(letter -> {
            floatEffects.add(new IdleFloatEffect());
            swayEffects.add(new IdleSwayEffect());
        });

        calcLetterX();
    }

    public void draw(float delta) {
        floatEffects.forEach(effect -> effect.update(delta));
        swayEffects.forEach(effect -> effect.update(delta));

        for (int i = 0; i < letterTextures.size(); i++) {
            TextureRegion letter = letterTextures.get(i);
            float x = lettersX.get(letter);
            float y = startingPos.y + floatEffects.get(i).getValue();
            float width = letter.getRegionWidth() / sizeRatio;
            float height = letter.getRegionHeight() / sizeRatio;
            float rotation = swayEffects.get(i).getValue();

            Pencil.I().addDrawing(new TextureDrawing(letterShadowTextures.get(i),
                x, y - 0.1f, width, height,
                1f, rotation, zIndex, Assets.I().shadowColor()));

            Pencil.I().addDrawing(new TextureDrawing(letter,
                x, y, width, height, 1f, rotation, zIndex));
        }
    }

    private void calcLetterX() {
        lettersX.put(letterTextures.get(0), startingPos.x);
        for (int i = 1; i < letterTextures.size(); i++) {
            TextureRegion letterBefore = letterTextures.get(i - 1);
            float letterBeforeWidth = letterBefore.getRegionWidth() / sizeRatio;
            float x = lettersX.get(letterBefore) + letterBeforeWidth + spacing;
            lettersX.put(letterTextures.get(i), x);
        }
    }

}
