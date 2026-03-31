package com.avaricious.components;

import com.avaricious.components.slot.DragableBody;
import com.avaricious.items.AbstractItem;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBag {

    private static ItemBag instance;

    public static ItemBag I() {
        return instance == null ? instance = new ItemBag() : instance;
    }

    private ItemBag() {
    }

    private boolean showingItems = false;

    private final Rectangle bagBounds = new Rectangle(7.2f, 5.75f, 42 / 30f, 48 / 30f);
    private final TextureRegion bagTexture = Assets.I().get(AssetKey.BAG);

    private final List<AbstractItem> items = new ArrayList<>();

    private final Vector2 mouseTouchdownLocation = new Vector2();

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        items.forEach(item -> item.getBody().update(delta));

        if(leftClickPressed && !leftClickWasPressed) {
            mouseTouchdownLocation.set(mouse.x, mouse.y);
        }

        if(!leftClickPressed && leftClickWasPressed && bagBounds.contains(mouseTouchdownLocation) && bagBounds.contains(mouse)) {
            toggleShowItems();
        }
    }

    public void draw() {
        Pencil.I().addDrawing(new TextureDrawing(
            bagTexture,
            bagBounds,
            ZIndex.RELIC_BAG
        ));

        if(showingItems) {
            for(AbstractItem item : items) {
                drawItem(item);
            }
        }
    }

    public void drawItem(AbstractItem item) {
        DragableBody body = item.getBody();
        Vector2 pos = body.getRenderPos(new Vector2());
        float scale = body.getScale();
        float rotation = body.getRotation();

        Pencil.I().addDrawing(new TextureDrawing(
            item.shadowTexture(),
            new Rectangle(pos.x, pos.y - 0.2f, )
        ));
    }

    public void toggleShowItems() {
        if (!showingItems) {
            List<Vector2> positions = new ArrayList<>();
            for (int col = 5; col > 0; col--) {
                for (int row = 0; row < 4; row++) {
                    positions.add(new Vector2(0.7f + row * 2f, 1f + col * 2.5f));
                }
            }

            int index = 0;
            for (AbstractItem item : items) {
                int finalIndex = index;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        item.getBody().moveTo(positions.get(finalIndex));
                    }
                }, finalIndex * 0.025f);
                index++;
            }

            showingItems = true;
        } else {
            List<AbstractItem> reversed = new ArrayList<>(items);
            Collections.reverse(reversed);
            int index = reversed.size() - 1;
            for (AbstractItem item : reversed) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        item.getBody().moveTo(new Vector2(
                            bagBounds.x, bagBounds.y
                        ));
                    }
                }, index * 0.025f);
                index--;
            }
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    showingItems = false;
                }
            }, items.size() * 0.025f + 0.25f);
        }
    }

    public <T extends AbstractItem> T getItemByClass(Class<T> ringClass) {
        return items.stream()
            .filter(ringClass::isInstance)
            .map(ringClass::cast)
            .findFirst().orElse(null);
    }


    public <T> List<T> getItemOfType(Class<T> type) {
        return items.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .collect(Collectors.toList());
    }

    public boolean containsItem(Class<? extends AbstractItem> itemClass) {
        return items.stream().anyMatch(itemClass::isInstance);
    }

    public void addItem(AbstractItem item) {
        items.add(item);
    }

    public void removeItem(AbstractItem item) {
        items.remove(item);
    }

    public List<AbstractItem> getItems() {
        return items;
    }
}
