package de.uhd.ifi.se.moviemanager.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uhd.ifi.se.moviemanager.util.Log;

/**
 * Interface for model classes with an image. This interface is implemented in the {@link
 * ModelObjectWithImage} class, which is the superclass of the {@link Movie}, {@link Performer}, and
 * {@link Renter} classes. The images are modeled in the {@link ImagePyramid} class.
 */
public interface ImageBased {

    /**
     * @return image with three different sizes (small, medium, and large).
     */
    ImagePyramid getImage();

    /**
     * @param image
     *         with three different sizes (small, medium, and large).
     */
    void setImage(ImagePyramid image);

    /**
     * @param context
     *         Android activity.
     * @param size
     *         small, medium, or large.
     * @return {@link Drawable} object of the image.
     */
    Drawable getImage(Context context, ImagePyramid.ImageSize size);

    void setImage(Drawable drawable);

    void setImage(ColorDrawable colorDrawable, ImagePyramid.ImageSize imageSize);

    void setImage(Bitmap image);

    /**
     * @param imageUrl e.g. from wikipedia for wiki sync.
     */
    void setImage(String imageUrl);

    static Bitmap crop(Bitmap inp, int wScale, int hScale) {
        int width = inp.getWidth();
        int height = inp.getHeight();
        int smallestSegment = width / wScale < height / hScale ? width / wScale : height / hScale;
        int newWidth = smallestSegment * wScale;
        int newHeight = smallestSegment * hScale;
        Log.d("DU", "W: " + width + ", H: " + height + ", w: " + newWidth + ", h: " + newHeight);
        Bitmap result = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        result.setDensity(Bitmap.DENSITY_NONE);
        Canvas c = new Canvas(result);
        Log.d("DU", "W: " + result.getWidth() +
                ", H: " + result.getHeight() +
                ", w: " + (newWidth / 2 - width / 2) +
                ", h: " + (newHeight / 2 - height / 2));
        c.drawBitmap(inp, newWidth / 2f - width / 2f, newHeight / 2f - height / 2f, null);
        return result;
    }

    static Bitmap getBitmapFromURL(String imageUrl) {
        Bitmap result = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            result = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e("getBitmapFromURL", e.getMessage());
        }
        return result;
    }
}
