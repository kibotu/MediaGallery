[![Donation](https://img.shields.io/badge/buy%20me%20a%20coffee-brightgreen.svg)](https://www.paypal.me/janrabe/5) [![About Jan Rabe](https://img.shields.io/badge/about-me-green.svg)](https://about.me/janrabe)
# MediaGallery [![](https://jitpack.io/v/kibotu/MediaGallery.svg)](https://jitpack.io/#kibotu/MediaGallery) [![](https://jitpack.io/v/kibotu/MediaGallery/month.svg)](https://jitpack.io/#kibotu/MediaGallery) [![Hits-of-Code](https://hitsofcode.com/github/kibotu/MediaGallery)](https://hitsofcode.com/view/github/kibotu/MediaGallery) [![Javadoc](https://img.shields.io/badge/javadoc-SNAPSHOT-green.svg)](https://jitpack.io/com/github/kibotu/MediaGallery/master-SNAPSHOT/javadoc/index.html) [![Build Status](https://travis-ci.org/kibotu/MediaGallery.svg)](https://travis-ci.org/kibotu/MediaGallery)  [![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16) [![Gradle Version](https://img.shields.io/badge/gradle-5.6.1-green.svg)](https://docs.gradle.org/current/release-notes)  [![Kotlin](https://img.shields.io/badge/kotlin-1.3.50-green.svg)](https://kotlinlang.org/) [![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/kibotu/MediaGallery/master/LICENSE) [![androidx](https://img.shields.io/badge/androidx-brightgreen.svg)](https://developer.android.com/topic/libraries/support-library/refactor)

Simple full screen media gallery

[![Screenshot](sample_big.gif)](sample_big.gif)

Features:

- [x] list of imageMedia objects
- [ ] list of video media objects
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
- [ ] viewpager indicators
- [x] resume seek position
- [x] scroll to position
- [ ] return scroll position

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

### How to install

	repositories {
	    maven {
	        url "https://jitpack.io"
	    }
	}

	dependencies {
        implementation 'com.github.kibotu:MediaGallery:-SNAPSHOT'
    }

### License

<pre>
Copyright 2019 Jan Rabe

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