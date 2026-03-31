package com.avaricious.items.upgrades.quests;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractQuest extends AbstractUpgrade {

    private boolean completed = false;

    @Override
    public String title() {
        return "Quest";
    }

    @Override
    public IUpgradeType type() {
        return QuestType.DEFAULT;
    }

    public TextureRegion texture() {
        return Assets.I().get(AssetKey.QUEST_SCROLL);
    }

    @Override
    public TextureRegion shadowTexture() {
        return Assets.I().get(AssetKey.QUEST_SCROLL_SHADOW);
    }

    public static AbstractQuest randomQuest() {
        return instantiateUpgrade(allQuestClasses.get((int) (Math.random() * allQuestClasses.size())));
    }

    public static final List<Class<? extends AbstractQuest>> allQuestClasses = Collections.unmodifiableList(Arrays.asList(
        PlaySevenCardsInOneSpinQuest.class
    ));

    public void complete() {
        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public abstract void claim();
}
