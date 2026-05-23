package com.avaricious;

import com.avaricious.utility.DeviceInfo;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class IOSDeviceInfo implements DeviceInfo {

    @Override
    public boolean isEmulator() {
        return isIOS() && isIOSSimulator();
    }

    private boolean isIOS() {
        return Gdx.app != null
            && Gdx.app.getType() == Application.ApplicationType.iOS;
    }

    private boolean isIOSSimulator() {
        return hasEnv("SIMULATOR_DEVICE_NAME")
            || hasEnv("SIMULATOR_UDID")
            || hasEnv("SIMULATOR_MODEL_IDENTIFIER")
            || hasEnv("SIMULATOR_ROOT");
    }

    private boolean hasEnv(String key) {
        String value = System.getenv(key);
        return value != null && !value.isEmpty();
    }
}
