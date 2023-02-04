package de.uhd.ifi.se.moviemanager.ui.detail;

import static de.uhd.ifi.se.moviemanager.ui.dialog.ListDialog.Mode.MULTI;
import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.addSwipeSupport;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ModelObjectWithImage;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.LinkedDataAdapter;
import de.uhd.ifi.se.moviemanager.ui.dialog.ListDialog.ListDialogBuilder;
import de.uhd.ifi.se.moviemanager.ui.dialog.SimpleDialog;
import de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils;

/**
 * Creates the list of e.g. {@link Movie}s or {@link Performer}s in the {@link
 * DetailEditActivity}s.
 * <p>
 * Enables to link new objects. The list is displayed in a{@link RecyclerView}.
 * <p>
 * Uses the {@link LinkedDataAdapter} to map the list of {@link Movie}s or
 * {@link Performer}s to this {@link RecyclerView}.
 * <p>
 * Extends an {@link ArrayList}.
 *
 * @param <T> {@link Movie} or {@link Performer} class.
 */
public class DataLinkList<T extends ModelObjectWithImage> extends ArrayList<T> {

    // Since this class extends ArrayList, it is Serializable.
    // Fields in a Serializable class must themselves be either Serializable
    // or transient.
    private transient AppCompatActivity context;
    private transient RecyclerView recyclerView;
    private transient LinkedDataAdapter<T> adapter;
    private transient List<T> removedItems;
    private transient Runnable onLinkedDataChanged;

    DataLinkList(AppCompatActivity context, RecyclerView recyclerView) {
        this(context, recyclerView, R.layout.listitem_portrayable_detail_edit);
    }

    DataLinkList(AppCompatActivity context, RecyclerView recyclerView,
                 @LayoutRes int listItemLayout) {
        this.context = context;
        this.recyclerView = recyclerView;
        removedItems = new ArrayList<>();
        initAdapter(listItemLayout);
    }

    /**
     * @param data e.g. all {@link Movie}s linked to a {@link Performer}. The
     *             linked movies are shown in the Performer DetailView.
     */
    void setInitialState(List<T> data) {
        addAll(data);
        adapter.update(data);
    }

    /**
     * @param listItemLayout creates the adapter that enables to show a list of
     *                       e.g. {@link Movie}s or {@link Performer}s in a
     *                       {@link RecyclerView}.
     */
    private void initAdapter(@LayoutRes int listItemLayout) {
        RecyclerViewUtils.setLinearLayoutTo(context, recyclerView);
        adapter = new LinkedDataAdapter<>(context, this, listItemLayout);
        recyclerView.setAdapter(adapter);

        addSwipeSupport(context, recyclerView, R.drawable.ic_link_broken,
                this::onUnlinkSelected);
    }

    /**
     * @param viewHolder that holds a single data object in the list, e.g. one
     *                   movie in the list of all movies that are linked to a
     *                   performer.
     */
    private void onUnlinkSelected(RecyclerView.ViewHolder viewHolder) {
        int pos = viewHolder.getBindingAdapterPosition();
        askIfSureToUnlink(pos);
    }

    /**
     * Shows a warning that asks whether the user wants to really unlink.
     *
     * @param position of the data object that should be unlinked (e.g. second
     *                 movie in the list of movies that are linked to a
     *                 performer).
     */
    private void askIfSureToUnlink(int position) {
        int unlinkWarning = R.string.unlink_warning_message;
        T object = get(position);
        boolean isPerformerRemoved = false;
        if (object instanceof Performer && ((Performer) object).getMovies()
                .size() <= 1 && context instanceof MovieDetailEditActivity) {
            isPerformerRemoved = true;
        }
        if (object instanceof Movie && size() <= 1 && context instanceof PerformerDetailEditActivity) {
            isPerformerRemoved = true;
        }

        String message = String
                .format(context.getString(unlinkWarning), object.getName());
        if (isPerformerRemoved) {
            message += " " + context
                    .getString(R.string.warning_performer_self_delete);
        }
        SimpleDialog.warning(context).setMessage(message)
                .setPositiveButtonAction(dialog -> {
                    unlink(position);
                    dialog.dismiss();
                }).setPositiveButtonText(R.string.yes)
                .setNegativeButtonText(R.string.no).show();
    }

    /**
     * @param onLinkedDataChanged method in form of a functional interface that
     *                            determines what happens when the user linked
     *                            or unlinked a data object.
     */
    void setOnLinkedDataChanged(Runnable onLinkedDataChanged) {
        this.onLinkedDataChanged = onLinkedDataChanged;
    }

    /**
     * Executes the method that determines what happens when the user linked or
     * unlinked a data object.
     */
    private void onLinkedDataChanged() {
        onLinkedDataChanged.run();
    }

    /**
     * Called when a list item is deleted/removed.
     *
     * @param position of the item/object/element in the list.
     */
    private void unlink(int position) {
        removedItems.add(get(position));
        remove(position);
        onLinkedDataChanged();
        adapter.notifyItemRemoved(position);
    }

    /**
     * Creates a dialog with a list of {@link Movie}s or {@link Performer}s
     *
     * @param clazz  {@link Movie} or {@link Performer} class.
     * @param sample list of {@link Movie}s or {@link Performer}s.
     */
    void showSelectionDialog(Class<T> clazz, List<T> sample) {
        new ListDialogBuilder<T>(context).setMode(MULTI)
                .setTitle("Select at least one " + clazz.getSimpleName())
                .setItems(sample)
                .setFilterCriterion(this::isObjectMatchingFilterString)
                .setPositive(R.string.confirm, createPositiveListener())
                .setNegative(R.string.cancel, () -> {
                }).show();
    }

    /**
     * Determines which objects are shown in the filtered list.
     *
     * @param object of class {{@link Movie} or {@link Performer}.
     * @param text   to be filtered.
     * @return true if object matches filter text.
     */
    private boolean isObjectMatchingFilterString(T object, CharSequence text) {
        String nameOfObject = object.getName().toLowerCase();
        String filterText = text.toString().toLowerCase();
        return nameOfObject.contains(filterText);
    }

    /**
     * @return method in form of a consumer functional interface that adds all
     * selected data objects to this data link list.
     */
    private Consumer<List<T>> createPositiveListener() {
        return this::linkAll;
    }

    /**
     * @param selectedObjects adds all selected data objects in the link dialog
     *                        (e.g. new movies that should be linked to a
     *                        performer) to the list.
     */
    private void linkAll(List<T> selectedObjects) {
        addAll(selectedObjects);
        onLinkedDataChanged();
        adapter.notifyDataSetChanged();
    }

    /**
     * @param data e.g. movie
     * @return true if the the data object is not shown in the list yet (e.g.
     * true if a movie is not linked to performer yet).
     */
    boolean containsNot(T data) {
        return !contains(data);
    }

    /**
     * @return all data objects that were unlinked, i.e. removed from the list
     * since the user saved the changes for the last time.
     */
    List<T> getUnlinked() {
        return removedItems;
    }

    @Override
    public boolean equals(Object o) {
        // no specific behavior here
        // SonarLint demands this method to be implemented
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        // no specific behavior here
        // SonarLint demands this method to be implemented
        return super.hashCode();
    }
}
