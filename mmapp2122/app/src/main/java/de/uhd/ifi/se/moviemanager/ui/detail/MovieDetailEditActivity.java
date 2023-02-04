package de.uhd.ifi.se.moviemanager.ui.detail;

import static java.util.stream.Collectors.toList;
import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;
import static de.uhd.ifi.se.moviemanager.util.Listeners.createOnTextChangedListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.dialog.PerformerSafeRemovalDialog;
import de.uhd.ifi.se.moviemanager.ui.view.DateElementView;
import de.uhd.ifi.se.moviemanager.ui.view.MovieReleaseView;
import de.uhd.ifi.se.moviemanager.ui.view.RatingElementView;
import de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryMode;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Responsible for the Movie DetailEditView.
 */
public class MovieDetailEditActivity extends DetailEditActivity<Movie> {
    private ImageButton linkPerformerButton;
    private DataLinkList<Performer> linkedPerformers;

    private MovieReleaseView movieReleaseView;

    private EditText runtimeTextView;
    private EditText descriptionTextView;
    private EditText languagesAttr;
    private EditText productionLocationsAttr;
    private DateElementView watchDateView;
    private RatingElementView ratingElementView;
    private ImageView sign;
    private EditText duedate;
    private DateElementView lenddatechooser;
    Date lend;
    public MovieDetailEditActivity() {
        super(R.layout.activity_movie_detail_edit);
    }

    @Override
    protected void initViewItems() {
        scrollView = findViewById(R.id.attributes_container_scroll);
        nameInput = findViewById(R.id.edit_movie_title);

        watchDateView = findViewById(R.id.edit_watch_date);
        sign = findViewById(R.id.signtest);
        movieReleaseView = findViewById(R.id.edit_movie_releases);
        duedate = findViewById(R.id.lenddate);
        runtimeTextView = findViewById(R.id.edit_runtime);
        ratingElementView = findViewById(R.id.edit_rating);

        descriptionTextView = findViewById(R.id.edit_description);
        languagesAttr = findViewById(R.id.edit_languages);
        productionLocationsAttr = findViewById(R.id.edit_production_locations);

        linkPerformerButton = findViewById(R.id.link_performer_button);
        RecyclerView linkedPerformersRecyclerView = findViewById(
                R.id.linked_performers);
        linkedPerformers = setupLinkedPerformersList(
                linkedPerformersRecyclerView);
        lenddatechooser = findViewById(R.id.lenddatechooser);
        initEditableImageView(ImagePyramid.ImageSize.LARGE);

    }

    /**
     * Initializes the list of performers linked to this movie. The performers
     * are shown in a {@link RecyclerView}.
     *
     * @param recyclerView UI list for the performers.
     * @return list of linked performers as a {@link DataLinkList}.
     */
    private DataLinkList<Performer> setupLinkedPerformersList(
            RecyclerView recyclerView) {
        DataLinkList<Performer> linkedPerformersList = new DataLinkList<>(this,
                recyclerView);
        linkedPerformersList.setOnLinkedDataChanged(() -> setChanged(true));
        return linkedPerformersList;
    }

    @Override
    protected Movie getObject(int id) {
        Movie movie = null;
        if (id >= 0) {
            movie = model.getMovieById(id).orElse(null);
        }
        return movie;
    }

    @Override
    protected void initForCreation() {
        currentObject = new Movie();
        setResetImageButtonEnabled(false);
    }

    @Override
    protected void initForUpdate() {
        setInitialStateFrom(currentObject);
    }

    private void setInitialStateFrom(Movie movie) {
        linkedPerformers.setInitialState(movie.getPerformers());

        movieReleaseView.setMovieReleases(movie.getReleases());
        duedate.setText(DateUtils.dateToText(movie.getDuedate()));
        nameInput.getEditText().setText(movie.getTitle());
        ratingElementView.setRating(movie.getRating());
        watchDateView.setDate(movie.getWatchDate());

        languagesAttr.setText(String.join("\n", movie.getLanguages()));
        runtimeTextView.setText(movie.getRuntime() + "");
        descriptionTextView.setText(movie.getDescription());
        duedate.setText(DateUtils.dateToText(currentObject.getDuedate()));

        productionLocationsAttr
                .setText(String.join("\n", movie.getProductionLocations()));
        if(currentObject.isIsavaliable()){
            sign.setImageResource(R.drawable.isavaliable);

        }
        else {
            sign.setImageResource(R.drawable.notavaliable);
        }
     clickers();
    }
    protected void clickers(){
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentObject.isIsavaliable()==true){
                    if(lenddatechooser.getDate()==null){
                        show();
                        return;
                    }
                    if(DateUtils.differenceInDays(lenddatechooser.getDate(),new Date())>=0){
                        show();
                        return;
                    }
                    sign.setImageResource(R.drawable.notavaliable);
                    lend = lenddatechooser.getDate();
                    duedate.setText(DateUtils.dateToText(lend));
                    setChanged(true);
                    return;
                }
                AlertDialog alertDialog = new AlertDialog.Builder(MovieDetailEditActivity.this)
//set icon
                        .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                        .setTitle("Are you sure to Exit")
//set message
                        .setMessage("Exiting will call finish() method")
//set positive button
                        .setPositiveButton("Recieve Movie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sign.setImageResource(R.drawable.isavaliable);
                                currentObject.recieveMovie();
                                saveAndFinish();
                                finish();
                            }
                        })
