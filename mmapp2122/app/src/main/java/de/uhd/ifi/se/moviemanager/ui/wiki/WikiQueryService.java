package de.uhd.ifi.se.moviemanager.ui.wiki;

import static de.uhd.ifi.se.moviemanager.util.AndroidStringUtils.generateIdentifier;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import de.uhd.ifi.se.moviemanager.wiki.MediaWiki;

public class WikiQueryService extends IntentService {
    public static final int DEFAULT_REQUEST_ID = -1;

    public static final String PARAMETER_ID = "parameter_id";
    public static final String PARAMETER_QUERY = "parameter_query";
    public static final String PARAMETER_TYPE = "parameter_type";

    public static final int RESULT_EMPTY = 0;
    public static final int RESULT_NON_EMPTY = 1;

    public static final String RESULT_ID = "result_id";
    public static final String RESULT_STATE = "result_state";
    public static final String RESULT_LIST = "result_list";
    public static final String RESULT_CALLBACK =
            "de.uhd.ifi.se.moviemanager" + ".ui.wiki.query";

    public WikiQueryService() {
        super(WikiQueryService.class.getSimpleName());
    }

    private static List<Integer> handleRequest(@NonNull Intent request,
                                               List<JsonObject> resultList) {
        int resultCode = RESULT_NON_EMPTY;
        int id = request.getIntExtra(PARAMETER_ID, DEFAULT_REQUEST_ID);
        String query = request.getStringExtra(PARAMETER_QUERY);
        WikiQueryMode type = WikiQueryMode
                .getFromIntent(request, PARAMETER_TYPE);

        if (type == WikiQueryMode.PERFORMER) {
            resultList.addAll(queryPerformers(query));
        } else if (type == WikiQueryMode.MOVIE) {
            resultList.addAll(queryMovies(query));
        } else {
            resultCode = RESULT_EMPTY;
        }

        if (resultList.isEmpty()) {
            resultCode = RESULT_EMPTY;
        }

        List<Integer> response = new ArrayList<>();
        response.add(id);
        response.add(resultCode);
        return response;
    }

    private static List<JsonObject> queryPerformers(String query) {
        return query(query, MediaWiki::getPerformerWikiPagesByName);
    }

    private static List<JsonObject> query(String query,
                                          Function<String,
                                                  Optional<List<JsonObject>>> retriever) {
        List<JsonObject> result;
        if (query == null || query.isEmpty()) {
            result = new ArrayList<>();
        } else {
            result = retriever.apply(query).orElseGet(ArrayList::new);
        }
        return result;
    }

    private static List<JsonObject> queryMovies(String query) {
        return query(query, MediaWiki::getMovieWikiPagesByTitle);
    }

    public static String wrapQueryResult(List<JsonObject> result) {
        return wrap(WikiQueryActivity.PAGE_QUERY_RESULTS, result);
    }

    private static <T> String wrap(Map<String, T> map, T result) {
        String key = generateIdentifier(map::containsKey);
        map.put(key, result);
        return key;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<JsonObject> result = new ArrayList<>();
        int requestId = DEFAULT_REQUEST_ID;
        int resultCode = RESULT_EMPTY;

        if (intent != null) {
            List<Integer> response = handleRequest(intent, result);
            requestId = response.get(0);
            resultCode = response.get(1);
        }

        publishResult(requestId, resultCode, result);
    }

    private void publishResult(int resultId, int resultCode,
                               List<JsonObject> result) {
        Intent intent = new Intent(RESULT_CALLBACK);
        intent.putExtra(RESULT_ID, resultId);
        intent.putExtra(RESULT_STATE, resultCode);
        intent.putExtra(RESULT_LIST, wrapQueryResult(result));
        sendBroadcast(intent);
    }
}
