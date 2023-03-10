package de.uhd.ifi.se.moviemanager.ui.view;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.function.DoubleConsumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.util.DimensionUtils;

/**
 * Creates a view item in the movie and performer detail views to show and
 * change the rating.
 *
 * It is used in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity}
 * and {@link de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailActivity}
 * classes to show the rating.
 *
 * It is used in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailEditActivity}
 * and in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailEditActivity}
 * classes to change the rating.
 */

public class RatingElementView extends FrameLayout {
    private static final int DEFAULT_STARS = 5;
    private static final float DEFAULT_RATING = -1.0f;
    private static final boolean IS_INDICATOR_DEFAULT = false;

    private StarElement[] stars;
    private int numberOfStars;
    private double newRating;
    private double rating;
    private int activeColor;
    private boolean isIndicator;
    private int starSize;

    private float lastX;

    private DoubleConsumer onRatingChanged;

    public RatingElementView(Context context) {
        super(context);
        init(context, null);
    }

    public RatingElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RatingElementView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingElementView(Context context, AttributeSet attrs,
                             int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.view_custom_rating_bar, this);

        setDefaultValues(context);
        setValuesFromXML(context, attrs);

        ViewGroup root = findViewById(R.id.root);

        createStars(context, root);
        setUnratedColor();
        updateStars(rating);
    }

    private void setDefaultValues(Context context) {
        starSize = (int) getResources().getDimension(R.dimen.star);
        numberOfStars = DEFAULT_STARS;
        activeColor = ContextCompat.getColor(context, R.color.yellow);
        rating = DEFAULT_RATING;
        isIndicator = IS_INDICATOR_DEFAULT;
    }

    private void setValuesFromXML(Context context,
                                  @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.RatingElementView, 0, 0);
            numberOfStars = a
                    .getInt(R.styleable.RatingElementView_stars, numberOfStars);
            starSize = a.getDimensionPixelSize(
                    R.styleable.RatingElementView_starSize, starSize);
            activeColor = a
                    .getInt(R.styleable.RatingElementView_starActiveColor,
                            activeColor);
            isIndicator = a
                    .getBoolean(R.styleable.RatingElementView_isIndicator,
                            isIndicator);
            a.recycle();
        }
    }

    private void createStars(Context context, ViewGroup root) {
        stars = new StarElement[numberOfStars];

        root.addView(createHorizontalSpace(0.5f));

        if (numberOfStars >= 1) {
            stars[0] = new StarElement(context, root);
            stars[0].setSize(starSize);
            stars[0].setActiveColor(activeColor);
        }

        for (int i = 1; i < numberOfStars; i++) {
            root.addView(createHorizontalSpace(1));
            stars[i] = new StarElement(context, root);
            stars[i].setSize(starSize);
            stars[i].setActiveColor(activeColor);
        }

        root.addView(createHorizontalSpace(0.5f));
    }

    private Space createHorizontalSpace(float weight) {
        Space space = new Space(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 1);
        params.weight = weight;
        space.setLayoutParams(params);
        return space;
    }

    private void setUnratedColor() {
        setColor(ContextCompat.getColor(getContext(), R.color.lighter_gray),
                PorterDuff.Mode.SRC_IN);
    }

    private void setColor(int color) {
        setColor(color, PorterDuff.Mode.MULTIPLY);
    }

    private void setColor(int color, PorterDuff.Mode filter) {
        Arrays.stream(stars)
                .forEach(star -> star.setActiveColor(color, filter));
    }

    private void updateStars(double rating) {
        for (int i = 0; i < numberOfStars; ++i) {
            stars[i].setState(rating, i);
        }
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating == this.rating) {
            return;
        }

        if (rating < 0) {
            setUnratedColor();
        } else {
            setColor(activeColor);
        }

        this.rating = rating;
        updateStars(rating);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!isIndicator) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP) {
                performClick();
                setRating(newRating);
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (Math.abs(event.getX() - lastX) > 50) {
                    requestDisallowInterceptTouchEvent(true);
                }
                newRating = positionToRating(event.getX());
            } else if (action == MotionEvent.ACTION_DOWN) {
                lastX = event.getX();
                newRating = positionToRating(event.getX());
            }
            updateStars(newRating);
        }

        return true;
    }

    @Override
    public boolean performClick() {
        if (onRatingChanged != null) {
            onRatingChanged.accept(newRating);
        }
        return super.performClick();
    }

    private float positionToRating(float x) {
        float widthPerState = getWidth() / (numberOfStars * 3f);
        float widthPerStar = (x / widthPerState) / 3f;
        float estimatedRating = (float) Math.round(widthPerStar * 2f) / 2;
        return Math.max(0, estimatedRating);
    }

    public void setOnRatingChanged(DoubleConsumer listener) {
        onRatingChanged = listener;
    }
}

class StarElement {
    private static final int EMPTY_INDEX = 0;
    private static final int HALF_INDEX = 1;
    private static final int FULL_INDEX = 2;

    private final ImageView starView;
    private final Drawable[] stars;

    StarElement(Context context, ViewGroup root) {
        starView = new ImageView(context);
        starView.setLayoutParams(
                new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        setSizeOf(starView, (int) DimensionUtils.dpToPixels(context, 32));
        stars = new Drawable[]{context.getDrawable(
                R.drawable.ic_view_star_empty), context.getDrawable(
                R.drawable.ic_view_star_half), context.getDrawable(
                R.drawable.ic_view_star_full)};

        root.addView(starView);
    }

    private static void setSizeOf(ImageView view, int size) {
        view.getLayoutParams().height = size;
        view.getLayoutParams().width = size;
    }

    void setSize(int size) {
        setSizeOf(starView, size);
    }

    void setActiveColor(int color) {
        setActiveColor(color, PorterDuff.Mode.MULTIPLY);
    }

    void setActiveColor(int color, PorterDuff.Mode filter) {
        starView.setColorFilter(color, filter);
    }

    void setState(double state, int offset) {
        Drawable currentImage;
        if (state < 0.25 + offset) {
            currentImage = stars[EMPTY_INDEX];
        } else if (state < 0.75 + offset) {
            currentImage = stars[HALF_INDEX];
        } else {
            currentImage = stars[FULL_INDEX];
        }
        starView.setImageDrawable(currentImage);
    }

}