//set negative button
                        .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                discardChanges();


                            }
                        })
                        .show();

            }
        });
    }
    public void show(){
        Toast.makeText(this,"Duedate cant be set in the past!",Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void registerSpecificListeners() {
        watchDateView.setDateChangeListener(date -> setChanged(true));
        lenddatechooser.setDateChangeListener(date -> setChanged(true));
        duedate.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        runtimeTextView.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        ratingElementView.setOnRatingChanged(rating -> {
            hideKeyboard();
            setChanged(true);
        });
        descriptionTextView.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        linkPerformerButton
                .setOnClickListener(v -> showPerformerSelectionDialog());

        movieReleaseView.addListChangedListener(this::setChanged);

        languagesAttr.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        productionLocationsAttr.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
    }


    /**
     * Opens the dialog to select performers for the current movie.
     */
    private void showPerformerSelectionDialog() {
        linkedPerformers
                .showSelectionDialog(Performer.class, getNotLinkedPerformers());
    }

    /**
     * @return all performers that are not linked to the selected movie.
     */
    private List<Performer> getNotLinkedPerformers() {
        return model.getPerformers().stream()
                .filter(linkedPerformers::containsNot).collect(toList());
    }

    @Override
    protected void enableOrDisableMenuItems() {
        enableOrDisableSaveButton();
        enableOrDisableWikiSyncButton();
    }

    @Override
    protected void showWarnings() {
        if (getCurrentName().isEmpty()) {
            nameInput.setError(getString(R.string.warning_movie_title));
        } else {
            nameInput.setError(null);
        }
    }

    private void enableOrDisableWikiSyncButton() {
        wikiItem.setEnabled(!getCurrentName().isEmpty());
    }

    @Override
    protected void handleResultOfWikiSync(Intent result) {
        Movie movieFromWiki = result.getParcelableExtra(CURRENT_OBJECT);
        if (movieFromWiki == null) {
            return;
        }
        nameInput.getEditText().setText(movieFromWiki.getTitle());
        duedate.setText(DateUtils.dateToText(movieFromWiki.getDuedate()));
        imageView.setImageBitmap(movieFromWiki.getImage().getCroppedBitmap());
        descriptionTextView.setText(movieFromWiki.getDescription());
        runtimeTextView.setText(movieFromWiki.getRuntime() + "");
        languagesAttr.setText(String.join("\n", movieFromWiki.getLanguages()));
        productionLocationsAttr.setText(
                String.join("\n", movieFromWiki.getProductionLocations()));
        setChanged(true);
    }

    @Override
    protected boolean areConstraintsFulfilled() {
        return !getCurrentName().isEmpty() && getInvalidPerformers().isEmpty();
    }

    private List<Performer> getInvalidPerformers() {
        List<Performer> unlinkedPerformer = linkedPerformers.getUnlinked();
        return PerformerSafeRemovalDialog
                .getInvalidPerformers(unlinkedPerformer);
    }

    @Override
    protected void enableOrDisableSaveButton() {
        // the button is also enabled if a performer has no movies linked
        // anymore but then a dialog
        // is shown that warns that the performer will be deleted
        boolean isButtonEnabled = isChanged && !getCurrentName().isEmpty();
        commitItem.setEnabled(isButtonEnabled);
    }

    @Override
    protected void showCommitWarnings() {
        PerformerSafeRemovalDialog.show(this, getInvalidPerformers(), li -> {
            getInvalidPerformers().forEach(
                    performer -> storage.deletePerformerFile(performer));
            saveAndFinish();
        }, () -> {
        });
    }

    @Override
    protected WikiQueryMode getQueryMode() {
        return WikiQueryMode.MOVIE;
    }

    @Override
    protected void onSave() {
        currentObject.setTitle(getCurrentName());
        currentObject.setRuntime(getRuntime());
        currentObject.setWatchDate(watchDateView.getDate());
        currentObject.setRating(ratingElementView.getRating());
        currentObject.setDescription(descriptionTextView.getText().toString());
        if(currentObject.isIsavaliable()==true && lend!=null) {
            currentObject.lendMovie(lenddatechooser.getDate());
        }
        List<String> languages = Arrays
                .asList(languagesAttr.getText().toString().split("\n"));
        currentObject.setLanguages(languages);

        List<String> productionLocations = Arrays
                .asList(productionLocationsAttr.getText().toString()
                        .split("\n"));
        currentObject.setProductionLocations(productionLocations);

        currentObject.setReleases(movieReleaseView.getMovieReleases());


        model.addMovie(currentObject);

        updateLinkedElements();

        currentObject.calculateOverallRating();
        storage.saveMovieToFile(currentObject);
    }

    private void updateLinkedElements() {
        updateLinkedPerformers();
    }

    private void updateLinkedPerformers() {
        for (Performer performer : linkedPerformers.getUnlinked()) {
            currentObject.unlink(performer);
        }

        for (Performer performer : linkedPerformers) {
            currentObject.link(performer);
        }
    }

    private int getRuntime() {
        String runtimeTextInput = runtimeTextView.getText().toString();
        return !runtimeTextInput.isEmpty() ? Integer
                .parseInt(runtimeTextInput) : 0;
    }
}



