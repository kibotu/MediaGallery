[![Donation](https://img.shields.io/badge/buy%20me%20a%20coffee-brightgreen.svg)](https://www.paypal.me/janrabe/5) [![About Jan Rabe](https://img.shields.io/badge/about-me-green.svg)](https://about.me/janrabe)
# MediaGallery [![](https://jitpack.io/v/kibotu/MediaGallery.svg)](https://jitpack.io/#kibotu/MediaGallery) [![](https://jitpack.io/v/kibotu/MediaGallery/month.svg)](https://jitpack.io/#kibotu/MediaGallery) [![Hits-of-Code](https://hitsofcode.com/github/kibotu/MediaGallery)](https://hitsofcode.com/view/github/kibotu/MediaGallery) [![Javadoc](https://img.shields.io/badge/javadoc-SNAPSHOT-green.svg)](https://jitpack.io/com/github/kibotu/MediaGallery/master-SNAPSHOT/javadoc/index.html) [![Build Status](https://app.travis-ci.com/kibotu/MediaGallery.svg?branch=master)](https://app.travis-ci.com/kibotu/MediaGallery) [![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16) [![Gradle Version](https://img.shields.io/badge/gradle-7.2-green.svg)](https://docs.gradle.org/current/release-notes)  [![Kotlin](https://img.shields.io/badge/kotlin-1.5.31-green.svg)](https://kotlinlang.org/) [![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/kibotu/MediaGallery/master/LICENSE) [![androidx](https://img.shields.io/badge/androidx-brightgreen.svg)](https://developer.android.com/topic/libraries/support-library/refactor)

Simple full screen media gallery

[![Screenshot](sample_big.gif)](sample_big.gif)

Features:

- [x] list of imageMedia objects
- [x] list of video media objects
- [x] asset uris
- [x] hls uris
- [x] file uri
- [x] youtube
- [ ] 360
- [ ] youtube 360
- [x] images
- [x] streaming urls
- [ ] click listener
- [x] zoomable
- [x] translatable
- [x] player controls
- [x] blurry
- [x] crossfade background
- [x] quit button
- [ ] swipe down to quit
- [x] preload
- [ ] preload progressbar
- [x] viewpager indicators
- [x] resume seek position
- [x] scroll to position
- [x] return scroll position
- [x] configuration change events (orientation|screenSize|screenLayout|keyboardHidden)
- [x] no dot indicator if there is only one element

### How to use

```kotlin
MediaGalleryActivity.Builder.with(this) {
    autoPlay = true
    isBlurrable = true
    isTranslatable = true
    isZoomable = true
    showVideoControls = true
    showVideoControlsTimeOut = 1750
    autoPlay = true,
    scrollPosition = 0
    smoothScroll = true
    preload = media.size
    showPageIndicator = true
    media = mutableListOf<MediaData>().apply {
        add(youtubeVideo)
        add(youtube360Video)
        add(youtubeHlsVideo)
        add(assetVideo)
        add(externalStorageVideo)
        add(internalStorageVideo)
        add(hlsVideo)
        add(fileVideo)
    }
}.startActivity()
```

### Style

#### Page Indicator

```xml
    <color name="default_selected_dot_color">@color/colorPrimary</color>
    <color name="default_dot_color">@color/colorPrimaryDark</color>
```

### How to install
```groovy
repositories {
    maven {
	url "https://jitpack.io"
    }
}

dependencies {
    implementation 'com.github.kibotu:MediaGallery:-SNAPSHOT'
}
``` 

### Notes

Follow me on Twitter: [@wolkenschauer](https://twitter.com/wolkenschauer)

Let me know what you think: [jan.rabe@kibotu.net](mailto:jan.rabe@kibotu.net)

Contributions welcome!

### License

<pre>
Copyright 2021 Jan Rabe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
