package com.avaricious.components.popups;

import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.FontDrawing;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.Map;

public class TooltipPopup {


    private final GlyphLayout titleTxt = new GlyphLayout();
    private final GlyphLayout descriptionTxt = new GlyphLayout();

    private final float WRAP_WIDTH = 500f;

    private final Map<Integer, TextureRegion> boxes = new HashMap<>();
    private final TextureRegion boxShadow;

    private final BitmapFont bigFont;
    private final BitmapFont smallFont;

    private final Upgrade upgrade;
    private final ZIndex layer;

    private Vector2 pos;

    private float alpha = 0f;
    private boolean visible = false;

    private Runnable onDead;

    public TooltipPopup(Upgrade upgrade, Vector2 pos) {
        this(upgrade, pos, ZIndex.POPUP_DEFAULT);
    }

    public TooltipPopup(Upgrade upgrade, Vector2 pos, ZIndex layer) {
        this.upgrade = upgrade;
        this.pos = new Vector2(pos);
        this.layer = layer;

        boxes.put(1, Assets.I().get(AssetKey.TOOLTIP_BOX_S));
        boxes.put(2, Assets.I().get(AssetKey.TOOLTIP_BOX_M));
        boxes.put(3, Assets.I().get(AssetKey.TOOLTIP_BOX_L));
        boxes.put(4, Assets.I().get(AssetKey.TOOLTIP_BOX_XL));

        boxShadow = Assets.I().get(AssetKey.TOOLTIP_BOX_SHADOW);
        bigFont = Assets.I().getBigFont();
        smallFont = Assets.I().getSmallFont();
        titleTxt.setText(bigFont, upgrade.title(), Color.WHITE, WRAP_WIDTH, Align.top | Align.center, true);
        setDescription(upgrade.description());
    }

    public void update(Vector2 pos, boolean visible) {
        this.pos = new Vector2(pos);
        this.visible = visible;
        setDescription(upgrade.description());
    }

    public void render(SpriteBatch batch, float delta) {
        updateAlpha(delta);
        TextureRegion box = boxes.get(calcDescriptionLineAmount());
        float worldWidth = ScreenManager.getViewport().getWorldWidth();

        float boxWidth = 82 / 15f;
        float boxHeight = box.getRegionHeight() / 15f;
        final float boxX = pos.x < 0.25f
            ? 0.25f : pos.x + boxWidth > worldWidth - 0.25f
            ? worldWidth - boxWidth - 0.25f : pos.x;
        float boxY = pos.y;

        Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            boxShadow,
            new Rectangle(boxX, boxY - 0.2f, boxWidth, boxHeight),
            layer, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            box,
            new Rectangle(boxX, boxY, boxWidth, boxHeight),
            layer, new Color(1f, 1f, 1f, alpha)
        ));

        Vector2 center = new Vector2(boxX + boxWidth / 2f, boxY + boxHeight / 2f);
        center.set(center.x * 100, center.y * 100);

        if (alpha > 0.5f) {
            float offset = calcTextOffset();

            float jokerX = center.x - WRAP_WIDTH / 2f;
            float jokerY = center.y + 105f + offset;

            float textX = center.x - WRAP_WIDTH / 2f;
            float textY = center.y + offset;

            Pencil.I().addDrawing(new FontDrawing(
                bigFont, titleTxt, new Vector2(jokerX, jokerY), layer
            ));
            Pencil.I().addDrawing(new FontDrawing(
                smallFont, descriptionTxt, new Vector2(textX, textY), layer
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

    private int calcDescriptionLineAmount() {
        if (descriptionTxt.height > 146) return 4;
        else if (descriptionTxt.height > 90) return 3;
        else if (descriptionTxt.height > 34) return 2;
        else return 1;
    }

    private float calcTextOffset() {
        int numberOfLines = calcDescriptionLineAmount();
        return -25 + ((numberOfLines - 1) * 29);
    }

    private void setDescription(String txt) {
        descriptionTxt.setText(smallFont, "[WHITE]" + txt + "[]", Color.WHITE, WRAP_WIDTH, Align.top | Align.center, true);
    }

    public void kill(Runnable onDead) {
        this.onDead = onDead;
        visible = false;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
