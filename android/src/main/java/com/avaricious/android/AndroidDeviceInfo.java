package com.avaricious.android;

import android.os.Build;

import com.avaricious.utility.DeviceInfo;

public class AndroidDeviceInfo implements DeviceInfo {
    public boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
            || Build.FINGERPRINT.contains("emulator")
            || Build.MODEL.contains("sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("emulator")
            || Build.BOARD.contains("goldfish")
            || Build.BOARD.contains("ranchu");
    }
}
