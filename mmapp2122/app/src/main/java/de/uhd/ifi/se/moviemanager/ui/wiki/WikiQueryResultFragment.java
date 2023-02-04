package de.uhd.ifi.se.moviemanager.ui.wiki;

import static de.uhd.ifi.se.moviemanager.ui.adapter.selection.SelectionAdapters.singleSelection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils;

/**
 * Shows the results of the wiki query, i.e. a list of suggested movies or
 * performers.
 */
public class WikiQueryResultFragment extends Fragment {
    private RecyclerView suggestionsRecyclerView;
    private final List<JsonObject> suggestions;
    private final Consumer<JsonObject> onSelected;

    public WikiQueryResultFragment(List<JsonObject> suggestionsFromWiki,
                                   Consumer<JsonObject> onSelected) {
        this.suggestions = suggestionsFromWiki;
        this.onSelected = onSelected;
    }

    private static String getTitle(JsonObject element) {
        String result = "<EMPTY>";

        JsonElement titleElement = element.get("title");
        if (titleElement != null) {
            result = titleElement.getAsString();
        }

        return result;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_wiki_sync_query_result, container,
                        false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        suggestionsRecyclerView = view.findViewById(R.id.show_query_results);
        setupSuggestionRecyclerView();
    }

    private void setupSuggestionRecyclerView() {
        RecyclerViewUtils
                .setLinearLayoutTo(getContext(), suggestionsRecyclerView);
        suggestionsRecyclerView.setAdapter(
                singleSelection(getContext(), this::selectSuggestion)
                        .setData(suggestions).setElementLayoutId(
                        R.layout.listitem_wiki_sync_query_result)
                        .setContentBinder(this::bindListItem)
                        .setFilterCriterion((element, constraint) -> true)
                        .setOrderCriterion(
                                Comparator.comparing(suggestions::indexOf))
                        .build());
    }

    private void selectSuggestion(JsonObject selectedSuggestion) {
        if (onSelected != null) {
            onSelected.accept(selectedSuggestion);
        }
    }

    private void bindListItem(ViewGroup parent, JsonObject element) {
        TextView queryResultName = parent.findViewById(R.id.query_result_name);
        queryResultName.setText(getTitle(element));
    }
}
