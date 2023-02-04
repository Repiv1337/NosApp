package de.uhd.ifi.se.moviemanager.ui.search;

import static de.uhd.ifi.se.moviemanager.ui.detail.DetailActivity.CURRENT_OBJECT;

import android.content.Intent;

import java.util.Set;

import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailActivity;


public class PerformerSearchActivity extends DataSearchActivity<Performer> {

    public PerformerSearchActivity() {
        super();
    }

    @Override
    protected Set<Performer> getDataObjects() {
        return model.getPerformers();
    }

    @Override
    protected void navigateToDetailsOf(Performer modelObject) {
        Intent intent = new Intent(this, PerformerDetailActivity.class);
        intent.putExtra(CURRENT_OBJECT, modelObject);
        startActivity(intent);
    }
}
