package com.avaricious.components.popups;

import com.avaricious.screens.ScreenManager;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
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
        jokerTxt.setText(bigFont, "Joker", Color.WHITE, 250f, Align.top | Align.center, true);
        description.setText(smallFont, "[BLACK]" + txt + "[]", Color.WHITE, 600f, Align.top | Align.center, true);
    }

    public void render(SpriteBatch batch) {
        float boxWidth = 82 / 15f;
        float boxHeight = 41 / 15f;
        float boxX = pos.x;
        float boxY = pos.y;

        float worldWidth = ScreenManager.getViewport().getWorldWidth();
        if (boxX < 0) boxX = 0.25f;
        else if (boxX + boxWidth > worldWidth) boxX = worldWidth - boxWidth - 0.25f;

        // WORLD SPACE
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
        batch.setColor(Assets.I().shadowColor());
        batch.draw(boxShadow, boxX, boxY - 0.2f, boxWidth, boxHeight);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(box, boxX, boxY, boxWidth, boxHeight);

        Vector2 center = new Vector2(boxX + boxWidth / 2f, boxY + boxHeight / 2f);
        ScreenManager.getViewport().project(center);

        batch.setProjectionMatrix(ScreenManager.getUiViewport().getCamera().combined);

        float jokerX = center.x - jokerTxt.width / 2f - 30f;
        float jokerY = center.y + 130f;

        bigFont.draw(batch, jokerTxt, jokerX, jokerY);

        // --- DESCRIPTION ----
        float textW = description.width;
        float textH = description.height;

        float textX = center.x - textW / 2f;
        float textY = center.y;

        smallFont.draw(batch, description, textX, textY);

        // restore world matrix
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
    }

}
