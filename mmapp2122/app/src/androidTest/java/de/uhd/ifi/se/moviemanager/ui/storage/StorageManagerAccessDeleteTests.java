package de.uhd.ifi.se.moviemanager.ui.storage;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.moviemanager.MovieManagerActivity;
import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils;


import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.rule.GrantPermissionRule.grant;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.STORAGE;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.openStorage;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class StorageManagerAccessDeleteTests {
    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel.getInstance();
    private static final String movieTitle = "Star Wars";
    private static final String performerName = "Jared Leto";
    private static final String renterName = "David";
    private static final String tagName = "famous";
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityScenarioRule = new ActivityScenarioRule<>(MovieManagerActivity.class);

    @BeforeClass
    public static void init() {
        initStorage();
        clearStorage();
        populateAndDepopulateStorage();
        closeActivity();
        initStorage();
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        UiTestUtils.clearStorage();
    }

    private static void initStorage() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch(MovieManagerActivity.class);
        activityScenario.onActivity(activity -> {
            grant(WRITE_EXTERNAL_STORAGE);
            openStorage(activity);
        });
    }

    private static void populateAndDepopulateStorage() {
        Movie m = STORAGE.saveMovieToFile(new Movie(movieTitle));
        model.addMovie(m);

        Performer p = STORAGE.savePerformerToFile(new Performer(performerName));
        model.addPerformer(p);

        STORAGE.deleteMovieFile( m );
        STORAGE.deletePerformerFile( p );
    }

    public static void closeActivity() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch(MovieManagerActivity.class);
        activityScenario.close();
    }

    @Test
    public void testDeleteMovieToFile() {
        onView(withId(R.id.model_objects_with_image)).check(matches(not(hasDescendant(withText(movieTitle)))));
    }

    @Test
    public void testDeletePerformerToFile() {
        onView(withId( R.id.bottom_navigation_performers)).perform(click());
        onView(withId(R.id.model_objects_with_image)).check(matches(not(hasDescendant(withText(performerName)))));
    }

}
