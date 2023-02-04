package de.uhd.ifi.se.moviemanager.ui.master;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.ui.adapter.DataRVAdapter;
import de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity;
import de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailEditActivity;
import de.uhd.ifi.se.moviemanager.ui.dialog.PerformerSafeRemovalDialog;
import de.uhd.ifi.se.moviemanager.ui.dialog.SimpleDialog;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.NameComparator;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.OverallRatingComparator;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.RatingComparator;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.WatchDateComparator;
import de.uhd.ifi.se.moviemanager.ui.view.SortingMenuItem;

/**
 * Movie MasterView
 */
public class MovieMasterFragment extends DataMasterFragment<Movie> {

    @Override
    protected void addSortingMenuItems() {
        String title = getString(R.string.movie_criterion_title);
        String rating = getString(R.string.movie_criterion_rating);
        String overallRating = getString(
                R.string.movie_criterion_overall_rating);
        String watchDate = getString(R.string.movie_criterion_watch_date);

        sortingMenuItems.add(new SortingMenuItem<>(title,
                new NameComparator<>(Movie::getOverallRatingInStars), true));
        sortingMenuItems
                .add(new SortingMenuItem<>(rating, new RatingComparator<>()));
        sortingMenuItems.add(new SortingMenuItem<>(overallRating,
                new OverallRatingComparator()));
        sortingMenuItems.add(new SortingMenuItem<>(watchDate,
                new WatchDateComparator()));
    }

    @Override
    protected DataRVAdapter<Movie> createAdapter() {
        String constraint = filter.getText().toString();
        adapter = new DataRVAdapter<>(this, sortingMenuItems.get(0),
                originalData, constraint);
        adapter.setDetailActivity(MovieDetailActivity.class);
        adapter.setDetailEditActivity(MovieDetailEditActivity.class);
        adapter.setRemoveFromStorageMethod(this::warnAndRemoveFromStorage);
        return adapter;
    }

    @Override
    protected void afterCreation() {
        originalData.clear();
        originalData.addAll(model.getMovies());
        adapter.sortAndFilter();
    }

    @Override
    protected void warnAndRemoveFromStorage(Movie movie) {
        PerformerSafeRemovalDialog
                .showIfNecessary(getActivity(), movie.getPerformers(), li -> {
                    PerformerSafeRemovalDialog
                            .getInvalidPerformers(movie.getPerformers())
                            .forEach(performer -> storage
                                    .deletePerformerFile(performer));
                    storage.deleteMovieFile(movie);
                    adapter.removeModelObject(movie);
                }, () -> {
                }, () -> SimpleDialog.warning(getContext())
                        .setMessage(R.string.deletion_warning_message,
                                movie.getName())
                        .setPositiveButtonAction(dialog -> {
                            storage.deleteMovieFile(movie);
                            adapter.removeModelObject(movie);
                            dialog.dismiss();
                        }).setPositiveButtonText(R.string.yes)
                        .setNegativeButtonText(R.string.no).show());
    }
}
