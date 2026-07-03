package com.avaricious.components.shop;

import static com.badlogic.gdx.math.MathUtils.lerp;

import com.avaricious.CreditManager;
import com.avaricious.CreditScore;
import com.avaricious.components.ButtonBoard;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.buttons.Button;
import com.avaricious.components.buttons.NextRoundButton;
import com.avaricious.components.texts.ShopText;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shop {

    private static final float WINDOW_X = -1f;
    private static final float WINDOW_Y = 0f;
    private static final float WINDOW_WIDTH = 375 / 35f;
    public static final float WINDOW_HEIGHT = 800 / 35f;

    // fully above the screen
    private static final float OFFSCREEN_TOP_Y = WINDOW_Y + WINDOW_HEIGHT + 2f;

    private float currentWindowY = OFFSCREEN_TOP_Y;

    private final ShopText shopText = new ShopText(new Vector2(WINDOW_X + 3.5f, currentWindowY + 14.65f),
        8f, 0.25f, ZIndex.SHOP);
    private final TextureRegion window = Assets.I().get(AssetKey.CHARCOAL_PIXEL);
    private final TextureRegion shopSlot = Assets.I().get(AssetKey.SHOP_SLOT);
    private final TextureRegion deckEditShopSlot = Assets.I().get(AssetKey.DECK_EDIT_SHOP_SLOT);

    private final Button nextRoundButton;
    private final Button rerollButton;

    private final CreditScore creditScore;

    private final ShopItemBar shopItemBar = new ShopItemBar();
    private final ShopItemBar shopItemBar2 = new ShopItemBar();
    //    private SymbolSpawnChancePack symbolSpawnChancePack = new SymbolSpawnChancePack();
//    private CardPack cardPack = new CardPack();
//    private RingPack ringPack = new RingPack();
    private final CardRemover cardRemover = new CardRemover(new Vector2(2f, 4f));

    private final Runnable onReturnedFromShop;

    private enum State {HIDDEN, ENTERING, SHOWN, EXITING}

    private State state = State.HIDDEN;

    private static final float GRAVITY_ENTER = -55f;
    private static final float GRAVITY_EXIT = 70f;
    private static final float BOUNCE_DAMPING = 0.28f;
    private static final float MIN_BOUNCE_VELOCITY = 4f;
    private float windowVelocityY = 0f;

    // ===== shop UI movement like HealthUi =====
    private boolean isUiMoving = false;
    private float uiMoveTime = 0f;
    private final float uiMoveDuration = 0.45f;
    private final float uiMoveDistance = 5f;

    private final float baseCreditScoreY = 0.5f;
    private final float baseNextRoundButtonY = 0.25f;

    private float startCreditScoreY;
    private float startNextRoundButtonY;
    private float targetCreditScoreY;
    private float targetNextRoundButtonY;
    // ==========================================

    public Shop(Runnable onReturnedFromShop) {
        this.onReturnedFromShop = onReturnedFromShop;

        creditScore = new CreditScore(
            new Rectangle(0.75f, baseCreditScoreY, 7 / 12f, 11 / 12f),
            0.9f
        );

        rerollButton = new Button(() -> {
            if (CreditManager.I().enoughCredit(3)) {
                shopItemBar.load();
                shopItemBar2.load();
                CreditManager.I().pay(3);
            } else {
                CreditManager.I().pulse();
            }
        },
            Assets.I().get(AssetKey.REROLL_BUTTON),
            Assets.I().get(AssetKey.REROLL_BUTTON_PRESSED),
            Assets.I().get(AssetKey.REROLL_BUTTON),
            new Rectangle(WINDOW_X + 5.75f, 12, 79 / 30f, 25 / 30f),
            Input.Keys.SPACE,
            ZIndex.SHOP
        );
        rerollButton.setShowShadow(false);

        nextRoundButton = new NextRoundButton(() -> {
            state = State.EXITING;
            windowVelocityY = 0f;
            moveOutUi();
            ButtonBoard.I().moveIn();
        }, new Rectangle(6f, baseNextRoundButtonY, 75 / 33f, 43 / 33f), Input.Keys.ENTER, ZIndex.SHOP);

        // start hidden below like HealthUi.moveOut() result
        creditScore.getFirstDigitBounds().y = baseCreditScoreY - uiMoveDistance;
        nextRoundButton.getBounds().y = baseNextRoundButtonY - uiMoveDistance;
    }

    public void draw(float delta) {
        if (state == State.HIDDEN) return;

        updateAnimation(delta);
        updateUiMovement(delta);

        Pencil.I().addDrawing(new TextureDrawing(
            window,
            WINDOW_X, currentWindowY, WINDOW_WIDTH, WINDOW_HEIGHT,
            ZIndex.SHOP
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            shopSlot,
            WINDOW_X + 1.75f, currentWindowY + 12.25f, 110 / 15f, 60 / 15f,
            ZIndex.SHOP, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            shopSlot,
            WINDOW_X + 1.75f, currentWindowY + 7.75f, 110 / 15f, 60 / 15f,
            ZIndex.SHOP, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            deckEditShopSlot,
            WINDOW_X + 2.45f, currentWindowY + 3.2f, 90 / 15f, 60 / 15f,
            ZIndex.SHOP, Assets.I().shadowColor()
        ));

        shopText.draw(delta);

        shopItemBar.draw(delta);
        shopItemBar2.draw(delta);
//        symbolSpawnChancePack.draw(delta);
//        cardPack.draw(delta);
//        ringPack.draw(delta);
        cardRemover.draw(delta);

//        rerollButton.draw();
        creditScore.draw(delta);
        nextRoundButton.draw(delta);
    }

    public void show() {
        shopItemBar.load();
        shopItemBar2.load();
//        symbolSpawnChancePack = new SymbolSpawnChancePack();
//        cardPack = new CardPack();
//        ringPack = new RingPack();

        currentWindowY = OFFSCREEN_TOP_Y;
        windowVelocityY = 0f;
        state = State.ENTERING;

        // keep them out while entering
        creditScore.getFirstDigitBounds().y = baseCreditScoreY - uiMoveDistance;
        nextRoundButton.getBounds().y = baseNextRoundButtonY - uiMoveDistance;

        ButtonBoard.I().moveOut();
        moveInUi();
    }

    private void updateAnimation(float delta) {
        switch (state) {
            case ENTERING:
                windowVelocityY += GRAVITY_ENTER * delta;
                currentWindowY += windowVelocityY * delta;

                if (currentWindowY <= WINDOW_Y) {
                    currentWindowY = WINDOW_Y;
                    ScreenShake.I().addTrauma(Math.abs(windowVelocityY * 0.01f));

                    if (Math.abs(windowVelocityY) < MIN_BOUNCE_VELOCITY) {
                        windowVelocityY = 0f;
                        state = State.SHOWN;
                    } else {
                        windowVelocityY = -windowVelocityY * BOUNCE_DAMPING;
                    }
                }
                break;

            case EXITING:
                windowVelocityY += GRAVITY_EXIT * delta;
                currentWindowY += windowVelocityY * delta;

                if (currentWindowY >= OFFSCREEN_TOP_Y) {
                    currentWindowY = OFFSCREEN_TOP_Y;
                    windowVelocityY = 0f;
                    onReturnedFromShop.run();
                    state = State.HIDDEN;
                }
                break;

            case SHOWN:
            case HIDDEN:
            default:
                break;
        }

        // keep child UI synced with the window position
        if (state == State.ENTERING || state == State.EXITING) {
            shopText.getStartingPos().y = currentWindowY + 17f;
            shopItemBar.setY(currentWindowY + 13.55f);
            shopItemBar2.setY(currentWindowY + 9f);
            rerollButton.getBounds().setY(currentWindowY + 12.05f);
//            symbolSpawnChancePack.getBody().getPos().y = currentWindowY + 9.35f;
//            cardPack.getBody().getPos().y = currentWindowY + 9f;
//            ringPack.getBody().getPos().y = currentWindowY + 9.45f;
            cardRemover.getBody().getPos().y = currentWindowY + 4.45f;
            nextRoundButton.getBounds().setY(currentWindowY + 0.5f);
            creditScore.getFirstDigitBounds().setY(currentWindowY + 0.7f);
        }
    }

    private void moveInUi() {
        startCreditScoreY = creditScore.getFirstDigitBounds().y;
        startNextRoundButtonY = nextRoundButton.getBounds().y;

        targetCreditScoreY = baseCreditScoreY;
        targetNextRoundButtonY = baseNextRoundButtonY;

        uiMoveTime = 0f;
        isUiMoving = true;
    }

    private void moveOutUi() {
        startCreditScoreY = creditScore.getFirstDigitBounds().y;
        startNextRoundButtonY = nextRoundButton.getBounds().y;

        targetCreditScoreY = baseCreditScoreY - uiMoveDistance;
        targetNextRoundButtonY = baseNextRoundButtonY - uiMoveDistance;

        uiMoveTime = 0f;
        isUiMoving = true;
    }

    private void updateUiMovement(float delta) {
        if (!isUiMoving) return;

        uiMoveTime += delta;
        float progress = Math.min(uiMoveTime / uiMoveDuration, 1f);
        float eased = Interpolation.smooth.apply(progress);

        creditScore.getFirstDigitBounds().y = lerp(startCreditScoreY, targetCreditScoreY, eased);
        nextRoundButton.getBounds().y = lerp(startNextRoundButtonY, targetNextRoundButtonY, eased);

        if (progress >= 1f) {
            creditScore.getFirstDigitBounds().y = targetCreditScoreY;
            nextRoundButton.getBounds().y = targetNextRoundButtonY;
            isUiMoving = false;
        }
    }

    public boolean isShowing() {
        return state != State.HIDDEN;
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if (state != State.SHOWN) return;

        if (cardRemover.getRemoveCardWindow().isOpen()) {
            cardRemover.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
            return;
        }

        shopItemBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        shopItemBar2.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        symbolSpawnChancePack.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        cardPack.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
//        ringPack.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        cardRemover.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
        nextRoundButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
        rerollButton.handleInput(mouse, leftClickPressed, leftClickWasPressed);
    }
}
