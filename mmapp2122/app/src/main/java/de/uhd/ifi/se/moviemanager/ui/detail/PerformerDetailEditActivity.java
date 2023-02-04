package de.uhd.ifi.se.moviemanager.ui.detail;

import static java.util.stream.Collectors.toList;
import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;
import static de.uhd.ifi.se.moviemanager.util.Listeners.createOnTextChangedListener;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uhd.ifi.se.moviemanager.MovieManagerActivity;
import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.dialog.SimpleDialog;
import de.uhd.ifi.se.moviemanager.ui.view.DateElementView;
import de.uhd.ifi.se.moviemanager.ui.view.RatingElementView;
import de.uhd.ifi.se.moviemanager.ui.wiki.WikiQueryMode;

/**
 * Responsible for the Performer DetailView.
 */
public class PerformerDetailEditActivity extends DetailEditActivity<Performer> {
    private RecyclerView linkedMoviesList;
    private ImageButton linkMoviesButton;
    private DataLinkList<Movie> linkedMovies;

    private DateElementView dateOfBirthDateView;
    private EditText birthNameView;
    private EditText biographyTextView;
    private EditText occupationsView;
    private RatingElementView ratingElementView;

    public PerformerDetailEditActivity() {
        super(R.layout.activity_performer_detail_edit);
    }

    @Override
    protected void initViewItems() {
        scrollView = findViewById(R.id.attributes_container_scroll);
        nameInput = findViewById(R.id.edit_performer_name);
        dateOfBirthDateView = findViewById(R.id.date_of_birth);
        ratingElementView = findViewById(R.id.edit_performer_rating);
        TextInputLayout birthNameInputLayout = findViewById(
                R.id.edit_birth_name);
        birthNameView = birthNameInputLayout.getEditText();
        biographyTextView = findViewById(R.id.edit_performer_biography);
        linkedMoviesList = findViewById(R.id.linked_movies_list);
        linkMoviesButton = findViewById(R.id.link_movie_button);
        occupationsView = findViewById(R.id.edit_occupations);

        initEditableImageView(ImagePyramid.ImageSize.LARGE);

        setupLinkedMoviesList();
    }

    private void setupLinkedMoviesList() {
        linkedMovies = new DataLinkList<>(this, linkedMoviesList);
        linkedMovies.setOnLinkedDataChanged(() -> setChanged(true));
    }

    @Override
    protected void initForCreation() {
        currentObject = new Performer();

        linkedMovies.showSelectionDialog(Movie.class,
                new ArrayList<>(model.getMovies()));

        setResetImageButtonEnabled(false);
    }

    @Override
    protected Performer getObject(int id) {
        Performer result = null;
        if (id >= 0) {
            result = model.getPerformerById(id).orElse(null);
        }
        return result;
    }

    @Override
    protected void initForUpdate() {
        linkedMovies.setInitialState(currentObject.getMovies());
        ratingElementView.setRating(currentObject.getRating());
        dateOfBirthDateView.setDate(currentObject.getDateOfBirth());
        biographyTextView.setText(currentObject.getBiography());
        occupationsView
                .setText(String.join("\n", currentObject.getOccupations()));
        nameInput.getEditText().setText(currentObject.getName());
        birthNameView.setText(currentObject.getBirthName());
    }

    @Override
    protected void registerSpecificListeners() {
        dateOfBirthDateView.setDateChangeListener(date -> setChanged(true));
        ratingElementView.setOnRatingChanged(rating -> {
            hideKeyboard();
            setChanged(true);
        });
        birthNameView.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        biographyTextView.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        occupationsView.addTextChangedListener(
                createOnTextChangedListener(this::setChanged));
        linkMoviesButton.setOnClickListener(view -> linkedMovies
                .showSelectionDialog(Movie.class, getNotLinkedMovies()));
    }


    private List<Movie> getNotLinkedMovies() {
        return model.getMovies().stream().filter(linkedMovies::containsNot)
                .collect(toList());
    }

    @Override
    protected void enableOrDisableMenuItems() {
        enableOrDisableSaveButton();
        changeWikiEnable();
    }

    @Override
    protected boolean areConstraintsFulfilled() {
        return !getCurrentName().isEmpty() && !linkedMovies.isEmpty();
    }

    @Override
    protected WikiQueryMode getQueryMode() {
        return WikiQueryMode.PERFORMER;
    }

    @Override
    protected void showWarnings() {
        boolean emptyTitle = getCurrentName().isEmpty();
        boolean emptyMovies = linkedMovies.isEmpty();
        boolean errorEnabled = !emptyTitle && !emptyMovies;

        if (errorEnabled) {
            nameInput.setError(null);
        } else {
            List<String> warnings = new ArrayList<>();
            if (emptyTitle) {
                warnings.add(getString(R.string.warning_performer_name));
            }
            if (emptyMovies) {
                String emptyMovieWarning = getString(
                        R.string.warning_performer_minimum_movies);

                if (editMode != EditMode.CREATION) {
                    emptyMovieWarning += getString(
                            R.string.warning_performer_self_delete);
                }

                warnings.add(emptyMovieWarning);
            }
            nameInput.setError(String.join("\n", warnings));
        }
    }

    private void changeWikiEnable() {
        wikiItem.setEnabled(!getCurrentName().isEmpty());
    }

    @Override
    protected void handleResultOfWikiSync(Intent result) {
        Performer performerFromWiki = result.getParcelableExtra(CURRENT_OBJECT);
        if (performerFromWiki == null) {
            return;
        }
        nameInput.getEditText().setText(performerFromWiki.getName());
        imageView.setImageBitmap(
                performerFromWiki.getImage().getCroppedBitmap());
        dateOfBirthDateView.setDate(performerFromWiki.getDateOfBirth());
        birthNameView.setText(performerFromWiki.getBirthName());
        biographyTextView.setText(performerFromWiki.getBiography());
        occupationsView
                .setText(String.join("\n", performerFromWiki.getOccupations()));
        setChanged(true);
    }

    @Override
    protected void showCommitWarnings() {
        SimpleDialog.warning(this)
                .setMessage(R.string.warning_performer_will_be_deleted,
                        getCurrentName())
                .setPositiveButtonAction(this::onChangesConfirmed).show();
    }

    private void onChangesConfirmed(DialogFragment dialog) {
        dialog.dismiss();
        storage.deletePerformerFile(currentObject);

        Intent intent = new Intent(this, MovieManagerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSave() {
        currentObject.setName(getCurrentName());
        currentObject.setBirthName(birthNameView.getText().toString());
        currentObject.setDateOfBirth(dateOfBirthDateView.getDate());
        currentObject.setBiography(biographyTextView.getText().toString());
        currentObject.setRating(ratingElementView.getRating());
        List<String> occupations = Arrays
                .asList(occupationsView.getText().toString().split("\n"));
        currentObject.setOccupations(occupations);
        currentObject.setImage(imageView.getDrawable());

        model.addPerformer(currentObject);

        updateLinkedElements();
        storage.savePerformerToFile(currentObject);
    }

    private void updateLinkedElements() {
        updateLinkedMovies();
    }

    private void updateLinkedMovies() {
        for (Movie movie : linkedMovies.getUnlinked()) {
            movie.unlink(currentObject);
            movie.calculateOverallRating();
            storage.saveMovieToFile(movie);
        }

        for (Movie movie : linkedMovies) {
            currentObject.link(movie);
            movie.calculateOverallRating();
            storage.saveMovieToFile(movie);
        }
    }
}
