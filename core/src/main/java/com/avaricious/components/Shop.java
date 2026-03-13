package com.avaricious.components;

import com.avaricious.CreditManager;
import com.avaricious.CreditScore;
import com.avaricious.components.bars.ShopCardsBar;
import com.avaricious.components.buttons.Button;
import com.avaricious.components.buttons.NextRoundButton;
import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.cards.CardPack;
import com.avaricious.upgrades.rings.RingPack;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Shop {

    private static final float WINDOW_X = -1f;
    private static final float WINDOW_Y = 4.85f;
    private static final float WINDOW_WIDTH = 375 / 35f;
    public static final float WINDOW_HEIGHT = 534 / 35f;

    // fully above the screen
    private static final float OFFSCREEN_TOP_Y = WINDOW_Y + WINDOW_HEIGHT + 2f;

    // units per second
    private float currentWindowY = OFFSCREEN_TOP_Y;

    private final TextureRegion window = Assets.I().get(AssetKey.SHOP_WINDOW);
    private final TextureRegion shopTxt = Assets.I().get(AssetKey.SHOP_TXT);
    private final TextureRegion shopTxtShadow = Assets.I().get(AssetKey.SHOP_TXT_SHADOW);
    private final TextureRegion yellowTexture = Assets.I().get(AssetKey.YELLOW_PIXEL);

    private final Button nextRoundButton;
    private final Button rerollButton;

    private final BuyBox buyBox = new BuyBox();
    private final CreditScore creditScore;
    private final ShopCardsBar cards = new ShopCardsBar(buyBox.getBounds());
    private final CardPack cardPack = new CardPack(buyBox.getBounds());
    private final RingPack ringPack = new RingPack(buyBox.getBounds());

    private enum State {HIDDEN, ENTERING, SHOWN, EXITING}

    private State state = State.HIDDEN;

    private static final float GRAVITY_ENTER = -55f;
    private static final float GRAVITY_EXIT = 70f;
    private static final float BOUNCE_DAMPING = 0.28f;
    private static final float MIN_BOUNCE_VELOCITY = 4f;
    private float windowVelocityY = 0f;

    public Shop(Runnable onReturnedFromShop) {
        creditScore = new CreditScore(0,
            new Rectangle(0.4f, 0.85f, 7 / 10f, 11 / 10f), 0.9f);

        rerollButton = new Button(() -> {
            if (CreditManager.I().enoughCredit(3)) {
                cards.loadCards(Deck.I().randomUpgrades(3));
                CreditManager.I().pay(3);
            }
        },
            Assets.I().get(AssetKey.REROLL_BUTTON), Assets.I().get(AssetKey.REROLL_BUTTON_PRESSED), Assets.I().get(AssetKey.REROLL_BUTTON),
            new Rectangle(WINDOW_X + 5.75f, 12, 79 / 30f, 25 / 30f), Input.Keys.SPACE, ZIndex.SHOP);
        rerollButton.setShowShadow(false);

        nextRoundButton = new NextRoundButton(() -> {
            state = State.EXITING;
            windowVelocityY = 0f;
            onReturnedFromShop.run(); // ideally delay this until fully hidden
            HealthUi.I().moveIn();
        }, new Rectangle(2.4f, 0.25f, 63 / 33f, 79 / 33f), Input.Keys.ENTER, ZIndex.SHOP);
    }

    public void draw(SpriteBatch batch, float delta) {
        if (state == State.HIDDEN) return;
        updateAnimation(delta);

        Pencil.I().addDrawing(new TextureDrawing(
            window,
            new Rectangle(WINDOW_X, currentWindowY, WINDOW_WIDTH, WINDOW_HEIGHT),
            ZIndex.SHOP
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            shopTxtShadow,
            new Rectangle(WINDOW_X + 3.75f, currentWindowY + 10.95f, 29 / 8f, 13 / 8f),
            ZIndex.SHOP,
            Assets.I().shadowColor()
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            shopTxt,
            new Rectangle(WINDOW_X + 3.75f, currentWindowY + 11.05f, 29 / 8f, 13 / 8f),
            ZIndex.SHOP
        ));

        buyBox.setVisible(cards.isDragging() || cardPack.isDragging() || ringPack.isDragging());

        cards.draw();
        cardPack.draw();
        ringPack.draw();

        rerollButton.draw();
        creditScore.draw(batch, delta);
        nextRoundButton.draw();
        buyBox.draw(delta);
    }

    public void show() {
        cards.loadCards(Deck.I().randomUpgrades(3));
        currentWindowY = OFFSCREEN_TOP_Y;
        windowVelocityY = 0f;
        state = State.ENTERING;

        HealthUi.I().moveOut();
    }

    private void updateAnimation(float delta) {
        switch (state) {
            case ENTERING:
                // falling down
                windowVelocityY += GRAVITY_ENTER * delta;
                currentWindowY += windowVelocityY * delta;

                // hit the resting position -> bounce
                if (currentWindowY <= WINDOW_Y) {
                    currentWindowY = WINDOW_Y;

                    if (Math.abs(windowVelocityY) < MIN_BOUNCE_VELOCITY) {
                        windowVelocityY = 0f;
                        state = State.SHOWN;

                        cards.showCards();
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                ringPack.showPack();
                            }
                        }, 0.6f);
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                cardPack.showPack();
                            }
                        }, 0.8f);
                    } else {
                        windowVelocityY = -windowVelocityY * BOUNCE_DAMPING;
                    }
                }
                break;

            case EXITING:
                // accelerate upward
                windowVelocityY += GRAVITY_EXIT * delta;
                currentWindowY += windowVelocityY * delta;

                if (currentWindowY >= OFFSCREEN_TOP_Y) {
                    currentWindowY = OFFSCREEN_TOP_Y;
                    windowVelocityY = 0f;
                    state = State.HIDDEN;
                }
                break;

            case SHOWN:
            case HIDDEN:
            default:
                break;
        }

        if (state == State.ENTERING || state == State.EXITING) {
            rerollButton.getButtonRectangle().setY(currentWindowY + 6.4f);
        }
    }

    private float getWindowOffsetY() {
        return currentWindowY - WINDOW_Y;
    }

    public boolean isShowing() {
        return state != State.HIDDEN;
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if (state != State.SHOWN) return;

        cards.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        cardPack.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        ringPack.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        nextRoundButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        rerollButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }

}
