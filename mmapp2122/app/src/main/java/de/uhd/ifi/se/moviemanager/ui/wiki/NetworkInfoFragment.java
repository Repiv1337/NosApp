package de.uhd.ifi.se.moviemanager.ui.wiki;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.RESTART;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.uhd.ifi.se.moviemanager.R;

/**
 * Presents information about the network status (waiting for response, not
 * available, ...) to the user.
 */
public class NetworkInfoFragment extends Fragment {
    private static final String NO_ANIMATION = "no_animation";
    private static final String ROTATE_ANIMATION = "rotate_animation";
    private ImageView showImage;
    @DrawableRes
    private final int imageId;
    private final String message;
    private final String animation;

    private NetworkInfoFragment(@DrawableRes int imageId, String message) {
        this(imageId, message, NO_ANIMATION);
    }

    private NetworkInfoFragment(@DrawableRes int imageId, String message,
                                String animation) {
        this.imageId = imageId;
        this.message = message;
        this.animation = animation;
    }

    public static NetworkInfoFragment noInternetFragment(Context context) {
        return new NetworkInfoFragment(R.drawable.ic_no_internet,
                context.getString(R.string.warning_no_internet));
    }

    public static NetworkInfoFragment noWifiFragment(Context context) {
        return new NetworkInfoFragment(R.drawable.ic_no_wifi,
                context.getString(R.string.warning_no_wifi));
    }

    public static NetworkInfoFragment loadingFragment(Context context) {
        return new NetworkInfoFragment(R.drawable.ic_loading_circle,
                context.getString(R.string.info_loading), ROTATE_ANIMATION);
    }

    public static NetworkInfoFragment noResultFragment(Context context) {
        return new NetworkInfoFragment(R.drawable.ic_search_unsuccessful,
                context.getString(R.string.warning_nothing_found));
    }

    private static Animation rotationAnimation() {
        int fromDegrees = 0;
        int toDegrees = 360;
        float relativeXPivot = 0.5f;
        float relativeYPivot = 0.5f;

        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, relativeXPivot,
                Animation.RELATIVE_TO_SELF, relativeYPivot);
        rotate.setDuration(2000);
        rotate.setRepeatMode(RESTART);
        rotate.setRepeatCount(INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        return rotate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_wiki_sync_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showImage = view.findViewById(R.id.info_image);
        TextView showMessage = view.findViewById(R.id.info_message);

        showImage();
        showMessage.setText(message);
    }

    private void showImage() {
        Context context = getContext();
        if (context != null) {
            Drawable drawable = context.getDrawable(imageId);
            showImage.setImageDrawable(drawable);
            switch (animation) {
                case ROTATE_ANIMATION:
                    showImage.startAnimation(rotationAnimation());
                    break;
                case NO_ANIMATION:
                default:
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        showImage.clearAnimation();
    }
}
