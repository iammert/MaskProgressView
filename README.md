# MaskProgressView
Yet another android custom progress view for your music player

# Demo
<img src=""/>

[Youtube Video Link](https://www.youtube.com/watch?v=9ysT9VmaNXU)

# Usage

```java
 <co.mobiwise.library.MaskProgressView
        ...
        app:progressEmptyColor="#4E3C51"
        app:progressLoadedColor="#E91E64"
        app:coverMaskColor="#80000000"
        app:durationTextColor="#FFFFFF"
        app:durationTextSize="15sp"
        app:coverImage="@drawable/cover"
        app:placeHolder="@drawable/cover"
        app:progressHeight="4dp"
        app:maxProgress="40"
        app:currentProgress="23"/>
```

```java
MaskProgressView maskProgressView = (MaskProgressView) findViewById(R.id.maskProgressView);
```

# Listeners
```java
maskProgressView.setOnProgressDraggedListener(new OnProgressDraggedListener() {
      @Override
      public void onProgressDragged(int position) {
          //update your mediaplayer with position
      }

      @Override
      public void onProgressDragging(int position) {
          //update your mediaplayer with position
      }
});
```

```java
maskProgressView.setAnimationCompleteListener(new AnimationCompleteListener() {
      @Override
      public void onAnimationCompleted() {
          //Called when animation completed
      }
  });
```

# Control
```java
maskProgressView.setmMaxSeconds(160); //set current track duration in seconds
maskProgressView.setCoverImage(Bitmap bitmap); //set cover image from loaded bitmap
maskProgressView.setCoverImage(R.drawable.{resource}); //set cover image from resource
maskProgressView.isPlaying() // Check if playing
maskProgressView.start(); // start or resume animation
maskProgressView.pause(); // pause animation
maskProgressView.stop(); //stop animation clears current progress
```

# Design Owner

This design is originally shared by [Dawid Dapszus](https://twitter.com/@Dapszus) on [Dribbble](https://dribbble.com/shots/2159130-CrowdPlayer-Android-app) and [MaterialUp](https://www.materialup.com/posts/crowdplayer-sketch-freebie).
Thanks to [him](https://twitter.com/@Dapszus) such a creative designer.

License
--------


    Copyright 2015 Mert Şimşek.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
