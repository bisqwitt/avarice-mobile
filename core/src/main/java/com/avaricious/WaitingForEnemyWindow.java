package com.avaricious;

import com.avaricious.components.texts.WaitingForOpponentToFinishTurnText;
import com.avaricious.effects.IdleFloatEffect;
import com.avaricious.effects.IdleSwayEffect;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class WaitingForEnemyWindow {

    private final WaitingForOpponentToFinishTurnText waitingForOpponentText = new WaitingForOpponentToFinishTurnText();
    private final TextureRegion dot = Assets.I().get(AssetKey.DOT_SYMBOL);

    private final List<IdleFloatEffect> floatEffects = new ArrayList<>();
    private final List<IdleSwayEffect> swayEffects = new ArrayList<>();

    private boolean open = false;

    private float loadingTimer = 0f;
    private int visibleDots = 0;
    private int dotDirection = 1;
    private final float dotInterval = 0.45f;

    public WaitingForEnemyWindow() {
        for (int i = 0; i < 3; i++) {
            floatEffects.add(new IdleFloatEffect());
            swayEffects.add(new IdleSwayEffect());
        }
    }

    public void draw(float delta) {
        if (!open) return;
        floatEffects.forEach(effect -> effect.update(delta));
        swayEffects.forEach(effect -> effect.update(delta));
        updateLoadingDots(delta);

        waitingForOpponentText.draw(delta);
        drawDot(0, 6.55f);
        drawDot(1, 7.15f);
        drawDot(2, 7.7f);
    }

    private void updateLoadingDots(float delta) {
        loadingTimer += delta;

        if (loadingTimer >= dotInterval) {
            loadingTimer = 0f;

            visibleDots += dotDirection;

            if (visibleDots >= 3) {
                visibleDots = 3;
                dotDirection = -1;
            } else if (visibleDots <= 0) {
                visibleDots = 0;
                dotDirection = 1;
            }
        }
    }

    private void drawDot(int index, float x) {
        if (index >= visibleDots) return;

        Pencil.I().addDrawing(new TextureDrawing(
            dot,
            x,
            12.5f + floatEffects.get(index).getValue(),
            7 / 15f,
            11 / 15f,
            1f,
            swayEffects.get(index).getValue(),
            ZIndex.PACK_OPENING
        ));
    }

    public void open() {
        open = true;
        loadingTimer = 0f;
        visibleDots = 1;
        dotDirection = 1;
    }

    public void close() {
        open = false;
    }

    public boolean isOpen() {
        return open;
    }
}
