package com.avaricious.items.potions;

import com.avaricious.components.ItemBag;
import com.avaricious.items.upgrades.AbstractUpgrade;
import com.badlogic.gdx.utils.Timer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPotion extends AbstractUpgrade {

    public void use() {
        body.pulse();
        onUse();
        final AbstractPotion potion = this;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ItemBag.I().removeItem(potion);
            }
        }, 0.75f);
    }

    protected abstract void onUse();

    public static AbstractPotion randomPotion() {
        return instantiateItem(allPotionClasses.get((int) (Math.random() * allPotionClasses.size())));
    }

    public static final List<Class<? extends AbstractPotion>> allPotionClasses = Collections.unmodifiableList(Arrays.asList(
        HealthPotion.class,
        ShieldPotion.class,
        CardPotion.class,
        StreakPotion.class
    ));

}
