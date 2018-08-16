package suraj.tiwari.reactnativefbads;

import android.util.Log;
import android.view.View;

import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdsManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdManager extends ReactContextBaseJavaModule implements NativeAdsManager.Listener {
  /**
   * @{Map} with all registered fb ads managers
   **/
  private Map<String, NativeAdsManager> mAdsManagers = new HashMap<>();

  public NativeAdManager(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "CTKNativeAdManager";
  }

  /**
   * Initialises native ad manager for a given placement id and ads to request.
   * This method is run on the UI thread
   *
   * @param placementId
   * @param adsToRequest
   */
  @ReactMethod
  public void init(final String placementId, final int adsToRequest) {
    final ReactApplicationContext reactContext = this.getReactApplicationContext();

    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final NativeAdsManager adsManager = new NativeAdsManager(reactContext, placementId, adsToRequest);

        adsManager.setListener(NativeAdManager.this);

        mAdsManagers.put(placementId, adsManager);

        adsManager.loadAds();
      }
    });
  }

  /**
   * Disables auto refresh
   *
   * @param placementId
   */
  @ReactMethod
  public void disableAutoRefresh(String placementId) {
    mAdsManagers.get(placementId).disableAutoRefresh();
  }

  /**
   * Sets media cache policy
   *
   * @param placementId
   * @param cachePolicy
   */
  @ReactMethod
  public void setMediaCachePolicy(String placementId, String cachePolicy) {
    Log.w("NativeAdManager", "This method is not supported on Android");
  }

  /**
   * Called when one of the registered ads managers loads ads. Sends state of all
   * managers back to JS
   */
  @Override
  public void onAdsLoaded() {
    WritableMap adsManagersState = Arguments.createMap();

    for (String key : mAdsManagers.keySet()) {
      NativeAdsManager adsManager = mAdsManagers.get(key);
      adsManagersState.putBoolean(key, adsManager.isLoaded());
    }

    sendAppEvent("CTKNativeAdsManagersChanged", adsManagersState);
  }

  @Override
  public void onAdError(AdError adError) {
    // @todo handle errors here
  }

  /**
   * Returns FBAdsManager for a given placement id
   *
   * @param placementId
   * @return
   */
  public NativeAdsManager getFBAdsManager(String placementId) {
    return mAdsManagers.get(placementId);
  }

  /**
   * Helper for sending events back to Javascript.
   *
   * @param eventName
   * @param params
   */
  private void sendAppEvent(String eventName, Object params) {
    ReactApplicationContext context = this.getReactApplicationContext();

    if (context == null || !context.hasActiveCatalystInstance()) {
      return;
    }

    context
        .getJSModule(RCTNativeAppEventEmitter.class)
        .emit(eventName, params);
  }

  @ReactMethod
  public void registerViewsForInteraction(final int adTag,
                                          final int mediaViewTag,
                                          final int adIconViewTag,
                                          final ReadableArray clickableViewsTags,
                                          final Promise promise) {
    getReactApplicationContext().getNativeModule(UIManagerModule.class).addUIBlock(new UIBlock() {
      @Override
      public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
        try {
          NativeAdView nativeAdView = null;
          MediaView mediaView = null;
          AdIconView adIconView = null;

          if (adTag != -1) {
            nativeAdView = (NativeAdView) nativeViewHierarchyManager.resolveView(adTag);
          }

          if (mediaViewTag != -1) {
            mediaView = (MediaView) nativeViewHierarchyManager.resolveView(mediaViewTag);
          }

          if (adIconViewTag != -1) {
            adIconView = (AdIconView) nativeViewHierarchyManager.resolveView(adIconViewTag);
          }

          List<View> clickableViews = new ArrayList<>();

          for (int i = 0; i < clickableViewsTags.size(); ++i) {
            View view = nativeViewHierarchyManager.resolveView(clickableViewsTags.getInt(i));
            clickableViews.add(view);
          }

          Log.w("NativeAdManagerClickableViewsTags", Integer.toString(clickableViewsTags.size()));
          Log.w("NativeAdManagerClickableViews", Integer.toString(clickableViews.size()) );

          nativeAdView.registerViewsForInteraction(mediaView, adIconView, clickableViews);
          promise.resolve(null);

        } catch (ClassCastException e) {
          promise.reject("E_CANNOT_CAST", e);
        } catch (IllegalViewOperationException e) {
          promise.reject("E_INVALID_TAG_ERROR", e);
        } catch (NullPointerException e) {
          promise.reject("E_NO_NATIVE_AD_VIEW", e);
        } catch (Exception e) {
          promise.reject("E_AD_REGISTER_ERROR", e);
        }
      }
    });
  }
}