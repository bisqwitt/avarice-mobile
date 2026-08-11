package com.avaricious.components.shop;

import com.avaricious.components.slot.Symbol;
import com.avaricious.components.texts.BellValueText;
import com.avaricious.components.texts.CherryValueText;
import com.avaricious.components.texts.CloverValueText;
import com.avaricious.components.texts.DiamondValueText;
import com.avaricious.components.texts.IronValueText;
import com.avaricious.components.texts.LemonValueText;
import com.avaricious.components.texts.SevenValueText;
import com.badlogic.gdx.math.Rectangle;

public class SymbolShopList extends ShopList {

    public SymbolShopList(Rectangle bounds) {
        super(bounds);
        items.add(new ShopItem(
            new LemonValueText(),
            3, Symbol.LEMON
        ));
        items.add(new ShopItem(
            new CherryValueText(),
            3, Symbol.CHERRY
        ));
        items.add(new ShopItem(
            new CloverValueText(),
            3, Symbol.CLOVER
        ));
        items.add(new ShopItem(
            new BellValueText(),
            3, Symbol.BELL
        ));
        items.add(new ShopItem(
            new IronValueText(),
            3, Symbol.IRON
        ));
        items.add(new ShopItem(
            new DiamondValueText(),
            3, Symbol.DIAMOND
        ));
        items.add(new ShopItem(
            new SevenValueText(),
            3, Symbol.SEVEN
        ));

        updateItemPositions();
    }

}
