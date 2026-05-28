package com.avaricious.components.roundInfoPanel;

import com.avaricious.CreditScore;
import com.avaricious.DevTools;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.texts.CreditsText;
import com.avaricious.components.texts.FabledText;
import com.avaricious.components.texts.SpinsText;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Observable;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RoundInfoPanel extends Observable<Float> {

    private static RoundInfoPanel instance;

    public static RoundInfoPanel I() {
        return instance == null ? instance = new RoundInfoPanel() : instance;
    }

    private final SpinsText spinsText = new SpinsText(new Vector2(6.75f, 5.65f), 30f, 0.05f, ZIndex.PATTERN_DISPLAY);
    private final DigitalNumber spinsNumber = new DigitalNumber(1, new Color(1f, 1f, 1f, 1f), 1,
        new Rectangle(8.25f, 5.65f, 7 / 30f, 11 / 30f), 0.7f);

    private final CreditsText creditsText = new CreditsText(new Vector2(6.75f, 19.1f), 30f, 0.05f, ZIndex.PATTERN_DISPLAY);
    private final CreditScore creditScore = new CreditScore(new Rectangle(0f, 18.35f, 7 / 23f, 11 / 23f), 0.4f);

    private final TextureRegion black = Assets.I().get(AssetKey.BLACK_PIXEL);
    private final TextureRegion white = Assets.I().get(AssetKey.WHITE_PIXEL);

    private RoundInfoPanel() {
//        RoundsManager.I().onChange(currentRoundNumber::setValue);
    }

    public void update(float delta) {
        centerRoundInfoNumbers();
    }

    public void draw(float delta) {
        update(delta);
        Pencil.I().addDrawing(new TextureDrawing(black,
            0, 18f, 9f, 5f, ZIndex.PATTERN_DISPLAY, Assets.I().shadowColor()));

        spinsText.draw(delta);
        spinsNumber.draw(delta);

        creditsText.draw(delta);
        creditScore.draw(delta);

        Pencil.I().addDrawing(new TextureDrawing(white,
            0, 18f, 9f, 0.05f, ZIndex.PATTERN_DISPLAY));
    }

    private void centerRoundInfoNumbers() {
        centerNumberToText(creditsText, creditScore);
    }

    private void centerNumberToText(FabledText text, DigitalNumber number) {
        float textX = text.getStartingPos().x;
        float textWidth = text.getWidth();
        float numberWidth = number.getWidth();

        number.getFirstDigitBounds().x = textX + (textWidth / 2f) - (numberWidth / 2f);
    }

    public void setSpins(float value) {
        if (!DevTools.unlimitedSpins())
            spinsNumber.setValue(value);

        if (value > 0 && SlotMachine.I().isStale())
            ScreenManager.I().getScreen(SlotScreen.class).onSpinButtonPressed();
        notifyChanged(snapshot());
    }

    public void addSpin() {
        setSpins(getSpins() + 1);
    }

    public void minusSpin() {
        setSpins(getSpins() - 1);
    }

    public float getSpins() {
        return spinsNumber.getValue();
    }

    @Override
    protected Float snapshot() {
        return spinsNumber.getValue();
    }
}
