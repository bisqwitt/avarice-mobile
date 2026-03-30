package com.avaricious.components;

import com.avaricious.CreditManager;
import com.avaricious.CreditNumber;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.popups.TooltipPopup;
import com.avaricious.components.slot.DragableBody;
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractShopItem {

    private final Rectangle buyBounds;

    private final Upgrade upgrade;
    private final CreditNumber priceTag;

    private boolean touched;
    private boolean selected;
    private boolean bought;

    private final Vector2 mouseTouchdownLocation = new Vector2();
    private TooltipPopup tooltipPopup = null;

    public AbstractShopItem(Rectangle buyBounds) {
        this.buyBounds = buyBounds;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        upgrade.getBody().update(delta);

        if(touching && !wasTouching) {
            if(upgrade.getBody().getBounds().contains(mouse)) {
                touched = true;
                upgrade.getBody().targetScale = 1.3f;
                upgrade.getBody().setIdleEffectsEnabled(false);
                upgrade.getBody().beginDrag(mouse.x, mouse.y, 0);

                mouseTouchdownLocation.set(mouse);
                tooltipPopup = PopupManager.I().createTooltip(upgrade, upgrade.getBody().getRenderPos(new Vector2()));
                return;
            }
            deselectCard(true);
        }

        if(touching && touched) {
            upgrade.getBody().dragTo(mouse.x, mouse.y, 0);
        }

        if(!touching && wasTouching && touched) {
            if(buyBounds.contains(upgrade.getBody().getCardCenter()) && CreditManager.I().enoughCredit(upgrade.price())) {
                buyUpgrade();
            } else {
                
            }
        }
    }

    private void buyUpgrade() {

    }

    private void deselectCard(boolean killTooltip) {
        upgrade.getBody().targetScale = 1f;
        upgrade.getBody().setIdleEffectsEnabled(true);
        selected = false;
        if(killTooltip) PopupManager.I().killTooltip(tooltipPopup);
    }

}
