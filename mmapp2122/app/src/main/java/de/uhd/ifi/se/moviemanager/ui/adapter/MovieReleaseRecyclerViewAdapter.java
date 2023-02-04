package de.uhd.ifi.se.moviemanager.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;
import java.util.Objects;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Enables to show a list of
 * {@link de.uhd.ifi.se.moviemanager.model.MovieRelease}s
 * in a {@link RecyclerView}.
 */
public class MovieReleaseRecyclerViewAdapter
        extends RecyclerView.Adapter<ViewHolder> {
    private final LayoutInflater inflater;
    private final int resource;
    private List<MovieRelease> movieReleases;
    private OnClickListener onItemClick;

    public MovieReleaseRecyclerViewAdapter(Context context,
                                           List<MovieRelease> movieReleases,
                                           @LayoutRes int resource) {
        inflater = LayoutInflater.from(context);
        this.movieReleases = movieReleases;
        this.resource = resource;
        onItemClick = v -> {
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(resource, viewGroup, false);
        view.setOnClickListener(onItemClick);
        ViewHolder holder = new ViewHolder(view) {
        };
        view.setTag(holder);
        return holder;
    }

    public void setOnItemClick(OnClickListener onItemClick) {
        this.onItemClick = Objects.requireNonNull(onItemClick);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        MovieRelease release = movieReleases.get(pos);
        TextView view = viewHolder.itemView
                .findViewById(R.id.show_release_location);
        view.setText(release.getLocation());

        view = viewHolder.itemView.findViewById(R.id.show_release_date);
        view.setText(DateUtils.dateToText(release.getDate()));
    }

    @Override
    public int getItemCount() {
        return movieReleases.size();
    }

    /**
     * Updates the {@link RecyclerView} to show a list of movie releases.
     *
     * @param releases list of all {@link MovieRelease}s to be shown.
     */
    public void update(List<MovieRelease> releases) {
        movieReleases = releases;
        notifyDataSetChanged();
    }
}
