package de.uhd.ifi.se.moviemanager.ui.detail;


import static java.lang.String.format;
import static de.uhd.ifi.se.moviemanager.ui.detail.DetailEditActivity.storage;
import static de.uhd.ifi.se.moviemanager.util.RecyclerViewUtils.setLinearLayoutTo;
import static de.uhd.ifi.se.moviemanager.util.ScrollViewUtils.enableDeepScroll;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.LinkedDataAdapter;
import de.uhd.ifi.se.moviemanager.ui.view.DateElementView;
import de.uhd.ifi.se.moviemanager.ui.view.MovieReleaseView;
import de.uhd.ifi.se.moviemanager.ui.view.RatingElementView;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Responsible for the Movie DetailView.
 */
public class MovieDetailActivity extends DetailActivity<Movie> {


    private TextView showRuntime;
    private RatingElementView showRating;
    private ImageView signAvaliable;
    private TextView overallRatingTitle;
    private TextView duedate;
    private TextView showDescription;

    private RecyclerView linkedPerformersList;
    private LinkedDataAdapter<Performer> linkedPerformersAdapter;

    private TextView showLanguages;



    private TextView showProductionLocations;

    public final ActivityResultLauncher<Intent> updateUIWithModelDataLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    updateUIWithModelData();
                }
            });


    public MovieDetailActivity() {
        super(R.layout.activity_movie_detail, MovieDetailEditActivity.class);
    }

    @Override
    protected void setupLists() {

        setupLinkedPerformersList();
        duedate.setText(DateUtils.dateToText(currentObject.getDuedate()));
        if(currentObject.isIsavaliable()==true){
            signAvaliable.setImageResource(R.drawable.isavaliable);

            return;
        }
        signAvaliable.setImageResource(R.drawable.notavaliable);
        if(currentObject.getDuedate()==null){
            duedate.setText("Not lent");
            return;
        }
        duedate.setText(DateUtils.dateToText(currentObject.getDuedate()));


    }
    @Override
    public void onResume(){
        super.onResume();
         if(currentObject.isIsavaliable()==true){
             signAvaliable.setImageResource(R.drawable.isavaliable);
             duedate.setText("");
             return;
         }
         signAvaliable.setImageResource(R.drawable.notavaliable);
         duedate.setText(DateUtils.dateToText(currentObject.getDuedate()));

    }
    protected void saveAndFinish() {


        Intent intent = new Intent();
        intent.putExtra(CURRENT_OBJECT, currentObject);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }
    void show(){
        Toast.makeText(this,"hello",Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void bindViews() {

        showRuntime = findViewById(R.id.runtime);
        showRating = findViewById(R.id.rating);
        signAvaliable = findViewById(R.id.signavaliable);
        overallRatingTitle = findViewById(R.id.overall_rating_header);
        duedate = findViewById(R.id.duedate);

        showDescription = findViewById(R.id.description);
        linkedPerformersList = findViewById(R.id.linked_performers);
        showLanguages = findViewById(R.id.languages);

        showProductionLocations = findViewById(R.id.production_locations);
    }

    private void setupLinkedPerformersList() {
        setLinearLayoutTo(this, linkedPerformersList);

        linkedPerformersAdapter = new LinkedDataAdapter<>(this,
                currentObject.getPerformers(),
                R.layout.listitem_model_object_with_image_detail);
        linkedPerformersAdapter.setOnItemClickListener(perf -> {
            Intent intent = new Intent(this, PerformerDetailActivity.class);
            intent.putExtra(CURRENT_OBJECT, perf);
            updateAfterLinkedDetailsLauncher.launch(intent);
        });
        linkedPerformersList.setAdapter(linkedPerformersAdapter);
    }

    /**
     * Initializes the list of  {@link MovieRelease}s. The {@link MovieRelease}s
     * are shown in a {@link RecyclerView}. The adapter is necessary to show the
     * {@link MovieRelease}s in the {@link RecyclerView}.
     */


    @Override
    protected void updateAfterLinkedDetails(Intent result) {
        Optional<Movie> currentModelOpt = model
                .getMovieById(currentObject.getId());
        if (currentModelOpt.isPresent()) {
            currentObject = currentModelOpt.get();
            updateUIWithModelData();
        } else {
            finish();
        }
    }

    @Override
    protected boolean updateAfterEdit(Intent intent) {
        Movie movie = intent.getParcelableExtra(CURRENT_OBJECT);
        if (movie == null) {
            return false;
        }
        currentObject = movie;
        setUpdated(true);
        updateUIWithModelData();
        return true;
    }

    @Override
    protected void updateUIWithModelData() {
        initImageView(ImagePyramid.ImageSize.LARGE);
        actionBar.setTitle(currentObject.getTitle());

        showRuntime.setText(
                format(getCurrentLocale(), "%d", currentObject.getRuntime()));
        updateOverallRating();

        showDescription.setText(currentObject.getDescription());
        linkedPerformersAdapter.update(currentObject.getPerformers());

        showLanguages.setText(String.join("\n", currentObject.getLanguages()));



        showProductionLocations.setText(
                String.join("\n", currentObject.getProductionLocations()));
    }

    private void updateOverallRating() {

        double overallRating = currentObject.getOverallRating();

        String overallRatingMessage = getString(R.string.not_rated);
        if (overallRating >= 0) {
            overallRatingMessage = format(Locale.US, "%2.1f", overallRating);
        }

        overallRatingTitle.setText(
                format(getString(R.string.movie_overallRating),
                        overallRatingMessage));

    }

    @Override
    protected void registerSpecificListeners() {
        enableDeepScroll(showDescription);
        enableDeepScroll(showLanguages);
        enableDeepScroll(showProductionLocations);
    }
}