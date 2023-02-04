package de.uhd.ifi.se.moviemanager.ui.wiki;

import static java.util.Optional.ofNullable;
import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryMode.UNDEFINED;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.PARAMETER_ID;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.PARAMETER_QUERY;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.PARAMETER_TYPE;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.RESULT_LIST;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.RESULT_NON_EMPTY;
import static de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryService.RESULT_STATE;
import static de.uhd.ifi.se.moviemanager.util.Listeners.liveQueryListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.detail.DetailEditActivity;
import de.uhd.ifi.se.moviemanager.wiki.MediaWiki;

/**
 * Queries movies or performers from Wikipedia and shows the results in the
 * {@link WikiQueryResultFragment}.
 */
public class WikiQueryActivity extends NetworkActivity<String> {
    public static final String EXTRA_INITIAL_QUERY = "initial_query";
    public static final String EXTRA_QUERY_MODE = "query_mode";
    protected static final Map<String, List<JsonObject>> PAGE_QUERY_RESULTS =
            new ConcurrentHashMap<>();

    private SearchView search;
    private ImageView refreshButton;
    private Button forwardButton;
    private ImageView forwardArrow;

    private WikiQueryMode mode = UNDEFINED;
    private JsonObject selected;

    /**
     * @param queryResultAsAString text to be parsed into JsonObjects
     * @return list of JsonObjects parsed from a String.
     */
    public static List<JsonObject> unwrapQueryResult(
            String queryResultAsAString) {
        return unwrap(PAGE_QUERY_RESULTS, queryResultAsAString);
    }

    private static <T> T unwrap(Map<String, T> map, String key) {
        return map.remove(key);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_sync_query);

        bindViews();

        search.setOnQueryTextListener(
                liveQueryListener(this, this::onQueryConfirmed));
        showFragment(NetworkInfoFragment.loadingFragment(this));
        refreshButton.setOnClickListener(
                view -> onQueryConfirmed("" + search.getQuery()));

        setupFromIntent();
        setForwardListener(this::fetchAndReturnToDetailActivity);
    }

    /**
     * Fills the UI elements.
     */
    private void bindViews() {
        search = findViewById(R.id.search);
        refreshButton = findViewById(R.id.refresh_button);
        forwardButton = findViewById(R.id.forward_button);
        forwardArrow = findViewById(R.id.forward_arrow);
    }

    /**
     * The intent object is created in the Detail EditView when the user selects
     * the wiki sync button.
     *
     * @see DetailEditActivity#syncWithWiki()
     */
    private void setupFromIntent() {
        Optional<Intent> intentOptional = ofNullable(getIntent());

        if (intentOptional.isPresent()) {
            Intent intent = intentOptional.get();
            mode = WikiQueryMode.getFromIntent(intent, EXTRA_QUERY_MODE);
            search.setQuery(intent.getStringExtra(EXTRA_INITIAL_QUERY), true);
        } else {
            throw new IllegalArgumentException(
                    "Query Activity was called without parameters!");
        }
    }

    private void setForwardListener(Runnable action) {
        forwardButton.setOnClickListener(view -> action.run());
        forwardArrow.setOnClickListener(view -> action.run());
    }

    /**
     * Completes the movie or performer in the respective DetailEditView with
     * the data from the wiki.
     */
    private void fetchAndReturnToDetailActivity() {
        Intent intent = new Intent();
        if (mode == WikiQueryMode.MOVIE) {
            Optional<Movie> movieOpt = MediaWiki
                    .getMovieDataFromWikiPage(selected);
            if (!movieOpt.isPresent()) {
                return;
            }
            intent.putExtra(CURRENT_OBJECT, movieOpt.get());
        } else {
            Optional<Performer> performerOpt = MediaWiki
                    .getPerformerDataFromWikiPage(selected);
            if (!performerOpt.isPresent()) {
                return;
            }
            intent.putExtra(CURRENT_OBJECT, performerOpt.get());
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    private boolean onQueryConfirmed(String query) {
        disableForward();
        sendRequestIfInternet(query);
        return true;
    }

    private void disableForward() {
        setForwardEnabled(false);
    }

    private void setForwardEnabled(boolean enabled) {
        @ColorRes int colorId = enabled ? R.color.icon_enabled_fill :
                R.color.icon_disabled_fill;
        forwardButton.setEnabled(enabled);
        forwardButton.setTextColor(getColor(colorId));
        forwardArrow.setEnabled(enabled);
    }

    @Override
    protected String callbackIdentifier() {
        return WikiQueryService.RESULT_CALLBACK;
    }

    @Override
    protected void sendRequestToService(int requestId, String requestData) {
        Intent requestList = new Intent(this, WikiQueryService.class);
        requestList.putExtra(PARAMETER_ID, requestId);
        requestList.putExtra(PARAMETER_TYPE, mode.ordinal());
        requestList.putExtra(PARAMETER_QUERY, requestData);
        startService(requestList);
    }

    @Override
    protected void latestServiceResponse(Bundle extras) {
        int resultCode = extras.getInt(RESULT_STATE);
        if (resultCode == RESULT_NON_EMPTY) {
            List<JsonObject> resultList = unwrapQueryResult(
                    extras.getString(RESULT_LIST));
            showFragment(
                    new WikiQueryResultFragment(resultList, this::onSelected));
        } else {
            showFragment(NetworkInfoFragment.noResultFragment(this));
        }
    }

    private void onSelected(JsonObject selected) {
        this.selected = selected;
        setForwardEnabled(selected != null);
    }
}
