package com.avaricious.items.upgrades;

import com.avaricious.items.AbstractItem;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractUpgrade extends AbstractItem {

    protected UpgradeRarity rarity = UpgradeRarity.COMMON;

    public abstract IUpgradeType type();

    public UpgradeRarity rarity() {
        return rarity;
    }

    public static <T> T instantiateUpgrade(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
