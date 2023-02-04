package de.uhd.ifi.se.moviemanager.ui.adapter;

import static androidx.recyclerview.widget.RecyclerView.ViewHolder;
import static de.uhd.ifi.se.moviemanager.model.ImagePyramid.ImageSize.SMALL;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.text.similarity.JaccardDistance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.model.Performer;

/**
 * Enables to show a list of {@link Movie}s or {@link Performer}s in a {@link RecyclerView}. Used in the {@link
 * de.uhd.ifi.se.moviemanager.ui.search.DataSearchActivity} and in the {@link
 * de.uhd.ifi.se.moviemanager.ui.master.SearchMasterFragment}.
 *
 * @param <T> {@link Movie} or {@link Performer} class.
 */
public class SearchListAdapter<T extends Identifiable & Nameable & ImageBased & Parcelable>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final @LayoutRes
    int itemLayout;
    private final LayoutInflater layoutInflater;
    private Set<T> originalData;
    private List<T> filteredData;
    private Consumer<T> onItemClick;
    private IntConsumer onSizeChangeListener;

    public SearchListAdapter(@NonNull Context context, Set<T> originalData) {
        this(context, originalData, true);
    }

    public SearchListAdapter(@NonNull Context context, Set<T> originalData,
                             boolean useSmall) {
        this.context = context;
        this.originalData = originalData;
        layoutInflater = LayoutInflater.from(context);
        filteredData = new ArrayList<>(originalData);

        if (useSmall) {
            itemLayout = R.layout.listitem_model_object_with_image_detail_small;
        } else {
            itemLayout = R.layout.listitem_model_object_with_image_detail;
        }
    }

    private List<T> applyFilter(@NonNull Set<T> originalData,
                                String constraint) {
        if (constraint.isEmpty()) {
            // result list should be empty if user did not provide input
            return new ArrayList<>();
        }
        Map<Double, T> distanceMap = calculateTextualSimilarity(originalData,
                constraint);
        return getFiveTopResults(distanceMap);
    }

    private Map<Double, T> calculateTextualSimilarity(Set<T> modelObjects,
                                                      String searchTerm) {
        // @decision We use a tree map to sort entries by edit distance!
        Map<Double, T> distanceMap = new TreeMap<>();
        for (T modelObject : modelObjects) {
            String name = modelObject.getName();
            // @decision We use JaccardDistance to assess textual similarity!
            double editDistance = new JaccardDistance().apply(name, searchTerm);
            distanceMap.put(editDistance, modelObject);
        }
        return distanceMap;
    }

    private List<T> getFiveTopResults(Map<Double, T> distanceMap) {
        List<T> searchResult = new ArrayList<>();

        // @decision We limit search results to maximal 5!
        int limit = distanceMap.values().size() < 5 ? distanceMap.values()
                .size() : 5;
        Iterator<T> iterator = distanceMap.values().iterator();
        for (int i = 0; i < limit; i++) {
            searchResult.add(iterator.next());
        }
        return searchResult;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = layoutInflater.inflate(itemLayout, parent, false);
        view.setOnClickListener(v -> {
            ViewHolder holder = (ViewHolder) v.getTag();
            int pos = holder.getBindingAdapterPosition();
            T elem = filteredData.get(pos);
            onItemClick.accept(elem);
        });
        ViewHolder holder = new ViewHolder(view) {
        };
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (position >= getItemCount()) {
            return;
        }
        T element = filteredData.get(position);
        ImageView showImage = viewHolder.itemView.findViewById(R.id.show_image);
        TextView showTitle = viewHolder.itemView
                .findViewById(R.id.dialog_title);
        showImage.setImageDrawable(element.getImage(context, SMALL));

        showTitle.setText(element.getName());
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void filter(CharSequence filterText) {
        filteredData = applyFilter(originalData, filterText.toString());
        if (onSizeChangeListener != null) {
            onSizeChangeListener.accept(filteredData.size());
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(Consumer<T> onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setOnSizeChangeListener(IntConsumer listener) {
        onSizeChangeListener = listener;
    }
}
