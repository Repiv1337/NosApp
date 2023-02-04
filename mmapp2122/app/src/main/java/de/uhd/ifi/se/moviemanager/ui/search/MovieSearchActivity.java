package de.uhd.ifi.se.moviemanager.ui.search;

import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;

import android.content.Intent;

import java.util.Set;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity;

public class MovieSearchActivity extends DataSearchActivity<Movie> {

    public MovieSearchActivity() {
        super();
    }

    @Override
    protected Set<Movie> getDataObjects() {
        return model.getMovies();
    }

    @Override
    protected void navigateToDetailsOf(Movie modelObject) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(CURRENT_OBJECT, modelObject);
        startActivity(intent);
    }
}
