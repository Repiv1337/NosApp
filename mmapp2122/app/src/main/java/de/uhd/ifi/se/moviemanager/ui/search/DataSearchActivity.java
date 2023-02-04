package de.uhd.ifi.se.moviemanager.ui.search;

import static de.uhd.ifi.se.moviemanager.util.Listeners.liveQueryListener;
import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.setLinearLayoutTo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Set;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ModelObjectWithImage;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess;
import de.uhd.ifi.se.moviemanager.ui.adapter.SearchListAdapter;
import de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity;

public abstract class DataSearchActivity<T extends ModelObjectWithImage>
        extends AppCompatActivity {
    static final StorageManagerAccess STORAGE = StorageManagerAccess
            .getInstance();
    private static final String INITIAL_QUERY = "initial_query";
    private static final String TITLE_ID = "title_id";
    private static final String QUERY_HINT_ID = "query_hint_id";
    protected static MovieManagerModel model = MovieManagerModel.getInstance();
    private final int layoutId;
    private String initialQuery;
    @StringRes
    private int queryHintId;
    @StringRes
    private int titleId;

    private SearchView searchBar;
    private RecyclerView resultList;
    private SearchListAdapter<T> adapter;
    private SearchInfo searchInfo;

    protected DataSearchActivity() {
        layoutId = R.layout.activity_search_list;
    }

    public static void openMovieSearch(Fragment source) {
        openMovieSearch("", source);
    }

    public static void openMovieSearch(String initialQuery, Fragment source) {
        Intent data = new Intent(source.getContext(),
                MovieSearchActivity.class);
        data.putExtra(INITIAL_QUERY, initialQuery);
        data.putExtra(TITLE_ID, R.string.movie_search_title);
        data.putExtra(QUERY_HINT_ID, R.string.search_in_movies_hint);
        source.startActivity(data);
    }

    public static void openPerformerSearch(String initialQuery,
                                           Fragment source) {
        Intent data = new Intent(source.getContext(),
                PerformerSearchActivity.class);
        data.putExtra(INITIAL_QUERY, initialQuery);
        data.putExtra(TITLE_ID, R.string.performer_search_title);
        data.putExtra(QUERY_HINT_ID, R.string.search_in_performers_hint);
        source.startActivity(data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        STORAGE.openMovieManagerStorage(this);

        getParametersFromIntent();
        bindViews();
        setupList();
        setupActionBar();
        setupSearchBar();
    }

    private void getParametersFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            initialQuery = intent.getStringExtra(INITIAL_QUERY);
            titleId = intent
                    .getIntExtra(TITLE_ID, R.string.default_search_title);
            queryHintId = intent
                    .getIntExtra(QUERY_HINT_ID, R.string.default_query_text);
        }
    }

    private void bindViews() {
        searchBar = findViewById(R.id.search_bar);
        resultList = findViewById(R.id.search_results);
        searchInfo = new SearchInfo(this);
    }

    private void setupSearchBar() {
        searchBar.setIconifiedByDefault(false);
        searchBar.setQuery(initialQuery, true);
        searchBar.setQueryHint(getString(queryHintId));
    }

    private void setupList() {
        resultList.setVisibility(View.VISIBLE);
        setLinearLayoutTo(this, resultList);

        configureListAdapter();
        setListeners();
    }

    private void configureListAdapter() {
        adapter = new SearchListAdapter<>(this, getDataObjects(), false);
        resultList.setAdapter(adapter);
    }

    protected abstract Set<T> getDataObjects();

    private void setListeners() {
        adapter.setOnItemClickListener(this::navigateToDetailsOf);
        searchBar.setOnQueryTextListener(
                liveQueryListener(this, this::onQueryChanged));
    }

    private boolean onQueryChanged(String query) {
        adapter.filter(query);
        searchInfo.setVisibility(View.INVISIBLE);
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(titleId);
        }
    }

    /**
     * Called when the user clicks a list entry, i.e. a certain model object.
     * Opens the respective detail view, i.e. {@link DetailActivity}.
     *
     * @param modelObject selected {@link Movie} or {@link Performer}.
     */
    protected abstract void navigateToDetailsOf(T modelObject);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent();
        setResult(RESULT_OK, backIntent);
        super.onBackPressed();
    }
}

