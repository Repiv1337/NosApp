package de.uhd.ifi.se.moviemanager.ui.detail;

import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.setLinearLayoutTo;
import static de.uhd.ifi.se.moviemanager.util.ScrollViewUtils.enableDeepScroll;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.LinkedDataAdapter;
import de.uhd.ifi.se.moviemanager.ui.view.DateElementView;
import de.uhd.ifi.se.moviemanager.ui.view.RatingElementView;

/**
 * Responsible for the Performer DetailView.
 */
public class PerformerDetailActivity extends DetailActivity<Performer> {

    private DateElementView showDateOfBirth;
    private RatingElementView showRating;
    private TextView showBirthName;
    private TextView showBiography;

    private RecyclerView linkedMoviesList;
    private LinkedDataAdapter<Movie> linkedMoviesAdapter;

    private TextView showOccupations;

    public PerformerDetailActivity() {
        super(R.layout.activity_performer_detail,
                PerformerDetailEditActivity.class);
    }

    @Override
    protected void bindViews() {
        showDateOfBirth = findViewById(R.id.date_of_birth);
        showRating = findViewById(R.id.rating);
        showBirthName = findViewById(R.id.birth_name);
        showBiography = findViewById(R.id.biography);

        linkedMoviesList = findViewById(R.id.linked_movies);

        showOccupations = findViewById(R.id.occupations);
    }

    @Override
    protected void setupLists() {
        setupLinkedMoviesList();
    }

    private void setupLinkedMoviesList() {
        setLinearLayoutTo(this, linkedMoviesList);

        linkedMoviesAdapter = new LinkedDataAdapter<>(this,
                currentObject.getMovies(),
                R.layout.listitem_model_object_with_image_detail);
        linkedMoviesAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(CURRENT_OBJECT, movie);
            updateAfterLinkedDetailsLauncher.launch(intent);
        });
        linkedMoviesList.setAdapter(linkedMoviesAdapter);
    }

    @Override
    protected void registerSpecificListeners() {
        enableDeepScroll(showBiography);
        enableDeepScroll(showOccupations);
    }



    @Override
    protected void updateAfterLinkedDetails(Intent result) {
        Optional<Performer> currentModelOpt = model
                .getPerformerById(currentObject.getId());
        if (currentModelOpt.isPresent()) {
            currentObject = currentModelOpt.get();
            updateUIWithModelData();
        } else {
            finishAfterDeletion(currentObject);
        }
    }

    @Override
    protected void updateUIWithModelData() {
        initImageView(ImagePyramid.ImageSize.LARGE);
        actionBar.setTitle(currentObject.getName());
        showDateOfBirth.setDate(currentObject.getDateOfBirth());
        showRating.setRating(currentObject.getRating());
        showBirthName.setText(currentObject.getBirthName());
        showBiography.setText(currentObject.getBiography());

        linkedMoviesAdapter.update(currentObject.getMovies());

        showOccupations
                .setText(String.join("\n", currentObject.getOccupations()));
    }

    @Override
    protected boolean updateAfterEdit(Intent intent) {
        Performer performer = intent.getParcelableExtra(CURRENT_OBJECT);
        if (performer == null) {
            return false;
        }
        currentObject = performer;
        setUpdated(true);
        updateUIWithModelData();
        return true;
    }

    void finishAfterDeletion(Performer removedPerformer) {
        String msg = String.format(getString(R.string.info_performer_deletion),
                removedPerformer.getName());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
