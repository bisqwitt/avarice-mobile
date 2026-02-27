package com.avaricious.components;

import com.avaricious.CreditManager;
import com.avaricious.CreditScore;
import com.avaricious.components.bars.JokerUpgradeBarWithPrices;
import com.avaricious.components.buttons.Button;
import com.avaricious.upgrades.Deck;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shop {

    private final float WINDOW_X = 0.7f;
    private final float WINDOW_Y = 6.6f;

    private final TextureRegion window = Assets.I().get(AssetKey.SHOP_WINDOW);
    private final TextureRegion windowShadow = Assets.I().get(AssetKey.SHOP_WINDOW_SHADOW);
    private final TextureRegion shopTxt = Assets.I().get(AssetKey.SHOP_TXT);
    private final TextureRegion shopTxtShadow = Assets.I().get(AssetKey.SHOP_TXT_SHADOW);

    private final Button returnButton;
    private final Button rerollButton;

    private final CreditScore creditScore;
    private final JokerUpgradeBarWithPrices shopCardsBar;

    private enum State {HIDDEN, ENTERING, SHOWN, EXITING}

    private State state = State.HIDDEN;

    private float animT = 0f;

    private static final float ENTER_DUR = 0.18f;
    private static final float EXIT_DUR = 0.14f;

    private float winScale = 1f;
    private float winAlpha = 1f;
    private float winYOffset = 0f;

    public Shop(Runnable onReturnedFromShop) {
        creditScore = new CreditScore(0,
            new Rectangle(WINDOW_X + 1.25f, WINDOW_Y + 2.2f, 0.32f, 0.56f), 0.35f);
        shopCardsBar = new JokerUpgradeBarWithPrices(Deck.I().randomUpgrades(), new Rectangle(
            WINDOW_X + 1f, WINDOW_Y + 3.5f, 142 / 100f, 190 / 100f),
            2f, false);

        rerollButton = new Button(() -> {
            if (CreditManager.I().enoughCredit(3)) {
                shopCardsBar.loadUpgrades(Deck.I().randomUpgrades());
                CreditManager.I().pay(3);
            }
        },
            Assets.I().get(AssetKey.REROLL_BUTTON), Assets.I().get(AssetKey.REROLL_BUTTON_PRESSED), Assets.I().get(AssetKey.REROLL_BUTTON),
            new Rectangle(WINDOW_X + 0.9f, WINDOW_Y + 0.75f, 79 / 30f, 25 / 30f), Input.Keys.SPACE);
        rerollButton.setShowShadow(false);

        returnButton = new Button(() -> {
            state = State.EXITING;
            animT = 0f;
            onReturnedFromShop.run();
        },
            Assets.I().get(AssetKey.RETURN_BUTTON), Assets.I().get(AssetKey.RETURN_BUTTON_PRESSED), Assets.I().get(AssetKey.RETURN_BUTTON),
            new Rectangle(WINDOW_X + 4.1f, WINDOW_Y + 0.75f, 79 / 30f, 25 / 30f), Input.Keys.ENTER);
        returnButton.setShowShadow(false);
    }

    public void draw(SpriteBatch batch, float delta) {
        if (state == State.HIDDEN) return;

        // Animate
        if (state == State.ENTERING) {
            animT += delta;
            float p = clamp01(animT / ENTER_DUR);

            winScale = lerp(0.86f, 1.00f, easeOutBack(p));
            winAlpha = lerp(0.00f, 1.00f, p);
            winYOffset = lerp(-0.35f, 0.00f, p);

            if (p >= 1f) {
                state = State.SHOWN;
                animT = 0f;
            }
        } else if (state == State.EXITING) {
            animT += delta;
            float p = clamp01(animT / EXIT_DUR);

            winScale = lerp(1.00f, 0.92f, easeInQuad(p));
            winAlpha = lerp(1.00f, 0.00f, p);
            winYOffset = lerp(0.00f, -0.20f, p);

            if (p >= 1f) {
                state = State.HIDDEN;
                animT = 0f;
                return;
            }
        } else { // SHOWN
            winScale = 1f;
            winAlpha = 1f;
            winYOffset = 0f;
        }

        // Window shadow
        float winW = 303 / 40f;
        float winH = 340 / 40f;
        Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            windowShadow,
            new Rectangle(WINDOW_X + 0.1f, WINDOW_Y - 0.2f + winYOffset, winW, winH),
            14, new Color(shadowColor.r, shadowColor.g, shadowColor.b, winAlpha)
        ));

        // Window
        Pencil.I().addDrawing(new TextureDrawing(
            window,
            new Rectangle(WINDOW_X, WINDOW_Y + winYOffset, winW, winH),
            14, new Color(1f, 1f, 1f, winAlpha)
        ));

        // Shop title shadow + title (fade + slide)
        Pencil.I().addDrawing(new TextureDrawing(shopTxtShadow,
            new Rectangle(WINDOW_X + 2.6f, WINDOW_Y + 6.5f + winYOffset, 29 / 10f, 13 / 10f),
            14, new Color(shadowColor.r, shadowColor.g, shadowColor.b, winAlpha)));
        Pencil.I().addDrawing(new TextureDrawing(shopTxt,
            new Rectangle(WINDOW_X + 2.5f, WINDOW_Y + 6.6f + winYOffset, 29 / 10f, 13 / 10f),
            14, new Color(1f, 1f, 1f, winAlpha)));

        shopCardsBar.draw(batch);

        returnButton.draw(batch, delta);
        rerollButton.draw(batch, delta);
        creditScore.draw(batch, delta);
    }

    public void show() {
        shopCardsBar.loadUpgrades(Deck.I().randomUpgrades());
        creditScore.setScore(CreditManager.I().getCredits());

        state = State.ENTERING;
        animT = 0f;
    }

    public boolean isShowing() {
        return state != State.HIDDEN;
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if (state != State.SHOWN) return;

        shopCardsBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        returnButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        rerollButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

    private static float clamp01(float x) {
        return Math.max(0f, Math.min(1f, x));
    }

    private static float easeOutBack(float t) {
        t = clamp01(t);
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
    }

    private static float easeInQuad(float t) {
        t = clamp01(t);
        return t * t;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }


}
