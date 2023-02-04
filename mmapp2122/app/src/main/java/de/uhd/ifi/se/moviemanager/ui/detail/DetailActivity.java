package de.uhd.ifi.se.moviemanager.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.ConfigurationCompat;

import java.util.Locale;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.ModelObjectWithImage;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess;

/**
 * Abstract superclass of the Detail Views.
 *
 * @param <T> {@link Movie}, {@link Performer}, o
 * @see MovieDetailActivity
 * @see PerformerDetailActivity
 */
public abstract class DetailActivity<T extends ModelObjectWithImage>
        extends AppCompatActivity {
    public static final String CURRENT_OBJECT = "initial_object";
    protected static final MovieManagerModel model = MovieManagerModel
            .getInstance();
    private final int layoutId;
    private final Class<? extends Activity> editActivity;
    protected ActionBar actionBar;
    protected ImageView imageView;

    final ActivityResultLauncher<Intent> updateAfterEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                    updateAfterEdit(data);
                }
            });

    public final ActivityResultLauncher<Intent> updateAfterLinkedDetailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null) {
                    updateAfterLinkedDetails(data);
                }
            });

    // object of {@link Movie} or {@link Performer} data class
    T currentObject;
    private boolean updated;

    protected DetailActivity(@LayoutRes int layoutId,
                             Class<? extends Activity> editActivity) {
        this.layoutId = layoutId;
        this.editActivity = editActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(layoutId);
        StorageManagerAccess.getInstance().openMovieManagerStorage(this);

        currentObject = getIntent().getParcelableExtra(CURRENT_OBJECT);
        if (currentObject == null) {
            finish();
            return;
        }
        setUpdated(false);

        bindViews();
        setupLists();
        registerSpecificListeners();
        setupActionBar();
        updateUIWithModelData();
    }

    /**
     * Binds the views
     */
    protected abstract void bindViews();

    protected abstract void setupLists();

    /**
     * Registers the event listeners specific to the subclass, e.g. to the movie
     * detail view.
     */
    protected abstract void registerSpecificListeners();

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean result = true;

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit:
                Intent intent = new Intent(this, editActivity);
                intent.putExtra(CURRENT_OBJECT, currentObject);
                updateAfterEditLauncher.launch(intent);
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    protected abstract boolean updateAfterEdit(Intent intent);

    /**
     * For example, if you click a linked movie in the Performer DetailView, the
     * Movie DetailView is opened.
     *
     * @param result Intent containing the selected {@link Movie} or {@link
     *               Performer}.
     */
    protected abstract void updateAfterLinkedDetails(Intent result);

    protected abstract void updateUIWithModelData();

    protected void initImageView(ImagePyramid.ImageSize imageSize) {
        imageView = findViewById(R.id.image);
        imageView.setImageDrawable(currentObject.getImage(this, imageSize));
    }

    protected final Locale getCurrentLocale() {
        Configuration config = getResources().getConfiguration();
        return ConfigurationCompat.getLocales(config).get(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(CURRENT_OBJECT, currentObject);
        setResult(updated ? RESULT_OK : RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    protected void setUpdated(boolean updated) {
        this.updated = updated;
    }
}

