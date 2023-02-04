package de.uhd.ifi.se.moviemanager.ui.adapter;

import static de.uhd.ifi.se.moviemanager.model.ImagePyramid.ImageSize.MEDIUM;
import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity;
import de.uhd.ifi.se.moviemanager.ui.detail.DetailEditActivity;
import de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity;
import de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailEditActivity;
import de.uhd.ifi.se.moviemanager.ui.master.ContentViewHolder;
import de.uhd.ifi.se.moviemanager.ui.master.DataMasterFragment;
import de.uhd.ifi.se.moviemanager.ui.master.HeaderViewHolder;
import de.uhd.ifi.se.moviemanager.ui.master.MovieMasterFragment;
import de.uhd.ifi.se.moviemanager.ui.master.SwipeController;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.CategorizedComparator;
import de.uhd.ifi.se.moviemanager.ui.view.SortingMenuItem;

/**
 * Enables to show a list of {@link Movie}s or {@link Performer}s in a {@link
 * RecyclerView}. Used in the {@link DataMasterFragment}s.
 *
 * @param <T> {@link Movie} or {@link Performer}
 *            class.
 */
public class DataRVAdapter<T extends Identifiable & Nameable & ImageBased & Parcelable>
        extends RecyclerView.Adapter<ViewHolder> {
    private final DataMasterFragment<T> host;
    private final Context context;
    private List<T> modelData;
    private Class<? extends DetailActivity> detailActivityClass;
    private Class<? extends DetailEditActivity> detailEditActivityClass;
    private Function<T, String> getLabelForContentViewHolderMethod;
    private Function<T, String> getWarningForContentViewHolderMethod;
    private Consumer<T> removeFromStorageMethod;

    private String filterString;
    private SortingMenuItem<T> selectedSortingMenuItem;
    /**
     * @decision We store the sorted, filtered, and categorized list entries in
     * a LinkedHashMap with categories as keys and model objects as values!
     */
    private Map<String, List<T>> categorizedData;

    /**
     * @param dataMasterFragment      e.g. {@link MovieMasterFragment} that
     *                                should show the interactive list of {@link
     *                                Movie}s.
     * @param selectedSortingMenuItem activated sorting criterion (e.g. sorting
     *                                regarding name).
     * @param modelData               list of e.g. {@link Movie}s or {@link
     *                                Performer}s.
     * @param filterString            entered by the user.
     */
    public DataRVAdapter(DataMasterFragment<T> dataMasterFragment,
                         SortingMenuItem<T> selectedSortingMenuItem,
                         List<T> modelData, String filterString) {
        host = dataMasterFragment;
        context = dataMasterFragment.getContext();
        this.modelData = modelData;
        // by default no label and warning is set on list entries
        getLabelForContentViewHolderMethod = modelObject -> "";
        getWarningForContentViewHolderMethod = modelObject -> "";

        sortAndFilter(selectedSortingMenuItem, filterString);
    }

    /**
     * Sorts, categorizes and filters the model objects (e.g. {@link Movie}s or
     * {@link Performer}s).
     *
     * @param selectedItem selected {@link SortingMenuItem} by the user, e.g.
     *                     sorting regarding name or rating.
     * @param filterString entered by the user.
     */
    public void sortAndFilter(SortingMenuItem<T> selectedItem,
                              String filterString) {
        this.filterString = filterString;
        selectedSortingMenuItem = selectedItem;
        CategorizedComparator<T> categorizedComparator = selectedItem
                .getCategorizedComparator();
        if (selectedItem.isDescending()) {
            modelData.sort(categorizedComparator.reversed());
        } else {
            modelData.sort(categorizedComparator);
        }
        List<T> filteredData = filter(modelData, filterString);
        categorizedData = categorizeData(filteredData, categorizedComparator);
        notifyDataSetChanged();
    }

    /**
     * @param modelObjects e.g. {@link Movie}s to be filtered by substring.
     * @param filterString e.g. the {@link Movie} title must contain the String
     *                     to be included in the list.
     * @return model objects (e.g. {@link Movie}s) that contain the filter
     * String.
     */
    private List<T> filter(List<T> modelObjects, String filterString) {
        if (filterString.length() == 0) {
            return modelObjects;
        }
        List<T> filteredModelObjects = new ArrayList<>();
        for (T modelObject : modelObjects) {
            if (modelObject.getName().toLowerCase()
                    .contains(filterString.toLowerCase())) {
                filteredModelObjects.add(modelObject);
            }
        }
        return filteredModelObjects;
    }

    /**
     * @param modelObjects          e.g. {@link Movie}s to be categorized.
     * @param categorizedComparator {@link CategorizedComparator}.
     * @return map with categories (e.g. A, B, C for name) as keys and list of
     * model objects that fall into the category as values.
     */
    private Map<String, List<T>> categorizeData(List<T> modelObjects,
                                                CategorizedComparator<T> categorizedComparator) {
        categorizedData = new LinkedHashMap<>();
        for (T modelObject : modelObjects) {
            String category = categorizedComparator
                    .getCategoryNameFor(modelObject);
            List<T> objectsPerCategory = categorizedData.get(category);
            if (objectsPerCategory == null) {
                objectsPerCategory = new ArrayList<>();
            }
            objectsPerCategory.add(modelObject);
            categorizedData.put(category, objectsPerCategory);
        }
        return categorizedData;
    }

    /**
     * Sorts, categorizes and filters the list of e.g. {@link Movie}s by the
     * preset sorting criterion.
     *
     * @param filterString e.g. the {@link Movie} title must contain this String
     *                     to be included in the list.
     */
    public void sortAndFilter(String filterString) {
        sortAndFilter(selectedSortingMenuItem, filterString);
    }

    /**
     * Sorts, categorizes and filters the list of e.g. {@link Movie}s by the
     * preset sorting criterion and filter string.
     */
    public void sortAndFilter() {
        sortAndFilter(filterString);
    }

    /**
     * Called when the user creates a new model object (e.g. {@link Movie}) by
     * pressing the button on the lower right in the master view.
     */
    public void createObject() {
        Intent intent = new Intent(context, getDetailEditActivity());
        intent.putExtra(CURRENT_OBJECT, (T) null);
        host.afterCreationLauncher.launch(intent);
    }

    /**
     * @return detail edit view that is opened when the user selects to create a
     * new model object (e.g. {@link MovieDetailEditActivity}.
     */
    public Class<? extends Activity> getDetailEditActivity() {
        return detailEditActivityClass;
    }

    /**
     * @param detailEditActivityClass detail edit view that is opened when the
     *                                user selects to edit or * create a model
     *                                object (e.g.
     *                                {@link MovieDetailEditActivity}.
     */
    public void setDetailEditActivity(
            Class<? extends DetailEditActivity> detailEditActivityClass) {
        this.detailEditActivityClass = detailEditActivityClass;
    }

    /**
     * Called when the user clicks a list entry, i.e. a certain model object.
     * Opens the respective detail view, i.e. {@link DetailActivity}.
     *
     * @param modelObject selected {@link Movie} or {@link Performer}.
     */
    private void navigateToDetailsOf(@NonNull T modelObject) {
        Intent intent = new Intent(context, getDetailActivity());
        intent.putExtra(CURRENT_OBJECT, modelObject);
        host.afterUpdateLauncher.launch(intent);
    }

    /**
     * @return detail view that is opened when the user selects to view the
     * details of a model object (e.g. {@link MovieDetailActivity}.
     */
    public Class<? extends Activity> getDetailActivity() {
        return detailActivityClass;
    }

    /**
     * @param detailActivityClass detail view that is opened when the user
     *                            selects to view the details of a model object
     *                            (e.g. {@link MovieDetailActivity}.
     */
    public void setDetailActivity(
            Class<? extends DetailActivity> detailActivityClass) {
        this.detailActivityClass = detailActivityClass;
    }

    /**
     * Adds the possibility to remove a list entry (e.g. {@link Movie}) by
     * swiping. Calls {@link #onDeleteSelected(ViewHolder)}.
     *
     * @param recyclerView UI widget that shows a list of e.g. {@link Movie}s in
     *                     the master view.
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        SwipeController swipeController = new SwipeController(context,
                R.drawable.ic_delete_enabled, this::onDeleteSelected);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    /**
     * @param viewHolder selected list entry (e.g. {@link Movie}) to be
     *                   deleted.
     */
    private void onDeleteSelected(@NonNull ViewHolder viewHolder) {
        T model = ((ContentViewHolder<T>) viewHolder).getModelObject();
        removeFromStorageMethod.accept(model);
    }

    /**
     * Called when the user deleted a model object (e.g. {@link Movie}) in the
     * master view (after warning dialog).
     *
     * @param modelObject e.g. {@link Movie} to be deleted.
     */
    public void removeModelObject(T modelObject) {
        modelData.remove(modelObject);
        sortAndFilter();
    }

    /**
     * @param parent   recycler view that the view holder is added to.
     * @param viewType 1 if a {@link HeaderViewHolder} or 0 if a {@link
     *                 ContentViewHolder} is placed at the position.
     * @return either {@link HeaderViewHolder} or {@link ContentViewHolder}.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        switch (viewType) {
            case 0:
                view = layoutInflater
                        .inflate(R.layout.listitem_master_content, parent,
                                false);
                view.setOnClickListener(v -> {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    T modelObject = ((ContentViewHolder<T>) holder)
                            .getModelObject();
                    navigateToDetailsOf(modelObject);
                });
                return new ContentViewHolder<>(view);
            case 1:
            default:
                view = layoutInflater
                        .inflate(R.layout.listitem_master_header, parent,
                                false);
                return new HeaderViewHolder(view);
        }
    }

    /**
     * @param position in the {@link RecyclerView} list. Both {@link
     *                 HeaderViewHolder}s and {@link ContentViewHolder}s are
     *                 list entries.
     * @return 1 if a {@link HeaderViewHolder} or 0 if a {@link
     * ContentViewHolder} is placed at the position.
     */
    @Override
    public int getItemViewType(int position) {
        Object element = getElementByPosition(position);
        if (element instanceof String) {
            return 1; // Header
        } else {
            return 0; // Content
        }
    }

    /**
     * @param position in the {@link RecyclerView} list. Both {@link
     *                 HeaderViewHolder}s and {@link ContentViewHolder}s are
     *                 list entries.
     * @return object at the given position. Is either a String of the category
     * name if there is a {@link HeaderViewHolder} at the position or a model
     * object (e.g. {@link Movie}) if there is a {@link ContentViewHolder} at
     * the position.
     */
    private Object getElementByPosition(int position) {
        int index = 0;
        for (Map.Entry<String, List<T>> entry : categorizedData.entrySet()) {
            if (index == position) {
                return entry.getKey();
            }
            ++index;
            for (T modelObject : entry.getValue()) {
                if (index == position) {
                    return modelObject;
                }
                ++index;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Object element = getElementByPosition(position);
        if (element == null) {
            return;
        }
        if (viewHolder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) viewHolder).setCategoryText(element.toString());
        } else if (viewHolder instanceof ContentViewHolder) {
            bindContentDataToView((ContentViewHolder) viewHolder, (T) element);
        }
    }

    /**
     * @param viewHolder  {@link ContentViewHolder} to show a specific model
     *                    object.
     * @param modelObject e.g. {@link Movie} or {@link Performer} to be shown in
     *                    the recycler view list.
     */
    private void bindContentDataToView(ContentViewHolder<T> viewHolder,
                                       T modelObject) {
        viewHolder.setModelObject(modelObject);
        viewHolder.resetLayout();
        viewHolder.setImage(modelObject.getImage(context, MEDIUM));
        viewHolder.setTitle(modelObject.getName());
        if (selectedSortingMenuItem == null) {
            return;
        }
        viewHolder.setSubText(selectedSortingMenuItem.getCategorizedComparator()
                .getSubText(modelObject));
        viewHolder.setWarning(
                getWarningForContentViewHolderMethod.apply(modelObject));
        viewHolder.setLabelText(
                getLabelForContentViewHolderMethod.apply(modelObject));
    }

    /**
     * @param getLabelMethod method (in form of a functional interface) that
     *                       provides the label for a model object, e.g. "LENT"
     *                       if a {@link Movie} is lent.
     */
    public void setGetLabelForContentViewHolderMethod(
            Function<T, String> getLabelMethod) {
        getLabelForContentViewHolderMethod = getLabelMethod;
    }

    /**
     * @param getWarningMethod method (in form of a functional interface) that
     *                         provides the label for a model object, e.g.
     *                         "OVERDUE" if a {@link Movie} is overdue.
     */
    public void setGetWarningForContentViewHolderMethod(
            Function<T, String> getWarningMethod) {
        getWarningForContentViewHolderMethod = getWarningMethod;
    }

    /**
     * @return number of categories + number of model objects shown in the
     * {@link RecyclerView}.
     */
    @Override
    public int getItemCount() {
        return (int) (categorizedData.keySet().size() + (categorizedData
                .values().stream().flatMap(Collection::stream).count()));
    }

    /**
     * @param removeFromStorageMethod method (in form of a functional interface)
     *                                that is called when a model object (e.g.
     *                                {@link Movie}) is deleted by the user.
     */
    public void setRemoveFromStorageMethod(
            Consumer<T> removeFromStorageMethod) {
        this.removeFromStorageMethod = removeFromStorageMethod;
    }
}