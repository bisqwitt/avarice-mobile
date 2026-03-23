package com.avaricious.components;

import com.avaricious.CreditManager;
import com.avaricious.RoundsManager;
import com.avaricious.components.buttons.Button;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.popups.TooltipPopup;
import com.avaricious.components.slot.DragableBody;
import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.cards.AbstractCard;
import com.avaricious.upgrades.rings.AbstractRing;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BossLootWindow {

    private final Rectangle WINDOW_BOUNDS = new Rectangle(0.25f, 7.25f, 170 / 20f, 197 / 20f);

    private final TextureRegion window = Assets.I().get(AssetKey.BOSS_LOOT_WINDOW);
    private final TextureRegion bossDefeatedTxt = Assets.I().get(AssetKey.BOSS_DEFEATED_TXT);
    private final TextureRegion bossDefeatedTxtShadow = Assets.I().get(AssetKey.BOSS_DEFEATED_TXT_SHADOW);
    private final TextureRegion rewardsTxt = Assets.I().get(AssetKey.REWARDS_TXT);
    private final TextureRegion rewardsTxtShadow = Assets.I().get(AssetKey.REWARDS_TXT_SHADOW);

    private final Button sellButton = new Button(this::onSellButtonPressed,
        Assets.I().get(AssetKey.SELL_BUTTON),
        Assets.I().get(AssetKey.SELL_BUTTON_PRESSED),
        Assets.I().get(AssetKey.SELL_BUTTON),
        new Rectangle(1f, 8f, 79 / 25f, 25 / 25f), Input.Keys.BACKSPACE, ZIndex.WINDOW_ON_TOP);

    private final Button claimButton = new Button(this::onClaimButtonPressed,
        Assets.I().get(AssetKey.CLAIM_BUTTON),
        Assets.I().get(AssetKey.CLAIM_BUTTON_PRESSED),
        Assets.I().get(AssetKey.CLAIM_BUTTON),
        new Rectangle(4.9f, 8f, 79 / 25f, 25 / 25f), Input.Keys.ENTER, ZIndex.WINDOW_ON_TOP);

    private Upgrade loot;
    private boolean touchingLoot = false;
    private TooltipPopup tooltipPopup = null;

    private boolean shown = false;

    private final Runnable onWindowClosed;

    public BossLootWindow(Runnable onWindowClosed) {
        this.onWindowClosed = onWindowClosed;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        if (!shown) return;

        if (touching && !wasTouching) {
            if (loot.getBody().getBounds().contains(mouse)) {
                touchingLoot = true;
                loot.getBody().targetScale = 1.3f;
                loot.getBody().beginDrag(mouse.x, mouse.y, 0);

                tooltipPopup = PopupManager.I().createTooltip(loot, new Vector2(), ZIndex.WINDOW_ON_TOP).setVisible(true);
            }
        }

        if (touching && touchingLoot) {
            Vector2 renderPos = loot.getBody().getRenderPos(new Vector2());
            loot.getBody().dragTo(mouse.x, mouse.y, 0);
            PopupManager.I().updateTooltip(
                new Vector2(renderPos.x - 2f, renderPos.y + (loot instanceof AbstractCard ? 2.85f : 1.85f)),
                true
            );
        }

        if (!touching && wasTouching && touchingLoot) {
            loot.getBody().endDrag(0);
            loot.getBody().targetScale = 1f;
            touchingLoot = false;
            PopupManager.I().killTooltip(tooltipPopup);
        }

        sellButton.handleInput(mouse, touching, wasTouching);
        claimButton.handleInput(mouse, touching, wasTouching);
    }

    public void draw(float delta) {
        if (!shown) return;
        loot.getBody().update(delta);

        Pencil.I().addDrawing(new TextureDrawing(window, WINDOW_BOUNDS, ZIndex.WINDOW_ON_TOP));

        Pencil.I().addDrawing(new TextureDrawing(bossDefeatedTxt,
            new Rectangle(1f, 15.5f, 104 / 15f, 11 / 15f), ZIndex.WINDOW_ON_TOP));

        Pencil.I().addDrawing(new TextureDrawing(rewardsTxt,
            new Rectangle(1f, 13f, 61f / 15f, 11 / 15f), ZIndex.WINDOW_ON_TOP));

        sellButton.draw();
        claimButton.draw();
        drawLoot();
    }

    private void drawLoot() {
        DragableBody body = loot.getBody();
        Vector2 renderPos = body.getRenderPos(new Vector2());
        float width = getTextureWidth();
        float height = getTextureHeight();
        float scale = body.getScale();
        float rotation = body.getRotation();

        Pencil.I().addDrawing(new TextureDrawing(loot.shadowTexture(),
            new Rectangle(renderPos.x, renderPos.y - 0.1f, width, height),
            scale, rotation, ZIndex.WINDOW_ON_TOP, Assets.I().shadowColor()));
        Pencil.I().addDrawing(new TextureDrawing(loot.texture(),
            new Rectangle(renderPos.x, renderPos.y, width, height),
            scale, rotation, ZIndex.WINDOW_ON_TOP));
    }

    public void show() {
        shown = true;
        loot = RoundsManager.I().getBoss().loot();
        float width = getTextureWidth();
        float height = getTextureHeight();
        float x = WINDOW_BOUNDS.x + WINDOW_BOUNDS.width / 2 - width / 2f;
        float y = (WINDOW_BOUNDS.y + WINDOW_BOUNDS.height / 2 - height / 2f) - 1.15f;

        loot.addBody(new Rectangle(x, y, width, height));
    }

    private float getTextureWidth() {
        return loot instanceof AbstractRing ? 1.5f : 142 / 80f;
    }

    private float getTextureHeight() {
        return loot instanceof AbstractRing ? 1.5f : 190 / 80f;
    }

    public boolean isShown() {
        return shown;
    }

    private void onSellButtonPressed() {
        CreditManager.I().gain(loot.price());
        closeWindow();
    }

    private void onClaimButtonPressed() {
        if (loot instanceof AbstractRing) RingBar.I().addRing((AbstractRing) loot);
        else Deck.I().addCardToDeck((AbstractCard) loot);
        closeWindow();
    }

    private void closeWindow() {
        shown = false;
        onWindowClosed.run();
    }

}
