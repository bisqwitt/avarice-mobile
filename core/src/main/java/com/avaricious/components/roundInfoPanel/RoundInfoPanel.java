package com.avaricious.components.roundInfoPanel;

import com.avaricious.CreditScore;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.texts.CreditsText;
import com.avaricious.components.texts.FabledText;
import com.avaricious.components.texts.ReachText;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RoundInfoPanel {

    private static RoundInfoPanel instance;

    public static RoundInfoPanel I() {
        return instance == null ? instance = new RoundInfoPanel() : instance;
    }

    private final CreditsText creditsText = new CreditsText(new Vector2(6.75f, 19.1f), 30f, 0.05f, ZIndex.PATTERN_DISPLAY);
    private final CreditScore creditScore = new CreditScore(new Rectangle(0f, 18.35f, 7 / 23f, 11 / 23f), 0.4f);

    private final ReachText reachText = new ReachText(new Vector2(0.5f, 19.1f), 30f, 0.05f, ZIndex.PATTERN_DISPLAY);
    private final DigitalNumber reachNumber = new DigitalNumber(300, Assets.I().lightColor(), new Rectangle(0, 18.35f, 7 / 23f, 11 / 23f), 0.4f);

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

        reachText.draw(delta);
        reachNumber.draw(delta);

        creditsText.draw(delta);
        creditScore.draw(delta);

        Pencil.I().addDrawing(new TextureDrawing(white,
            0, 18f, 9f, 0.05f, ZIndex.PATTERN_DISPLAY));
    }

    private void centerRoundInfoNumbers() {
        centerNumberToText(creditsText, creditScore);
        centerNumberToText(reachText, reachNumber);
    }

    private void centerNumberToText(FabledText text, DigitalNumber number) {
        float textX = text.getStartingPos().x;
        float textWidth = text.getWidth();
        float numberWidth = number.getWidth();

        number.getFirstDigitBounds().x = textX + (textWidth / 2f) - (numberWidth / 2f);
    }

    public DigitalNumber getReachNumber() {
        return reachNumber;
    }
}
