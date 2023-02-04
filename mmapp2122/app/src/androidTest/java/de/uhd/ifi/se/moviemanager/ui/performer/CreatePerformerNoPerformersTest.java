package de.uhd.ifi.se.moviemanager.ui.performer;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import org.junit.AfterClass;
import org.junit.Before;
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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.rule.GrantPermissionRule.grant;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.STORAGE;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.selectItemFromMoviePerformerSelectionDialog;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class CreatePerformerNoPerformersTest {

    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel.getInstance();
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityScenarioRule = new ActivityScenarioRule<>(MovieManagerActivity.class);

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

    @Before
    public void prepareTests() {
        clearStorage();
        populateStorageWithMovies();
    }

    @Test
    public void testCreatePerformerWithNoMoviesNoConfirmation() {
        clearStorage();
        String name = "Silvester Stallone";

        // in movie master
        changeToPerformerView();
        // in performer master
        clickAddButton();
        // in movie se cancel movie selection
        onView(withId(R.id.negative_button)).perform(click());
        // change performer name to Silvester Stallone
        onView(withId(R.id.performer_name_input)).perform(typeText(name), closeSoftKeyboard());
        // check if Silvester Stallone is going to be removed because it has no linked movie
        /*
        // current implementation: clicking commit does nothing if no movie linked
        onView(withId(R.id.commit)).perform(click());
        onView(withText("The Performer 'Silvester Stallone' will be removed!"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("CANCEL"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
                */

        // leave the view by pressing back
        pressBack();
        // in the warning dialog select YES
        onView(withText("Do you really want to discard changes?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("YES"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        // check that Silvester Stallone is not in the performer master list
        onView(withId(R.id.model_objects_with_image))
                .check(matches(not(hasDescendant(withText(name)))));
    }

    @Test
    public void testCreatePerformersWithNoPerformersAndExistingMovies() {
        String performerName = "Harrison Ford";

        changeToPerformerView();
        clickAddButton();

        // add the movie Star Wars to the linked movie list in the movie selection dialog
        selectItemFromMoviePerformerSelectionDialog(2);
        onView(withId(R.id.positive_button)).perform(click());

        onView(withId(R.id.performer_name_input))
                .perform(typeText(performerName), closeSoftKeyboard());
        // check if Star Wars is in the linked movie list
        onView(withId(R.id.linked_movies_list)).check(matches(hasDescendant(withText("Star Wars"))));
        // confirm the changes
        onView(withId(R.id.commit)).perform(click());
        // in performer master check that Harrison Ford is in the list
        onView(withId(R.id.model_objects_with_image)).check(matches(hasDescendant(withText(performerName))));
    }

    public void changeToPerformerView() {
        onView(withId(R.id.bottom_navigation_performers)).perform(click());
    }

    public void clickAddButton() {
        onView(withId(R.id.add_button)).perform(click());
    }
}