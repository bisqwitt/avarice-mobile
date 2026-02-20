package com.avaricious.components.popups;

import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Upgrade;
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

    private final TextureRegion box;
    private final TextureRegion boxShadow;

    private final BitmapFont bigFont;
    private final BitmapFont smallFont;

    private final Upgrade upgrade;

    private Vector2 pos;

    private float alpha = 0f;
    private boolean visible = false;

    private Runnable onDead;

    public TooltipPopup(Upgrade upgrade, Vector2 pos) {
        this.upgrade = upgrade;
        this.pos = new Vector2(pos);
        box = Assets.I().get(AssetKey.TOOLTIP_BOX);
        boxShadow = Assets.I().get(AssetKey.TOOLTIP_BOX_SHADOW);
        bigFont = Assets.I().getBigFont();
        smallFont = Assets.I().getSmallFont();
        jokerTxt.setText(bigFont, "Joker", Color.WHITE, 250f, Align.top | Align.center, true);
        setDescription(upgrade.description());
    }

    public void update(Vector2 pos, boolean visible) {
        this.pos = new Vector2(pos);
        this.visible = visible;
        setDescription(upgrade.description());
    }

    public void render(SpriteBatch batch, float delta) {
        updateAlpha(delta);

        float boxWidth = 82 / 15f;
        float boxHeight = 41 / 15f;
        float originX = boxWidth / 2f;
        float originY = boxHeight / 2f;
        float boxX = pos.x;
        float boxY = pos.y;

        float worldWidth = ScreenManager.getViewport().getWorldWidth();
        if (boxX < 0.25f) boxX = 0.25f;
        else if (boxX + boxWidth > worldWidth - 0.25f) boxX = worldWidth - boxWidth - 0.25f;

        // WORLD SPACE
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
        Color shadowColor = Assets.I().shadowColor();
        batch.setColor(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha));
        batch.draw(boxShadow,
            boxX, boxY - 0.2f,
            originX, originY,
            boxWidth, boxHeight,
            1f, 1f,
            0);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(box,
            boxX, boxY,
            originX, originY,
            boxWidth, boxHeight,
            1f, 1f,
            0);
        batch.setColor(1f, 1f, 1f, 1f);

        Vector2 center = new Vector2(boxX + boxWidth / 2f, boxY + boxHeight / 2f);
        ScreenManager.getViewport().project(center);

        if (alpha > 0.5f) {
            batch.setProjectionMatrix(ScreenManager.getUiViewport().getCamera().combined);

            float jokerX = center.x - jokerTxt.width / 2f - 30f;
            float jokerY = center.y + 130f;

            float textW = description.width;
            float textH = description.height;

            float textX = center.x - textW / 2f;
            float textY = center.y;

            batch.setColor(1f, 1f, 1f, alpha);
            bigFont.draw(batch, jokerTxt, jokerX, jokerY);
            smallFont.draw(batch, description, textX, textY);
            batch.setColor(1f, 1f, 1f, 1f);
            // restore world matrix
            batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
        }
    }

    private float getTargetAlpha() {
        return visible ? 1f : 0f;
    }

    private void updateAlpha(float delta) {
        float targetAlpha = getTargetAlpha();
        float responsiveness = 12f; // higher = snappier
        alpha += (targetAlpha - alpha) * (1f - (float) Math.exp(-responsiveness * delta));
        // optional: snap when extremely close
        if (Math.abs(targetAlpha - alpha) < 0.001f) {
            alpha = targetAlpha;
            if (onDead != null) onDead.run();
        }
    }

    public void kill(Runnable onDead) {
        this.onDead = onDead;
        visible = false;
    }

    private void setDescription(String txt) {
        description.setText(smallFont, "[BLACK]" + txt + "[]", Color.WHITE, 600f, Align.top | Align.center, true);
    }
}
