package com.avaricious.upgrades.rings;

import com.avaricious.CreditManager;
import com.avaricious.PackOpening;
import com.avaricious.components.RingBar;
import com.avaricious.components.buttons.Button;
import com.avaricious.components.popups.ClaimedPopup;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.popups.SoldPopup;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class RingPack extends PackOpening {

    private AbstractRing resultRing;

    public RingPack(Rectangle buyBounds) {
        super(new Rectangle(3.65f, 8.5f, 1.5f, 1.5f), buyBounds);
    }

    @Override
    protected TextureRegion getTexture() {
        if (ripped) return Assets.I().get(resultRing.keySet().getTextureKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getTextureKey());
    }

    @Override
    protected TextureRegion getShadowTexture() {
        if (ripped) return Assets.I().get(resultRing.keySet().getShadowKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getShadowKey());
    }

    @Override
    protected TextureRegion getWhiteTexture() {
        if (ripped) return Assets.I().get(resultRing.keySet().getWhiteKey());
        return Assets.I().get(RingAssetKeys.values()[currentTextureIndex].getWhiteKey());
    }

    @Override
    protected int getTextureAmount() {
        return RingAssetKeys.values().length;
    }

    @Override
    protected float getTooltipYOffset() {
        return 2f;
    }

    @Override
    protected Upgrade getPackDescription() {
        return new Upgrade() {
            @Override
            public String title() {
                return "Ring";
            }

            @Override
            public String description() {
                return "Random Ring";
            }

            @Override
            public IUpgradeType type() {
                return RingType.UNKNOWN;
            }

            @Override
            public int price() {
                return 5;
            }

            @Override
            public TextureRegion texture() {
                return null;
            }

            @Override
            public TextureRegion shadowTexture() {
                return null;
            }

            @Override
            public UpgradeRarity rarity() {
                return UpgradeRarity.UNKNOWN;
            }
        };
    }

    @Override
    protected Upgrade getResult() {
        return resultRing = AbstractRing.randomRing();
    }

    @Override
    protected Button getSellButton() {
        return new Button(() -> {
            CreditManager.I().gain(resultRing.price());
            PopupManager.I().killTooltip(tooltipPopup);
            closing = true;
            PopupManager.I().spawnTextPopup(new SoldPopup(new Vector2(2.5f, 12f), ZIndex.PACK_OPENING_SELECTED));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    close();
                }
            }, 1);
        },
            Assets.I().get(AssetKey.SELL_BUTTON),
            Assets.I().get(AssetKey.SELL_BUTTON_PRESSED),
            Assets.I().get(AssetKey.SELL_BUTTON),
            new Rectangle(1, 5, 79 / 25f, 25 / 25f),
            Input.Keys.BACKSPACE, ZIndex.PACK_OPENING_SELECTED);
    }

    @Override
    protected Button getClaimButton() {
        return new Button(() -> {
            RingBar.I().addRing(resultRing);
            PopupManager.I().killTooltip(tooltipPopup);
            closing = true;
            PopupManager.I().spawnTextPopup(new ClaimedPopup(new Vector2(2.5f, 12f), ZIndex.PACK_OPENING_SELECTED));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    close();
                }
            }, 1);
        },
            Assets.I().get(AssetKey.CLAIM_BUTTON),
            Assets.I().get(AssetKey.CLAIM_BUTTON_PRESSED),
            Assets.I().get(AssetKey.CLAIM_BUTTON),
            new Rectangle(4.9f, 5f, 79 / 25f, 25 / 25f),
            Input.Keys.ENTER, ZIndex.PACK_OPENING_SELECTED);
    }
}
