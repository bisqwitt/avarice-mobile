package com.avaricious.upgrades;

import com.avaricious.components.slot.DragableBody;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Upgrade {

    protected DragableBody body = null;

    public abstract String title();

    public abstract String description();

    public abstract IUpgradeType type();

    public abstract int price();

    public abstract TextureRegion texture();

    public abstract TextureRegion shadowTexture();

    public void addBody(Rectangle initialBounds) {
        body = new DragableBody(initialBounds).setTilt(200f, 20f);
    }

    public DragableBody getBody() {
        return body;
    }

}
