package de.uhd.ifi.se.moviemanager.model;

import static android.graphics.BitmapFactory.decodeFile;
import static java.io.File.separator;
import static de.uhd.ifi.se.moviemanager.model.ImageBased.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Optional;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess;

/**
 * Models an image with four different sizes (small, medium, large and wide).
 */
public class ImagePyramid implements Identifiable {

    public static Drawable getDefaultImage(Context context,
                                           ImagePyramid.ImageSize size) {
        switch (size) {
            case LARGE:
                return context.getDrawable(R.drawable.default_image_large);
            case MEDIUM:
                return context.getDrawable(R.drawable.default_image_medium);
            case WIDE:
                return new ColorDrawable(
                        context.getColor(R.color.tag_no_linked));
            case SMALL:
            default:
                return context.getDrawable(R.drawable.default_image_small);
        }
    }

    private static final Bitmap.CompressFormat FORMAT =
            Bitmap.CompressFormat.PNG;

    private int id;
    private String prefix;
    private Bitmap bitmap;
    private String imageUrl;

    public ImagePyramid(int id, Class modelClass) {
        this.id = id;
        setPrefix(modelClass.getSimpleName().toLowerCase());
        imageUrl = "";
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Bitmap getBitmap() {
        if (bitmap != null) {
            return bitmap;
        }
        return ImageBased.getBitmapFromURL(imageUrl);
    }

    public Bitmap getCroppedBitmap() {
        return crop(getBitmap(), 2, 3);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * @param imageUrl e.g. on wikipedia as a String.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return imageUrl e.g. on wikipedia as a String.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public void setBitmap(Drawable drawable) {
        bitmap = ((BitmapDrawable) drawable).getBitmap();
    }

    public void setBitmap(ColorDrawable colorDrawable, ImageSize imageSize) {
        bitmap = convertColorDrawableToBitmap(colorDrawable, imageSize);
    }

    private Bitmap convertColorDrawableToBitmap(ColorDrawable colorDrawable,
                                                ImageSize imageSize) {
        Bitmap colorBitmap = Bitmap
                .createBitmap(imageSize.width, imageSize.height,
                        Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(colorBitmap);
        colorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        colorDrawable.draw(canvas);
        return colorBitmap;
    }

    public String getFileName() {
        return prefix + "_" + id + "." + FORMAT.name().toLowerCase();
    }

    public String getPath(ImageSize size) {
        String imagePath = StorageManagerAccess.getInstance().getImagePath();
        return imagePath + separator + size.folder + separator + getFileName();
    }

    /**
     * Loads the image from the file system.
     */
    public Optional<Bitmap> getBitmap(ImageSize size) {
        String path = getPath(size);
        if (!new File(path).exists()) {
            return Optional.empty();
        }
        bitmap = decodeFile(path);
        return Optional.ofNullable(bitmap);
    }

    @NonNull
    @Override
    public String toString() {
        return "ImagePyramid{id=" + id + ", fileName='" + getFileName() + "'}";
    }

    public enum ImageSize {
        SMALL("small", 50, 75), MEDIUM("medium", 100, 150), LARGE("large", 200,
                300), WIDE("wide", 500, 20);

        public final String folder;
        public final int width;
        public final int height;

        ImageSize(String folder, int width, int height) {
            this.folder = folder;
            this.width = width;
            this.height = height;
        }
    }
}
