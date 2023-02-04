package de.uhd.ifi.se.moviemanager.ui.movie;

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
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class NonEmptyListMoviesTest {

    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel.getInstance();
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityScenarioRule = new ActivityScenarioRule<>(MovieManagerActivity.class);

    @BeforeClass
    public static void initStorage() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch(MovieManagerActivity.class);
        activityScenario.onActivity(activity -> {
            grant(WRITE_EXTERNAL_STORAGE);
            openStorage(activity);
            clearStorage();
            populateStorageWithMovies();
        });
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    private static void populateStorageWithMovies() {
        Movie starWars = STORAGE.saveMovieToFile(new Movie("Star Wars"));
        model.addMovie(starWars);

        Movie red = STORAGE.saveMovieToFile(new Movie("R.E.D."));
        model.addMovie(red);

        Movie pulpFiction = STORAGE.saveMovieToFile(new Movie("Pulp Fiction"));
        model.addMovie(pulpFiction);
    }

    @Test
    public void testListMoviesWithNonEmptyList() {
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(
                        hasDescendant(withText("Star Wars")),
                        hasDescendant(withText("R.E.D.")),
                        hasDescendant(withText("Pulp Fiction"))
                )
        ));
    }
}
