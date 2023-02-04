package de.uhd.ifi.se.moviemanager.ui.master;

import static androidx.core.content.ContextCompat.getDrawable;
import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.setLinearLayoutTo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uhd.ifi.se.moviemanager.MovieManagerActivity;
import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess;
import de.uhd.ifi.se.moviemanager.ui.adapter.DataRVAdapter;
import de.uhd.ifi.se.moviemanager.ui.adapter.SortingCriteriaAdapter;
import de.uhd.ifi.se.moviemanager.ui.view.SortingMenuItem;
import de.uhd.ifi.se.moviemanager.util.DimensionUtils;
import de.uhd.ifi.se.moviemanager.util.Listeners;

/**
 * Abstract super class for the Movie MasterView and Performer MasterView.
 *
 * @param <T> e.g. {@link Movie} or {@link Performer} data class.
 */
public abstract class DataMasterFragment<T extends Identifiable & Nameable & ImageBased & Parcelable>
        extends Fragment {
    protected StorageManagerAccess storage = StorageManagerAccess.getInstance();
    protected MovieManagerModel model = MovieManagerModel.getInstance();
    protected EditText filter;
    protected List<T> originalData;
    protected List<SortingMenuItem<T>> sortingMenuItems;
    private ImageView sortingButton;
    private int nameId;
    private RecyclerView list;
    protected DataRVAdapter<T> adapter;

    /**
     * Factory method to create the Movie MasterView.
     *
     * @param nameId refers to the name of the fragment (=Movie).
     * @param movies to be shown the master view.
     * @return {@link MovieMasterFragment}.
     */
    public static DataMasterFragment<Movie> newMovieFragmentInstance(
            @StringRes int nameId, Set<Movie> movies) {
        DataMasterFragment<Movie> fragment = new MovieMasterFragment();
        fragment.nameId = nameId;
        fragment.originalData = new ArrayList<>(movies);
        return fragment;
    }

    /**
     * Factory method to create the Performer MasterView.
     *
     * @param nameId     refers to the name of the fragment (=Performer).
     * @param performers to be shown the master view.
     * @return {@link PerformerMasterFragment}.
     */
    public static DataMasterFragment<Performer> newPerformerFragmentInstance(
            @StringRes int nameId, Set<Performer> performers) {
        DataMasterFragment<Performer> fragment = new PerformerMasterFragment();
        fragment.nameId = nameId;
        fragment.originalData = new ArrayList<>(performers);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        storage.openMovieManagerStorage(getActivity());
        return inflater.inflate(R.layout.fragment_master, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.model_objects_with_image);
        filter = view.findViewById(R.id.filter);
        sortingButton = view.findViewById(R.id.criterion_button);
        FloatingActionButton addButton = view.findViewById(R.id.add_button);

        sortingMenuItems = new ArrayList<>();
        addSortingMenuItems();
        setupRecyclerView();

        sortingButton.setOnClickListener(this::openSortingMenu);
        filter.addTextChangedListener(Listeners.createOnTextChangedListenerChar(
                s -> adapter.sortAndFilter(s.toString())));
        addButton.setOnClickListener(v -> adapter.createObject());
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            MovieManagerActivity master = (MovieManagerActivity) activity;
            master.setTitle(nameId);
            master.setBottomNavigationTo(nameId);
        }
    }

    /**
     * Creates the possibility to sort the list of data objects regarding
     * sorting criteria. For example, the list of {@link Movie}s can be sorted
     * regarding name, rating, overall rating, and other criteria.
     */
    protected abstract void addSortingMenuItems();

    /**
     * Called when the user opens the menu to sort the list e.g. alphabetically
     * regarding names/titles.
     *
     * @param anchor master view.
     */
    private void openSortingMenu(View anchor) {
        SortingCriteriaAdapter<T> sortingCriteriaAdapter =
                new SortingCriteriaAdapter<>(
                getContext(), sortingMenuItems);
        Context context = getContext();
        if (context == null) {
            return;
        }
        ListPopupWindow popupMenu = new ListPopupWindow(context);
        popupMenu.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupMenu.setAnchorView(anchor);
        popupMenu.setWidth((int) DimensionUtils.dpToPixels(getContext(), 168));
        popupMenu.setAdapter(sortingCriteriaAdapter);
        popupMenu.setOnItemClickListener((parent, view, pos, id) -> {
            popupMenu.dismiss();
            selectSortingCriterion(pos, sortingMenuItems);
            sortingCriteriaAdapter.notifyDataSetChanged();
        });
        popupMenu.show();
    }

    /**
     * Called when the user selects a specific sorting criterion, e.g. sorting
     * by names.
     *
     * @param selectedIndex index of the sorting criterion.
     * @param items         all {@link SortingMenuItem}s.
     */
    private void selectSortingCriterion(int selectedIndex,
                                        List<SortingMenuItem<T>> items) {
        for (int i = 0; i < items.size(); i++) {
            SortingMenuItem<T> item = items.get(i);
            if (i != selectedIndex) {
                item.setNeutral();
            } else {
                item.activate();
            }
        }

        SortingMenuItem<T> selectedItem = items.get(selectedIndex);
        adapter.sortAndFilter(selectedItem, filter.getText().toString());

        Activity activity = getActivity();
        int resourceId = selectedItem.getSortingMenuItemResource();
        Drawable drawable = getDrawable(activity, resourceId);
        sortingButton.setImageDrawable(drawable);
    }

    /**
     * Initializes the Android {@link RecyclerView} widget to show a list of
     * {@link Movie}s or {@link Performer}s.
     */
    private void setupRecyclerView() {
        list.setVisibility(View.VISIBLE);
        list.setHasFixedSize(true);
        setLinearLayoutTo(getContext(), list);
        list.setAdapter(createAdapter());
    }

    /**
     * Creates the adapter that is necessary to show a list of {@link Movie}s or
     * {@link Performer}s in the Android {@link RecyclerView} widget.
     *
     * @return {@link DataRVAdapter} instance.
     */
    protected abstract DataRVAdapter<T> createAdapter();

    /**
     * Called after a model objects was created by the user.
     */
    protected abstract void afterCreation();

    /**
     * Called after a model objects was changed by the user and the user
     * navigates back from the detail to the master view.
     */
    private void afterUpdate() {
        adapter.sortAndFilter();
    }

    /**
     * Waits for the user to create a model object and then calls {@link
     * #afterCreation()}.
     */
    public final ActivityResultLauncher<Intent> afterCreationLauncher =
            registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (data != null) {
                    afterCreation();
                }
            });

    /**
     * Waits for the user to change a model object and then calls {@link
     * #afterUpdate()}.
     */
    public final ActivityResultLauncher<Intent> afterUpdateLauncher =
            registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result
                        .getResultCode() == Activity.RESULT_OK && data != null) {
                    afterUpdate();
                }
            });

    /**
     * Shows a warning dialog to the user before deleting an object (e.g. {@link
     * Movie}). If the user accepts, the object is deleted from storage.
     *
     * @param modelObject e.g. {@link Movie} that should be deleted.
     */
    protected abstract void warnAndRemoveFromStorage(T modelObject);
}