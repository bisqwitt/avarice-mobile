package com.avaricious.android;

import android.os.Bundle;

import com.avaricious.Main;
import com.avaricious.utility.DeviceInfo;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        DeviceInfo deviceInfo = new AndroidDeviceInfo();
        initialize(new Main(deviceInfo), configuration);
    }
}
