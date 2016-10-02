/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.toro.sample.develop;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import im.ene.lab.toro.sample.R;
import im.ene.toro.exoplayer.ExoVideo;
import im.ene.toro.exoplayer.ExoVideoView;

public class DemoActivity extends AppCompatActivity {

  ExoVideoView videoView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo);

    videoView = (ExoVideoView) findViewById(R.id.demo_video_view);
    ExoVideo video =
        new ExoVideo(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"), "Demo Video");
    videoView.setMedia(video);
  }

  @Override protected void onStart() {
    super.onStart();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      if (!videoView.isPlaying()) {
        videoView.start();
      }
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      videoView.pause();
    }
  }
}