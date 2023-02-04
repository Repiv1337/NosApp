package de.uhd.ifi.se.moviemanager.ui.movie;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.rule.GrantPermissionRule.grant;
import static org.hamcrest.Matchers.not;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.openStorage;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
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
import de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class CreateMovieTests {

    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(
            WRITE_EXTERNAL_STORAGE);
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityScenarioRule =
            new ActivityScenarioRule<>(
            MovieManagerActivity.class);

    @BeforeClass
    public static void initStorage() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch(
                MovieManagerActivity.class);
        activityScenario.onActivity(activity -> {
            grant(WRITE_EXTERNAL_STORAGE);
            openStorage(activity);
            clearStorage();
        });
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        UiTestUtils.clearStorage();
    }

    @Test
    public void testCreateMovieWithNoMovies() {
        String movieTitle = "Star Wars";
        clickAddButton();
        // now in detail edit give the movie a title
        setMovieTitle(movieTitle);

        // check whether Star Wars is in the list
        onView(withText(movieTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateMovieWithExistingMovies() {
        String movieTitle = "Movie: The Movie";
        clickAddButton();
        setMovieTitle(movieTitle);

        // check again if new movie is in the list
        onView(withText(movieTitle)).check(matches(isDisplayed()));
    }

    private void setMovieTitle(String title) {
        onView(withId(R.id.movie_title_input))
                .perform(typeText(title), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.commit)).perform(click());
    }

    @Test
    public void testCreateMovieNoConfirmation() {
        String movieTitle = "Not Real";
        clickAddButton();
        onView(withId(R.id.movie_title_input))
                .perform(typeText(movieTitle), ViewActions.closeSoftKeyboard());
        pressBack();

        onView(withText("Do you really want to discard changes?"))
                .inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed()))
                .perform(click());

        // check if the item is not in the list
        onView(withId(R.id.model_objects_with_image))
                .check(matches(not(hasDescendant(withText(movieTitle)))));

    }

    private void clickAddButton() {
        onView(withId(R.id.add_button)).perform(click());
    }
}
