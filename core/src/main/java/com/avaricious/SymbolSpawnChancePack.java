package com.avaricious;

import com.avaricious.components.buttons.Button;
import com.avaricious.components.popups.ClaimedPopup;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.popups.SoldPopup;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.cards.CardType;
import com.avaricious.upgrades.rings.RingType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class SymbolSpawnChancePack extends PackOpening {

    private Symbol result;

    public SymbolSpawnChancePack(Rectangle buyBounds) {
        super(new Rectangle(1.6f, 9.25f, 1.7f, 1.7f), buyBounds);
    }

    @Override
    protected TextureRegion getTexture() {
        if (ripped) return Assets.I().get(result.textureKey());
        return Assets.I().get(Symbol.values()[currentTextureIndex].textureKey());
    }

    @Override
    protected TextureRegion getShadowTexture() {
        if (ripped) return Assets.I().get(result.shadowKey());
        return Assets.I().get(Symbol.values()[currentTextureIndex].shadowKey());
    }

    @Override
    protected TextureRegion getWhiteTexture() {
        if (ripped) return Assets.I().get(result.whiteKey());
        return Assets.I().get(Symbol.values()[currentTextureIndex].whiteKey());
    }

    @Override
    protected int getTextureAmount() {
        return Symbol.values().length;
    }

    @Override
    protected Upgrade getPackDescription() {
        return new Upgrade() {
            @Override
            public String title() {
                return "Symbol";
            }

            @Override
            public String description() {
                return "Increase Spawn chance of random Symbol by 5%\n"
                    + "(Can be discarded)";
            }

            @Override
            public IUpgradeType type() {
                return CardType.UNKNOWN;
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
        result = Symbol.values()[MathUtils.random(Symbol.values().length - 1)];
        return new Upgrade() {
            @Override
            public String title() {
                return "Spawn chance";
            }

            @Override
            public String description() {
                return "Increase " + result.toString() + " spawn chance from " + result.baseSpawnChance()
                    + "% to " + (result.baseSpawnChance() + 5) + "%";
            }

            @Override
            public IUpgradeType type() {
                return RingType.PASSIVE;
            }

            @Override
            public int price() {
                return 5;
            }

            @Override
            public TextureRegion texture() {
                return Assets.I().get(result.textureKey());
            }

            @Override
            public TextureRegion shadowTexture() {
                return Assets.I().get(result.shadowKey());
            }
        };
    }

    @Override
    protected Button getSellButton() {
        return new Button(() -> {
            PopupManager.I().killTooltip(tooltipPopup);
            closing = true;
            PopupManager.I().spawnTextPopup(new SoldPopup(new Vector2(2.5f, 11f), ZIndex.PACK_OPENING_SELECTED));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    close();
                }
            }, 1);
        },
            Assets.I().get(AssetKey.DISCARD_BUTTON),
            Assets.I().get(AssetKey.DISCARD_BUTTON_PRESSED),
            Assets.I().get(AssetKey.DISCARD_BUTTON),
            new Rectangle(1, 5, 79 / 25f, 25 / 25f),
            Input.Keys.BACKSPACE, ZIndex.PACK_OPENING_SELECTED);
    }

    @Override
    protected Button getClaimButton() {
        return new Button(() -> {
            result.setBaseSpawnChance(result.baseSpawnChance() + 5);
            PopupManager.I().killTooltip(tooltipPopup);
            closing = true;
            PopupManager.I().spawnTextPopup(new ClaimedPopup(new Vector2(2.5f, 11f), ZIndex.PACK_OPENING_SELECTED));
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

    @Override
    protected float getTooltipYOffset() {
        return 2.25f;
    }
}
