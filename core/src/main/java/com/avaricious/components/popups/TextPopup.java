package com.avaricious.components.popups;

import com.avaricious.effects.PulseEffect;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class TextPopup {

    private final TextureRegion text;
    private final Rectangle bounds;

    private final ZIndex z;

    private final PulseEffect pulseEffect = new PulseEffect();

    public TextPopup(TextureRegion text, Rectangle bounds, ZIndex z) {
        this.text = text;
        this.bounds = bounds;
        this.z = z;
        pulseEffect.pulse();
    }

    public void update(float delta) {
        pulseEffect.update(delta);
    }

    public void draw() {
        Pencil.I().addDrawing(new TextureDrawing(text, bounds, pulseEffect.getScale(), pulseEffect.getRotation(), z));
    }

}
