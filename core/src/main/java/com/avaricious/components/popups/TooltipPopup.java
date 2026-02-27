package com.avaricious.components.popups;

import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.FontDrawing;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
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
        float worldWidth = ScreenManager.getViewport().getWorldWidth();

        float boxWidth = 82 / 15f;
        float boxHeight = 41 / 15f;
        float originX = boxWidth / 2f;
        float originY = boxHeight / 2f;
        final float boxX = pos.x < 0.25f
            ? 0.25f : pos.x + boxWidth > worldWidth - 0.25f
            ? worldWidth - boxWidth - 0.25f : pos.x;
        float boxY = pos.y;

        // WORLD SPACE
        batch.setProjectionMatrix(ScreenManager.getViewport().getCamera().combined);
        Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            boxShadow,
            new Rectangle(boxX, boxY - 0.2f, boxWidth, boxHeight),
            1f, 0f, 16, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            box,
            new Rectangle(boxX, boxY, boxWidth, boxHeight),
            1f, 0f, 16, new Color(1f, 1f, 1f, alpha)
        ));

        Vector2 center = new Vector2(boxX + boxWidth / 2f, boxY + boxHeight / 2f);
        ScreenManager.getViewport().project(center);

        if (alpha > 0.5f) {
            float jokerX = center.x - jokerTxt.width / 2f - 30f;
            float jokerY = center.y + 130f;

            float textW = description.width;
            float textH = description.height;

            float textX = center.x - textW / 2f;
            float textY = center.y;

            Pencil.I().addDrawing(new FontDrawing(
                bigFont, jokerTxt, new Vector2(jokerX, jokerY), 16
            ));
            Pencil.I().addDrawing(new FontDrawing(
                smallFont, description, new Vector2(textX, textY), 16
            ));
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
