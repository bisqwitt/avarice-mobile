package com.avaricious.utility;

import java.util.concurrent.ThreadLocalRandom;

public enum RingKey {
    RING_4(AssetKey.RING_4, AssetKey.RING_4_SHADOW),
    RING_12(AssetKey.RING_12, AssetKey.RING_12_SHADOW),
    RING_19(AssetKey.RING_19, AssetKey.RING_19_SHADOW),
    RING_24(AssetKey.RING_24, AssetKey.RING_24_SHADOW),
    RING_25(AssetKey.RING_25, AssetKey.RING_25_SHADOW),
    RING_31(AssetKey.RING_31, AssetKey.RING_31_SHADOW),
    RING_32(AssetKey.RING_32, AssetKey.RING_32_SHADOW),
    RING_43(AssetKey.RING_43, AssetKey.RING_43_SHADOW),
    RING_44(AssetKey.RING_44, AssetKey.RING_44_SHADOW),
    RING_58(AssetKey.RING_58, AssetKey.RING_58_SHADOW),
    RING_77(AssetKey.RING_77, AssetKey.RING_77_SHADOW),
    RING_80(AssetKey.RING_80, AssetKey.RING_80_SHADOW),
    RING_93(AssetKey.RING_93, AssetKey.RING_93_SHADOW),
    RING_95(AssetKey.RING_95, AssetKey.RING_95_SHADOW);

    private final AssetKey assetKey;
    private AssetKey shadowKey;

    RingKey(AssetKey assetKey, AssetKey shadowKey) {
        this.assetKey = assetKey;
        this.shadowKey = shadowKey;
    }

    RingKey(AssetKey assetKey) {
        this(assetKey, null);
    }

    public AssetKey getAssetKey() {
        return assetKey;
    }

    public AssetKey getShadowKey() {
        return shadowKey;
    }

    public static RingKey random() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }
}
