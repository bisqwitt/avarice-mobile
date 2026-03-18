package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractBoss {

    public abstract String description();

    public abstract Upgrade loot();

    public static AbstractBoss getRandomBoss() {
        List<Class<? extends AbstractBoss>> allBossClasses = Arrays.asList(
            LemonDebuffBoss.class,
            OneLessCardBoss.class,
            DoubleDamageBoss.class
        );

        try {
            return allBossClasses.get((int) (Math.random() * allBossClasses.size())).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
