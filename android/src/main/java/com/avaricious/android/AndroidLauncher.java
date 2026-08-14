package com.avaricious.android;

import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

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
        configuration.useImmersiveMode = true;

        DeviceInfo deviceInfo = new AndroidDeviceInfo();
        initialize(new Main(deviceInfo), configuration);

        hideSystemUI();
    }

    private void hideSystemUI() {
        WindowInsetsController controller = getWindow().getInsetsController();

        if (controller == null) {
            return;
        }

        controller.hide(WindowInsets.Type.systemBars());

        controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) hideSystemUI();
    }
}
