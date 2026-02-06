package com.avaricious.components.bars;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Slot;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SlotScreenJokerBar {

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final TextureRegion jokerTexture = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerShadowTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);
    private final TextureRegion blueGreenTexture = Assets.I().get(AssetKey.BLUE_GREEN_PIXEL);

    private final Map<Upgrade, Rectangle> jokerBounds = new LinkedHashMap<>();
    private final Map<Upgrade, Slot> jokerAnimationManagers = new LinkedHashMap<>();

    private final List<Rectangle> jokerRectangles;

    private Upgrade selectedUpgrade;

    public SlotScreenJokerBar() {
        jokerRectangles = Arrays.asList(
            new Rectangle(0.6f, 2.75f, 142 / 95f, 190 / 95f),
            new Rectangle(2.6f, 2.75f, 142 / 95f, 190 / 95f),
            new Rectangle(4.6f, 2.75f, 142 / 95f, 190 / 95f),
            new Rectangle(6.6f, 2.75f, 142 / 95f, 190 / 95f),
            new Rectangle(0.6f, 0.5f, 142 / 95f, 190 / 95f),
            new Rectangle(2.6f, 0.5f, 142 / 95f, 190 / 95f),
            new Rectangle(4.6f, 0.5f, 142 / 95f, 190 / 95f),
            new Rectangle(6.6f, 0.5f, 142 / 95f, 190 / 95f)
        );

        loadJokers(UpgradesManager.I().getDeck());
        UpgradesManager.I().onDeckChange(this::loadJokers);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        jokerBounds.forEach((upgrade, bounds) -> {
            if (pressed && !wasPressed) {
                float x = bounds.x;
            }
            if (bounds.contains(mouse) && pressed && !wasPressed) {
                selectedUpgrade = selectedUpgrade != upgrade ? upgrade : null;
                Slot slot = jokerAnimationManagers.get(upgrade);
                slot.wobble();
                slot.pulse();
            }
        });

        jokerAnimationManagers.forEach(((upgrade, slot) -> {
            slot.updateHoverWobble(true, delta);
            slot.updatePulse(false, delta);
        }));
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(Assets.I().shadowColor());
        jokerRectangles.forEach(rectangle -> batch.draw(blueGreenTexture, rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        batch.setColor(1f, 1f, 1f, 1f);

        jokerBounds.entrySet().stream()
            .filter(entry -> entry.getKey() != selectedUpgrade)
            .forEach((entry -> drawJokerCard(batch, entry.getKey(), entry.getValue())));
        if (selectedUpgrade != null)
            drawJokerCard(batch, selectedUpgrade, jokerBounds.get(selectedUpgrade));
    }

    private void drawJokerCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds) {
        Slot slot = jokerAnimationManagers.get(upgrade);
        float selectedScale = selectedUpgrade == upgrade ? 1.3f : 1f;
        float s = slot.pulseScale() * slot.wobbleScale() * selectedScale;
        float r = slot.wobbleAngleDeg();

        float originX = bounds.width * 0.5f;
        float originY = bounds.height * 0.5f;

        if (selectedUpgrade == upgrade) {
            batch.setColor(Assets.I().shadowColor());
            batch.draw(jokerShadowTexture,
                bounds.x, bounds.y - 0.2f,
                originX, originY,
                bounds.width, bounds.height,
                s, s,
                r);
            batch.setColor(1f, 1f, 1f, 1f);

            PopupManager.I().renderTooltip(selectedUpgrade, bounds.x - 2f, bounds.y + 2.65f);
        }

        batch.draw(
            jokerTexture,
            bounds.x, bounds.y,
            originX, originY,
            bounds.width, bounds.height,
            s, s,
            r);
    }

    private void loadJokers(List<? extends Upgrade> upgrades) {
        jokerBounds.clear();
        jokerAnimationManagers.clear();

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            Rectangle rectangle = jokerRectangles.get(i);

            jokerBounds.put(upgrade, rectangle);
            jokerAnimationManagers.put(upgrade, new Slot(new Vector2(rectangle.x, rectangle.y)));
        }
    }

    public Rectangle getBoundsByUpgrade(Upgrade upgrade) {
        return jokerBounds.get(upgrade);
    }

    public Slot getSlotByUpgrade(Upgrade upgrade) {
        return jokerAnimationManagers.get(upgrade);
    }

}
