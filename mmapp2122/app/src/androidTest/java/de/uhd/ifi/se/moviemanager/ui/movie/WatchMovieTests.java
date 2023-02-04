package de.uhd.ifi.se.moviemanager.ui.movie;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.rule.GrantPermissionRule.grant;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.STORAGE;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.childAtPosition;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.openStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.selectMenuItemAndEnterEdit;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class WatchMovieTests {
    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel.getInstance();
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityActivityScenarioRule = new ActivityScenarioRule<>(MovieManagerActivity.class);

    @BeforeClass
    public static void initStorage() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch(MovieManagerActivity.class);
        activityScenario.onActivity(activity -> {
            openStorage(activity);
            clearStorage();
            populateStorageWithMovie();
        });
    }

    private static void populateStorageWithMovie() {
        Movie blade = STORAGE.saveMovieToFile(new Movie("Star Wars"));
        model.addMovie(blade);
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    @Test
    public void testWatchMovieNoCancellationWithConfirmation() {
        selectMenuItemAndEnterEdit("Star Wars");

        enterWatchDatePicker();

        onView(withId(R.id.calendar))
                .perform(PickerActions.setDate(2016, 9, 13));
        onView(withText(R.string.confirm)).perform(click());
        onView(withId(R.id.commit)).perform(click());
        onView(withId(R.id.watch_date))
                .check(matches(hasDescendant(withText("13.09.2016"))));
    }

    @Test
    public void testWatchMovieNoCancellationWithoutConfirmation() {
        selectMenuItemAndEnterEdit("Star Wars");

        enterWatchDatePicker();

        onView(withId(R.id.calendar))
                .perform(PickerActions.setDate(2017, 4, 22));
        onView(withText(R.string.confirm)).perform(click());
        pressBack();
        onView(withText(R.string.yes)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.watch_date))
                .check(matches(not(hasDescendant(withText("22.04.2017")))));
    }

    @Test
    public void testWatchMovieWithCancellation() {
        selectMenuItemAndEnterEdit("Star Wars");

        enterWatchDatePicker();

        onView(withId(R.id.calendar))
                .perform(PickerActions.setDate(2019, 2, 19));
        onView(withText("CANCEL")).perform(click());

        pressBack();
        onView(withId(R.id.watch_date))
                .check(matches(not(hasDescendant(withText("19.02.2019")))));
    }

    private void enterWatchDatePicker() {
        onView(allOf(withId(R.id.select_date), withContentDescription("Select a date."),
                childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_watch_date),
                                0),
                        2),
                isDisplayed())).perform(click());
    }
}
