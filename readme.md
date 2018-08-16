react-native-ads-facebook [![npm version](https://badge.fury.io/js/react-native-ads-facebook.svg)](https://badge.fury.io/js/react-native-ads-facebook)
============

[![Facebook Ads](./images/threed_mockup_fbNativeads.png)](https://developers.facebook.com/products/app-monetization)

**Facebook Audience SDK** integration for React Native, available on iOS and Android. Features native, interstitial and banner ads.

## Table of Contents

- [Installation](#installation)
  - [Install Javascript packages](#1-install-javascript-packages)
  - [Configure native projects](#2-configure-native-projects)
    - [iOS](#21-ios)
    - [Android](#22-android)
- [Usage](#usage)
   - [Interstitial Ads](#interstitial-ads)
      - [1. Showing ad](#1-showing-ad)
   - [Native Ads](#native-ads)
      - [1. Creating AdsManager](#1-creating-adsmanager)
      - [2. Making ad component](#2-making-ad-component)
      - [3. Rendering an ad](#3-rendering-an-ad)
   - [Banner View](#bannerview)
      - [1. Showing ad](#1-showing-ad-1)
- [API](#api)
    - [NativeAdsManager](#nativeadsmanager)
      - [disableAutoRefresh](#disableautorefresh)
      - [setMediaCachePolicy](#setmediacachepolicy)
    - [InterstitialAdManager](#interstitialadmanager)
      - [showAd](#showad)
    - [AdSettings](#adsettings)
      - [currentDeviceHash](#currentdevicehash)
      - [addTestDevice](#addtestdevice)
      - [clearTestDevices](#cleartestdevices)
      - [setLogLevel](#setloglevel)
      - [setIsChildDirected](#setischilddirected)
      - [setMediationService](#setmediationservice)
      - [setUrlPrefix](#seturlprefix)
- [Running example](#running-example)
   - [Install dependencies](#1-install-dependencies)
   - [Start packager](#2-start-packager)
   - [Run it on iOS / Android](#3-run-it-on-ios--android)
- [Credits](#credits)

## Installation

### 1. Install Javascript packages
Install JavaScript packages:

```bash
$ react-native install react-native-ads-facebook
```

### 2. Configure native projects

The react-native-ads-facebook has been automatically linked for you, the next step will be downloading and linking the native Facebook SDK for both platforms.

#### 2.1 iOS

Make sure you have the latest Xcode installed. Open the .xcodeproj in Xcode found in the ios subfolder from your project's root directory. Now, follow all the steps in the [Getting Started Guide for Facebook SDK](https://developers.facebook.com/docs/ios/getting-started) for iOS. Along with FBSDKCoreKit.framework, don't forget to import FBAudienceNetwork.framework.

Next, **follow steps 1 and 3** from the [Getting Started Guide for Facebook Audience](https://developers.facebook.com/docs/audience-network/getting-started). Once you have created the `placement id`, write it down and continue to next section.

#####Suggestion:
**Use CocaPods If you want to save your time fixing random issue**

#### 2.2. Android

If you are using [`react-native-fbsdk`](https://github.com/facebook/react-native-fbsdk) you can follow their installation instructions. Otherwise, please follow official [Getting Started Guide for Facebook SDK](https://developers.facebook.com/docs/android/getting-started).

### Interstitial Ads

Interstitial Ad is a type of an ad that displays full screen with media content. It has a dismiss button as well as the clickable area that takes user outside of your app.

<img src="https://cloud.githubusercontent.com/assets/2464966/19014517/3cea1da2-87ef-11e6-9f5a-6f3dbccc18a2.png" height="500">

They are displayed over your root view with a single, imperative call.

#### 1. Showing ad

In order to show an ad, you have to import `InterstitialAdManager` and call `showAd` on it supplying it a placementId identifier, as in the below example:

```js
import { InterstitialAdManager } from 'react-native-ads-facebook';

InterstitialAdManager.showAd(placementId)
  .then(didClick => {})
  .catch(error => {})
```

Method returns a promise that will be rejected when an error occurs during a call (e.g. no fill from ad server or network error) and resolve when user either dimisses or interacts with the displayed ad.

### Native Ads

Native Ad is a type of an ad that matches the form and function of your React Native interface.

![Facebook Native Ad](./images/nativeAd.png =150)
<img style='width: 600px' src="images/nativeAd.png"></img>

#### 1. Creating AdsManager

In order to start rendering your custom native ads within your app, you have to construct
a `NativeAdManager` that is responsible for caching and fetching ads as you request them.

```js
import { NativeAdsManager } from 'react-native-ads-facebook';

const adsManager = new NativeAdsManager(placementId, numberOfAdsToRequest);
```

The constructor accepts two parameters:
- `placementId` - which is an unique identifier describing your ad units,
- `numberOfAdsToRequest` - which is a number of ads to request by ads manager at a time

#### 2. Making ad component

After creating `adsManager` instance, next step is to wrap an arbitrary component that you want to
use for rendering your custom advertises with a `withNativeAd` wrapper.

It's a higher order component that passes `nativeAd` via props to a wrapped component allowing
you to actually render an ad!

The `nativeAd` object can contain the following properties:

- `advertiserName` - The name of the Facebook Page or mobile app that represents the business running each ad.
- `headline` - The headline that the advertiser entered when they created their ad. This is usually the ad's main title.
- `linkDescription` - Additional information that the advertiser may have entered.
- `translation` - The word 'ad', translated into the language based upon Facebook app language setting.
- `promotedTranslation` - The word 'promoted', translated into the language based upon Facebook app language setting.
- `sponsoredTranslation` - The word 'sponsored', translated into the language based upon Facebook app language setting.
- `bodyText` - Ad body
- `callToActionText` - Call to action phrase, e.g. - "Install Now"
- `socialContext` - social context for the Ad, for example "Over half a million users"


** Note: ** Don't use more than one MediaView/AdIconView component within one native ad.

** Note: ** To make any text `Triggerable` wrap it in <TriggerableView></TriggerableView> use only <Text /> component


```js
import { AdIconView,MediaView,TriggerableView } from 'react-native-ads-facebook';
class AdComponent extends React.Component {
  render() {
    return (
      <View>
        <AdIconView />
        <MediaView />
        <TriggerableView>
            <Text>{this.props.nativeAd.description}</Text>
        </TriggerableView>
      </View>
    );
  }
}

export default withNativeAd(AdComponent);
```

#### 3. Rendering an ad

Finally, you can render your wrapped component from previous step and pass it `adsManager`
of your choice.

##### Adchoice position props

| prop | default | required | params | description |
|------------------|----------|----------|-----------------------------------------------------------------------------|----------------------------------|
| adsManager | null | true | `const adsManager = new NativeAdsManager(placementId, numberOfAdsToRequest)` | Set Placement id for native ad |
| adChoicePosition | topRight | false | `topLeft , topRight , bottomLeft , bottomRight` | Set Ad choice position |
| expandable | true | false | BOOLEAN | IOS only set Adchoice expandable |

```js
class MainApp extends React.Component {
  render() {
    return (
      <View>
        <AdComponent adsManager={adsManager} adChoicePosition="topLeft" expandable={false} />
      </View>
    );
  }
}
```

### BannerView

BannerView is a component that allows you to display native banners (know as *AdView*).

Banners are available in 3 sizes:
- `standard` (BANNER_HEIGHT_50)
- `large` (BANNER_HEIGHT_90)
- `rectangle` (RECTANGLE_HEIGHT_250)

#### 1. Showing ad

In order to show an ad, you have to first import it `BannerView` from the package:

```js
import { BannerView } from 'react-native-ads-facebook';
```

Later in your app, you can render it like below:

```js
function ViewWithBanner(props) {
  return (
    <View>
      <BannerView
        placementId="YOUR_BANNER_PLACEMENT_ID"
        type="standard"
        onPress={() => console.log('click')}
        onError={(err) => console.log('error', err)}
      />
    </View>
  );
}
```

## API

### NativeAdsManager

Provides a mechanism to fetch a set of ads and then use them within your application. The native ads manager supports giving out as many ads as needed by cloning over the set of ads it got back from the server which can be useful for feed scenarios. It's a wrapper for [`FBNativeAdsManager`](https://developers.facebook.com/docs/reference/ios/current/class/FBNativeAdsManager/)

#### disableAutoRefresh

By default the native ads manager will refresh its ads periodically. This does not mean that any ads which are shown in the application's UI will be refreshed but simply that requesting next native ads to render may return new ads at different times. This method disables that functionality.

```js
adsManager.disableAutoRefresh();
```

#### setMediaCachePolicy

Sets the native ads manager caching policy. This controls which media from the native ads are cached before being displayed. The default is to not block on caching.

```js
adsManager.setMediaCachePolicy('none' | 'icon' | 'image' | 'all');
```

**Note:** This method is a noop on Android

### InterstitialAdManager

```js
import { InterstitialAdManager } from 'react-native-ads-facebook';
```

InterstitialAdManager is a manager that allows you to display interstitial ads within your app with a single call.

#### showAd

Loads an interstitial ad asynchronously and shows it full screen by attaching a view onto the current root view
controller.

```js
InterstitialAdManager.showAd('placementId')
  .then(...)
  .catch(...);
```

Promise will be rejected when there's an error loading ads from Facebook Audience network. It will resolve with a
`boolean` indicating whether user didClick an ad or not.

On Android you have to add following activity to *AndroidManifest.xml*
```xml
<activity
  android:name="com.facebook.ads.InterstitialAdActivity"
  android:configChanges="keyboardHidden|orientation" />
```

**Note:** There can be only one `showAd` call being performed at a time. Otherwise, an error will be thrown.

### AdSettings

```js
import { AdSettings } from 'react-native-ads-facebook';
```

AdSettings contains global settings for all ad controls.

#### currentDeviceHash

Constant which contains current device's hash id.

#### addTestDevice

Registers given device to receive test ads. When you run app on simulator, it should automatically get added. Use it
to receive test ads in development mode on a standalone phone. Hash of the current device can be obtained from a
debug log or `AdSettings.currentDeviceHash` constant.

All devices should be specified before any other action takes place, like [`AdsManager`](#nativeadsmanager) gets created.

```js
AdSettings.addTestDevice('hash');
```

#### clearTestDevices

Clears all previously set test devices. If you want your ads to respect newly set config, you'll have to destroy and create
an instance of AdsManager once again.

```js
AdSettings.clearTestDevices();
```

#### setLogLevel

Sets current SDK log level.

```js
AdSettings.setLogLevel('none' | 'debug' | 'verbose' | 'warning' | 'error' | 'notification');
```

**Note:** This method is a noop on Android.

#### setIsChildDirected

Configures the ad control for treatment as child-directed.

```js
AdSettings.setIsChildDirected(true | false);
```

#### setMediationService

If an ad provided service is mediating Audience Network in their sdk, it is required to set the name of the mediation service

```js
AdSettings.setMediationService('foobar');
```

#### setUrlPrefix

Sets the url prefix to use when making ad requests.

```js
AdSettings.setUrlPrefix('...');
```

**Note:** This method should never be used in production

## Running example

In order to see ads you will have to create your own `placementId` and use it instead of the one provided in the examples. 

## Switching from react-native-fbads

```bash

react-native uninstall react-native-fbads

react-native install react-native-ads-facebook

change 'react-native-fbads' to 'react-native-ads-facebook' in your import dependencies.

```



### Credits

Some of the API explanations were borrowed from Facebook SDK documentation.
Documentation taken from react-native-fbads
