package com.avaricious;

import com.avaricious.utility.DeviceInfo;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIRectEdge;

public class IOSLauncher extends IOSApplication.Delegate {

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration configuration =
            new IOSApplicationConfiguration();

        configuration.screenEdgesDeferringSystemGestures =
            UIRectEdge.Bottom;
        configuration.hideHomeIndicator = false;

        DeviceInfo deviceInfo = new IOSDeviceInfo();
        return new IOSApplication(
            new Main(deviceInfo),
            configuration
        );
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();

        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}
