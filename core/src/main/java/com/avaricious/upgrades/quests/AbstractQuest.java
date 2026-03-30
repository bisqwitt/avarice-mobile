package com.avaricious.upgrades.quests;

import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractQuest extends Upgrade {

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
        return instantiateCard(allQuestClasses.get((int) (Math.random() * allQuestClasses.size())));
    }

    public static final List<Class<? extends AbstractQuest>> allQuestClasses = Collections.unmodifiableList(Arrays.asList(
        PlayFiveCardsInOneSpinQuest.class
    ));
}
