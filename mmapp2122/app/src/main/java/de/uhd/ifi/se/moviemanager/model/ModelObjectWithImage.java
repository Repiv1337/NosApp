package de.uhd.ifi.se.moviemanager.model;

import static java.lang.String.format;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;
import java.util.Optional;

/**
 * Superclass for model classes with an image, i.e., for the {@link Movie},
 * {@link Performer}, and {@link Renter} class. The images are modeled in the
 * {@link ImagePyramid} class.
 *
 * This class is abstract and cannot be instantiated.
 */
public abstract class ModelObjectWithImage
        implements Identifiable, Nameable, ImageBased, Comparable, Parcelable {
    private int id;
    private String name;
    protected ImagePyramid image;

    protected MovieManagerModel model = MovieManagerModel.getInstance();

    protected ModelObjectWithImage(Integer id) {
        this.id = id;
        name = "";
        // @decision The image has the same id as the object.
        image = new ImagePyramid(id, getClass());
    }

    /**
     * Necessary to pass objects from one Android activity to another activity
     * via {@link android.content.Intent}s as {@link Parcel}s.
     */
    protected ModelObjectWithImage(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = new ImagePyramid(id, getClass());
        image.setImageUrl(in.readString());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
        // @decision The image has the same id as the object.
        image.setId(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public ImagePyramid getImage() {
        return image;
    }

    @Override
    public void setImage(ImagePyramid image) {
        this.image = image;
    }

    @Override
    public void setImage(Drawable drawable) {
        image.setBitmap(drawable);
    }

    @Override
    public void setImage(ColorDrawable colorDrawable,
                         ImagePyramid.ImageSize imageSize) {
        image.setBitmap(colorDrawable, imageSize);
    }

    @Override
    public void setImage(Bitmap bitmap) {
        image.setBitmap(bitmap);
    }

    @Override
    public void setImage(String imageUrl) {
        image.setImageUrl(imageUrl);
    }

    @Override
    public Drawable getImage(Context context, ImagePyramid.ImageSize size) {
        Optional<Bitmap> bitmap = getImage().getBitmap(size);
        if (bitmap.isPresent()) {
            return new BitmapDrawable(context.getResources(), bitmap.get());
        }
        return ImagePyramid.getDefaultImage(context, size);
    }

    @NonNull
    @Override
    public String toString() {
        return format("%s{id=%s, name=%s}", getClass().getSimpleName(), getId(),
                getName());
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof ModelObjectWithImage)) {
            return false;
        }
        ModelObjectWithImage modelObjectWithImage =
                (ModelObjectWithImage) object;
        return modelObjectWithImage.getId() == id && modelObjectWithImage
                .getName().equals(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object objectToCompare) {
        ModelObjectWithImage objectWithImageToCompare =
                (ModelObjectWithImage) objectToCompare;
        if (getId() > objectWithImageToCompare.getId()) {
            return 1;
        }
        if (getId() == objectWithImageToCompare.getId()) {
            return 0;
        }
        return -1;
    }

    /**
     * Necessary to pass objects from one Android activity to another activity
     * via {@link android.content.Intent}s as {@link Parcel}s.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image.getImageUrl());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
