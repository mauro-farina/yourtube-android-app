package it.units.sim.yourtube;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import it.units.sim.yourtube.model.VideoData;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class VideoPlayerFragment extends Fragment {

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayerWhenReady;
    private VideoData video;
    private boolean isFullscreen;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            video = getArguments().getParcelable("video");
        }

        toggleToolbarAndBottomNav();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        FrameLayout fullscreenViewContainer = view.findViewById(R.id.youtube_player_fullscreen_container);
        getLifecycle().addObserver(youTubePlayerView);

        YouTubePlayerListener playerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayerWhenReady = youTubePlayer;
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youTubePlayerView, youTubePlayer);
                youTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                youTubePlayer.loadVideo(video.getVideoId(), 0);
            }
        };

        IFramePlayerOptions playerOptions = new IFramePlayerOptions.Builder()
                .controls(0)
                .fullscreen(1)
                .build();

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> exitFullscreen) {
                isFullscreen = true;
                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
//                optionally request landscape orientation
//                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onExitFullscreen() {
                isFullscreen = false;
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    youTubePlayerWhenReady.toggleFullscreen();
                } else {
                    toggleToolbarAndBottomNav();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
            }
        };

        youTubePlayerView.initialize(
                playerListener,
                playerOptions
        );

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
        initVideoInfoUi(view);

        return view;
    }

    private void initVideoInfoUi(View view) {
        TextView videoTitle = view.findViewById(R.id.video_player_title);
        TextView videoViewCount = view.findViewById(R.id.video_player_views_counter);
        TextView videoDate = view.findViewById(R.id.video_player_date);
        TextView videoChannelName = view.findViewById(R.id.list_item_subscription_channel_name);
        ImageView videoChannelThumbnail = view.findViewById(R.id.list_item_subscription_thumbnail);
        TextView videoDescription = view.findViewById(R.id.video_player_description);

        videoTitle.setText(video.getTitle());
        videoViewCount.setText("ViewCount");
        videoDate.setText(video.getReadablePublishedDate());
        videoChannelName.setText(video.getChannel().getChannelName());
        videoDescription.setText((video.getDescription()));
        Picasso
                .get()
                .load(video.getChannel().getThumbnailUrl())
                .into(videoChannelThumbnail);
    }

    private void toggleToolbarAndBottomNav() {
        Toolbar topToolbar = requireActivity().findViewById(R.id.top_app_bar);
        if (topToolbar.getVisibility() == View.VISIBLE) {
            topToolbar.setVisibility(View.GONE);
        } else {
            topToolbar.setVisibility(View.VISIBLE);
        }
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

}