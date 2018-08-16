/**
 * FBAdsPackage.java
 * suraj.tiwari.reactnativefbads
 *
 * Created by Suraj Tiwari on 07/08/18.
 * Copyright © 2018 Suraj Tiwari All rights reserved.
 */
package suraj.tiwari.reactnativefbads;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Main package exporting native modules and views
 */
public class FBAdsPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(
           new NativeAdManager(reactContext),
           new AdSettingsManager(reactContext),
           new InterstitialAdManager(reactContext),
           new NativeAdChoicesViewManager(reactContext)
        );
    }

    // Deprecated RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
           new NativeAdViewManager(),
           new BannerViewManager(),
           new AdIconViewManager(),
           new MediaViewManager(),
           new NativeAdChoicesViewManager(reactContext)
        );
    }
}
