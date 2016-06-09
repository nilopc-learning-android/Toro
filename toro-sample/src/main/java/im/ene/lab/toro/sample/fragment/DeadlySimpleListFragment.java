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

package im.ene.lab.toro.sample.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import im.ene.lab.toro.Toro;
import im.ene.lab.toro.ToroAdapter;
import im.ene.lab.toro.ToroPlayer;
import im.ene.lab.toro.ToroViewHolder;
import im.ene.lab.toro.player.MediaSource;
import im.ene.lab.toro.player.PlaybackException;
import im.ene.lab.toro.player.TrMediaPlayer;
import im.ene.lab.toro.player.widget.VideoPlayerView;
import im.ene.lab.toro.sample.R;
import im.ene.lab.toro.sample.data.SimpleVideoObject;
import im.ene.lab.toro.sample.data.VideoSource;
import im.ene.lab.toro.sample.widget.DividerItemDecoration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 2/6/16.
 */
public class DeadlySimpleListFragment extends Fragment {

  public static final String TAG = "DeadlySimpleListFragment";

  public static DeadlySimpleListFragment newInstance() {
    return new DeadlySimpleListFragment();
  }

  private RecyclerView mRecyclerView;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.generic_recycler_view, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    mRecyclerView.setAdapter(new Adapter());
  }

  @Override public void onResume() {
    super.onResume();
    Toro.register(mRecyclerView);
  }

  @Override public void onPause() {
    Toro.unregister(mRecyclerView);
    super.onPause();
  }

  /**
   * Simple implementation by extending {@link ToroAdapter.ViewHolder} and implementing {@link
   * ToroPlayer}
   */
  private static class SimpleViewHolder extends ToroViewHolder {

    private static final int LAYOUT_RES = R.layout.vh_toro_video_deadly_simple;

    private VideoPlayerView mVideoView;
    private ImageView mThumbnail;

    private SimpleVideoObject mItem;
    private boolean mPlayable = true;

    public SimpleViewHolder(View itemView) {
      super(itemView);
      mVideoView = (VideoPlayerView) itemView.findViewById(R.id.video);
      mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
      mVideoView.setPlayerStateChangeListener(this);
    }

    @Override public void bind(@Nullable Object object) {
      if (!(object instanceof SimpleVideoObject)) {
        throw new IllegalArgumentException("This ViewHolder needs a SimpleVideoObject");
      }

      mItem = (SimpleVideoObject) object;
      mVideoView.setMediaUri(Uri.parse((mItem.video)));
      Picasso.with(itemView.getContext())
          .load(R.drawable.toro_place_holder)
          .fit()
          .centerInside()
          .into(mThumbnail);
    }

    @Override public void onViewHolderBound() {
      // Do nothing
    }

    @Override public void onVideoPrepared(TrMediaPlayer mp) {
      mPlayable = true;
    }

    @Override public boolean onPlaybackError(TrMediaPlayer mp, PlaybackException error) {
      mPlayable = false;
      return super.onPlaybackError(mp, error);
    }

    @Override public void onPlaybackStarted() {
      mThumbnail.setVisibility(View.INVISIBLE);
    }

    @Override public void onPlaybackPaused() {
      mThumbnail.setVisibility(View.VISIBLE);
    }

    @Override public void onPlaybackStopped() {
      mThumbnail.setVisibility(View.INVISIBLE);
    }

    @Override public boolean wantsToPlay() {
      return visibleAreaOffset() >= 0.75 && mPlayable;
    }

    @Nullable @Override public String getVideoId() {
      return (long) mItem.hashCode() + " - " + getAdapterPosition();
    }

    @NonNull @Override public View getVideoView() {
      return mVideoView;
    }

    @Override public void start() {
      mVideoView.start();
    }

    @Override public void pause() {
      mVideoView.pause();
    }

    @Override public void stop() {
      mVideoView.stop();
    }

    @Override public long getDuration() {
      return mVideoView.getDuration();
    }

    @Override public long getCurrentPosition() {
      return mVideoView.getCurrentPosition();
    }

    @Override public void seekTo(long pos) {
      mVideoView.seekTo(pos);
    }

    @Override public boolean isPlaying() {
      return mVideoView.isPlaying();
    }

    @Override public void setBackgroundAudioEnabled(boolean enabled) {
      mVideoView.setBackgroundAudioEnabled(enabled);
    }

    @Override public void setMediaSource(@NonNull MediaSource source) {
      mVideoView.setMediaSource(source);
    }

    @Override public void setMediaUri(Uri uri) {
      mVideoView.setMediaUri(uri);
    }

    @Override public void setVolume(@FloatRange(from = 0.f, to = 1.f) float volume) {
      mVideoView.setVolume(volume);
    }

    @Override public boolean isLoopAble() {
      return true;
    }
  }

  private static class Adapter extends ToroAdapter<SimpleViewHolder> {

    protected List<SimpleVideoObject> mVideos = new ArrayList<>();

    public Adapter() {
      super();
      setHasStableIds(true);
      for (String item : VideoSource.SOURCES) {
        mVideos.add(new SimpleVideoObject(item));
      }
    }

    @Nullable @Override protected Object getItem(int position) {
      return mVideos.get(position % mVideos.size());
    }

    @Override public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(SimpleViewHolder.LAYOUT_RES, parent, false);
      return new SimpleViewHolder(view);
    }

    @Override public int getItemCount() {
      return 100;
    }
  }
}