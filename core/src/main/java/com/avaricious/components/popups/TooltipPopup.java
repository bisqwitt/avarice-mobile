package com.avaricious.components.popups;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.screens.ScreenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class TooltipPopup {

    private final GlyphLayout jokerTxt = new GlyphLayout();
    private final GlyphLayout description = new GlyphLayout();
    private final Vector2 pos;
    private final TextureRegion box;
    private final TextureRegion boxShadow;
    private final BitmapFont bigFont;
    private final BitmapFont smallFont;

    public TooltipPopup(String txt, Vector2 pos) {
        this.pos = pos;
        box = Assets.I().get(AssetKey.TOOLTIP_BOX);
        boxShadow = Assets.I().get(AssetKey.TOOLTIP_BOX_SHADOW);
        bigFont = Assets.I().getBigFont();
        smallFont = Assets.I().getSmallFont();
        jokerTxt.setText(bigFont, "Joker", Color.WHITE, 250f, Align.center, true);
        description.setText(smallFont, "[BLACK]" + txt + "[]", Color.WHITE, 600f, Align.top | Align.center, true);
    }

    public void render(SpriteBatch batch) {
        float boxWidth = 82 / 15f;
        float boxHeight = 41 / 15f;
        float boxX = pos.x;
        float boxY = pos.y;

        // WORLD SPACE
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
        batch.setColor(Assets.I().shadowColor());
        batch.draw(boxShadow, boxX + 0.1f, boxY - 0.1f, boxWidth, boxHeight);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(box, boxX, boxY, boxWidth, boxHeight);

        // Center of the box in world coordinates
        Vector2 center = new Vector2(boxX + boxWidth / 2f, boxY + boxHeight / 2f);

        // Project to UI screen coordinates
        ScreenManager.getViewport().project(center);

        // SWITCH TO UI SPACE
        batch.setProjectionMatrix(ScreenManager.getUiViewport().getCamera().combined);

        // --- JOKER ----
        float jokerW = jokerTxt.width;
        float jokerH = jokerTxt.height;

        float jokerX = center.x - jokerW / 2f;
        float jokerY = center.y + 55 + jokerH;
        // adjust + (boxHeight * 10) to taste

        bigFont.draw(batch, jokerTxt, jokerX, jokerY);

        // --- DESCRIPTION ----
        float textW = description.width;
        float textH = description.height;

        float textX = center.x - textW / 2f;
        float textY = center.y + textH / 2f;

        smallFont.draw(batch, description, textX - 30f, textY);

        // restore world matrix
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
    }

}
