package de.uhd.ifi.se.moviemanager.ui.view;

import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.addSwipeSupport;
import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.setLinearLayoutTo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;
import de.uhd.ifi.se.moviemanager.ui.adapter.MovieReleaseRecyclerViewAdapter;
import de.uhd.ifi.se.moviemanager.ui.dialog.MovieReleaseDialog;


/**
 * Creates a list of {@link MovieRelease}s, which is displayed in a {@link
 * RecyclerView}. It is both responsible for showing and changing {@link
 * MovieRelease}s.
 *
 * It is used in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailEditActivity}
 * for changing {@link MovieRelease}s and in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity}
 * for displaying the list of {@link MovieRelease}s.
 */
public class MovieReleaseView extends FrameLayout {

    private RecyclerView movieReleasesRecyclerView;
    private List<MovieRelease> movieReleases;
    private MovieReleaseRecyclerViewAdapter movieReleaseRecyclerViewAdapter;

    private int itemLayout;

    private boolean editEnable = false;


    public MovieReleaseView(Context context) {
        this(context, null);
    }

    public MovieReleaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieReleaseView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        updateUI();
    }


    private void updateUI() {
        movieReleaseRecyclerViewAdapter.update(movieReleases);
    }

    private void init(AttributeSet attrs) {
        initFromAttributes(attrs);
        if (!editEnable) {
            inflate(getContext(), R.layout.view_movie_release_nonedit, this);
        } else {
            inflate(getContext(), R.layout.view_movie_release, this);
        }

        bindViews();
        setupIfEditDisabled();
        setupMovieReleaseList();
    }

    private void setupIfEditDisabled() {
        if (!editEnable) {
            itemLayout = R.layout.listitem_release;
        } else {
            itemLayout = R.layout.listitem_release_edit;

            ImageButton addMovieRelease = findViewById(R.id.add_release_button);
            addMovieRelease
                    .setOnClickListener(v -> showMovieReleaseCreationDialog());
        }

    }

    private void initFromAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray ta = getContext()
                .obtainStyledAttributes(attrs, R.styleable.MovieReleaseView);
        editEnable = ta.getBoolean(R.styleable.MovieReleaseView_editEnable,
                editEnable);

        ta.recycle();
    }

    private void bindViews() {
        movieReleasesRecyclerView = findViewById(R.id.releases);
    }

    public void setupMovieReleaseList() {
        if (editEnable) {
            movieReleases = new ArrayList<>();
        }

        movieReleaseRecyclerViewAdapter = new MovieReleaseRecyclerViewAdapter(
                getContext(), movieReleases, itemLayout);
        movieReleasesRecyclerView.setAdapter(movieReleaseRecyclerViewAdapter);

        if (editEnable) {
            movieReleaseRecyclerViewAdapter.setOnItemClick(view -> {
                int position = ((RecyclerView.ViewHolder) view.getTag())
                        .getBindingAdapterPosition();
                showMovieReleaseModificationDialog(movieReleases.get(position),
                        position);
            });
        }

        setLinearLayoutTo(getContext(), movieReleasesRecyclerView);
        if (editEnable) {
            addSwipeSupport(getContext(), movieReleasesRecyclerView,
                    R.drawable.ic_delete_enabled, this::deleteMovieRelease);
        }
    }

    /**
     * Shows the dialog to change/modify an existing movie release.
     *
     * @param release  existing {@link MovieRelease} to be changed.
     * @param position of the release in the list of releases, e.g, 0 if it's
     *                 the first release.
     */
    private void showMovieReleaseModificationDialog(MovieRelease release,
                                                    int position) {
        MovieReleaseDialog dialog = MovieReleaseDialog.create(release);

        dialog.setConfirmationListener(changedRelease -> {
            movieReleases.set(position, changedRelease);
            movieReleaseRecyclerViewAdapter.notifyItemChanged(position);
            movieReleaseRecyclerViewAdapter.notifyDataSetChanged();
        });

        dialog.show(
                ((FragmentActivity) getContext()).getSupportFragmentManager(),
                "modify_release_dialog");
    }

    /**
     * Deletes a {@link MovieRelease} in a certain position of the list that is
     * shown in a {@link RecyclerView}.
     *
     * @param viewHolder
     */
    private void deleteMovieRelease(RecyclerView.ViewHolder viewHolder) {
        int pos = viewHolder.getBindingAdapterPosition();

        movieReleases.remove(pos);
        movieReleaseRecyclerViewAdapter.notifyItemRemoved(pos);
        movieReleaseRecyclerViewAdapter
                .notifyItemRangeChanged(pos, movieReleases.size());
        movieReleaseRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Opens the dialog to create a new {@link MovieRelease}.
     */
    private void showMovieReleaseCreationDialog() {
        MovieReleaseDialog dialog = MovieReleaseDialog.create();

        dialog.setConfirmationListener(release -> {
            movieReleases.add(release);
            movieReleaseRecyclerViewAdapter.notifyDataSetChanged();
        });

        dialog.show(
                ((FragmentActivity) getContext()).getSupportFragmentManager(),
                "create_release_dialog");
    }

    public void addListChangedListener(Consumer<Boolean> setChanged) {
        movieReleaseRecyclerViewAdapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        setChanged.accept(true);
                    }
                });
    }

    public List<MovieRelease> getMovieReleases() {
        return movieReleases;
    }

    public void setMovieReleases(List<MovieRelease> movieReleases) {
        this.movieReleases = movieReleases;
        updateUI();
        invalidate();
    }
}
