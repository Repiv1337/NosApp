package de.uhd.ifi.se.moviemanager.ui.master;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.model.Performer;

/**
 * Responsible to show a single
 * {@link de.uhd.ifi.se.moviemanager.model.ModelObjectWithImage}
 * in the MasterView, e.g. in the Movie MasterView. Is used in the {@link
 * de.uhd.ifi.se.moviemanager.ui.adapter.DataRVAdapter}.
 *
 * @param <T> e.g. {@link Movie} or {@link Performer}.
 */
public class ContentViewHolder<T extends Identifiable & Nameable & ImageBased & Parcelable>
        extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final TextView titleView;
    private final TextView metadataView;
    private final TextView contentWarning;
    private final TextView label;
    private final int textColor;
    private T modelObject;

    public ContentViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.content_image);
        titleView = itemView.findViewById(R.id.content_title);
        metadataView = itemView.findViewById(R.id.content_subtitle);
        textColor = metadataView.getCurrentTextColor();
        contentWarning = itemView.findViewById(R.id.content_warning);
        label = itemView.findViewById(R.id.content_label);
        itemView.setTag(this);
    }

    public void resetLayout() {
        metadataView.setTextColor(textColor);
        contentWarning.setVisibility(View.VISIBLE);
        contentWarning.setText("");
        label.setVisibility(View.VISIBLE);
        label.setText("");
    }

    /**
     * @param drawable image of e.g. {@link Movie} or {@link Performer}.
     * @see de.uhd.ifi.se.moviemanager.model.ModelObjectWithImage#getImage(Context,
     * ImagePyramid.ImageSize)
     */
    public void setImage(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    /**
     * @param title or name of e.g. {@link Movie} or {@link Performer}. Is used
     *              for the main title of this view holder.
     */
    public void setTitle(String title) {
        titleView.setText(title);
    }

    /**
     * @return title or name of e.g. {@link Movie} or {@link Performer}.
     */
    public String getTitle() {
        return titleView.getText().toString();
    }

    /**
     * @param subText text that is shown underneath the title/name of the
     *                object.
     */
    public void setSubText(String subText) {
        if (subText != null) {
            metadataView.setText(subText);
        }
    }

    /**
     * @param message warning text to be shown, e.g. OVERDUE for a {@link
     *                Movie}.
     */
    public void setWarning(String message) {
        if (message == null || message.isEmpty()) {
            contentWarning.setVisibility(View.INVISIBLE);
        } else {
            contentWarning.setText(message);
        }
    }

    /**
     * @param labelText additional information, e.g. LENT for a {@link Movie}.
     */
    public void setLabelText(String labelText) {
        if (labelText == null || labelText.isEmpty()) {
            label.setVisibility(View.GONE);
        } else {
            label.setText(labelText);
        }
    }

    /**
     * @return model/data object that is shown in this view holder, e.g. a
     * {@link Movie} or {@link Performer} object.
     */
    public T getModelObject() {
        return modelObject;
    }

    /**
     * @param modelObject model/data object that is shown in this view holder,
     *                    e.g. a {@link Movie} or {@link Performer} object.
     */
    public void setModelObject(T modelObject) {
        this.modelObject = modelObject;
    }
}
