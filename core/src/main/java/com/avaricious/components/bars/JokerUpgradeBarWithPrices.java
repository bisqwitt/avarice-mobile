package com.avaricious.components.bars;

import com.avaricious.CreditManager;
import com.avaricious.CreditNumber;
import com.avaricious.cards.Card;
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JokerUpgradeBarWithPrices extends JokerUpgradeBar {

    private final Map<Card, CreditNumber> cardPrices = new HashMap<>();

    public JokerUpgradeBarWithPrices(List<? extends Card> upgrades, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        super(upgrades, cardRectangle, offset, tooltipOnTop);
        loadCreditNumbers(upgrades);
    }

    @Override
    protected void drawCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds, float scale, float rotation) {
        super.drawCard(batch, upgrade, bounds, scale, rotation);
        cardPrices.get(upgrade).draw(batch, 0f);
    }

    @Override
    protected void onUpgradeClicked(Upgrade clickedUpgrade) {
        if (CreditManager.I().enoughCredit(3)) {
            super.onUpgradeClicked(clickedUpgrade);
            cardPrices.remove(clickedUpgrade);
            CreditManager.I().pay(3);
        }
    }

    private void loadCreditNumbers(List<? extends Card> upgrades) {
        cardPrices.clear();

        for (int i = 0; i < upgrades.size(); i++) {
            cardPrices.put(upgrades.get(i),
                new CreditNumber(3,
                    new Rectangle(getCardRectangle().x + 0.35f + (i * 2f), getCardRectangle().y + 2.1f, 0.32f / 1.25f, 0.56f / 1.25f),
                    0.35f));
        }
    }

    @Override
    public void loadUpgrades(List<? extends Upgrade> upgrades) {
        super.loadUpgrades(upgrades);
        loadCreditNumbers((List<Card>) upgrades);
    }
}
