package de.uhd.ifi.se.moviemanager.ui.master;

import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;
import static de.uhd.ifi.se.moviemanager.ui.search.DataSearchActivity.openMovieSearch;
import static de.uhd.ifi.se.moviemanager.ui.search.DataSearchActivity.openPerformerSearch;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import de.uhd.ifi.se.moviemanager.MovieManagerActivity;
import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess;
import de.uhd.ifi.se.moviemanager.ui.adapter.SearchListAdapter;
import de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity;
import de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailActivity;
import de.uhd.ifi.se.moviemanager.ui.search.CompositeScrollView;
import de.uhd.ifi.se.moviemanager.ui.search.SearchInfo;
import de.uhd.ifi.se.moviemanager.ui.search.SearchResultBlock;
import de.uhd.ifi.se.moviemanager.util.AndroidUtils;
import de.uhd.ifi.se.moviemanager.util.Listeners;

/**
 * Search MasterView.
 */
public class SearchMasterFragment extends Fragment {
    private static final StorageManagerAccess STORAGE = StorageManagerAccess
            .getInstance();
    protected static MovieManagerModel model = MovieManagerModel.getInstance();

    private Activity source;
    private @StringRes
    int nameId;
    private SearchView searchBar;

    private SearchResultBlock movieBlock;
    private SearchListAdapter<Movie> movieAdapter;

    private SearchResultBlock performerBlock;
    private SearchListAdapter<Performer> performerAdapter;

    private SearchInfo info;

    public static SearchMasterFragment newInstance(@StringRes int nameId) {
        SearchMasterFragment fragment = new SearchMasterFragment();
        fragment.nameId = nameId;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        STORAGE.openMovieManagerStorage(getActivity());

        source = getActivity();
        return inflater
                .inflate(R.layout.fragment_master_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        configureListAdapters();
        setupListeners();
    }

    /**
     * @param query given by the user in the Search MasterView.
     * @return
     */
    private boolean onQueryChanged(String query) {
        movieAdapter.filter(query);
        performerAdapter.filter(query);
        info.setVisibility(View.INVISIBLE);
        return true;
    }

    private void bindViews(@NonNull View view) {
        searchBar = view.findViewById(R.id.search_bar);
        CompositeScrollView scrollView = view
                .findViewById(R.id.search_scroll_view);
        scrollView.setOnDispatchListener(
                () -> AndroidUtils.closeKeyboard(source));
        LinearLayout blockRoot = view.findViewById(R.id.content_root);

        movieBlock = createBlock(R.string.movie_search_title,
                R.string.continue_search_in_movies);

        performerBlock = createBlock(R.string.performer_search_title,
                R.string.continue_search_in_performers);

        blockRoot.addView(movieBlock);
        blockRoot.addView(performerBlock);

        info = new SearchInfo(source);
    }

    private SearchResultBlock createBlock(@StringRes int titleId,
                                          @StringRes int continueTextId) {
        SearchResultBlock block = new SearchResultBlock(source);
        block.setName(titleId);
        block.setContinueText(continueTextId);
        block.setVisibility(View.GONE);
        return block;
    }

    private void configureListAdapters() {
        movieAdapter = createAdapter(model.getMovies(), movieBlock,
                this::showMovieFrom);
        performerAdapter = createAdapter(model.getPerformers(), performerBlock,
                this::showPerformerFrom);
    }

    private <T extends Identifiable & Nameable & ImageBased & Parcelable> SearchListAdapter<T> createAdapter(
            Set<T> data, SearchResultBlock block, Consumer<T> listClick) {
        SearchListAdapter<T> result = new SearchListAdapter<>(source, data);
        result.setOnSizeChangeListener(showBlockIfNonEmpty(block));
        result.setOnItemClickListener(listClick);
        block.setAdapter(result);
        return result;
    }

    private IntConsumer showBlockIfNonEmpty(SearchResultBlock block) {
        return size -> {
            int visibility = View.GONE;
            if (size > 0) {
                visibility = View.VISIBLE;
            }
            block.setVisibility(visibility);
            info.setVisibility(View.INVISIBLE);
        };
    }

    private void showMovieFrom(@NonNull Movie elem) {
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra(CURRENT_OBJECT, elem);
        startActivity(intent);
    }

    private void showPerformerFrom(@NonNull Performer elem) {
        Intent intent = new Intent(getActivity(),
                PerformerDetailActivity.class);
        intent.putExtra(CURRENT_OBJECT, elem);
        startActivity(intent);
    }

    private void setupListeners() {
        searchBar.setOnQueryTextListener(
                Listeners.liveQueryListener(source, this::onQueryChanged));

        movieBlock.setContinueListener(
                v -> openMovieSearch(searchBar.getQuery().toString(), this));
        performerBlock.setContinueListener(
                v -> openPerformerSearch(searchBar.getQuery().toString(),
                        this));

        info.addOnClickActionTo(R.id.only_search_movies,
                () -> openMovieSearch("", this));
        info.addOnClickActionTo(R.id.only_search_performers,
                () -> openPerformerSearch("", this));
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
}
