package de.uhd.ifi.se.moviemanager.ui.movie;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.rule.GrantPermissionRule.grant;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.STORAGE;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.atPosition;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clickCriterionButton;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.openStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.selectCriterionAndDirection;

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

import java.util.Date;

import de.uhd.ifi.se.moviemanager.MovieManagerActivity;
import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Tests the sorting and filtering of movies. Note that {@link
 * de.uhd.ifi.se.moviemanager.ui.master.HeaderViewHolder}s also are list entries
 * and need to be regarded when checking the positions.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SortAndFilterMoviesTests {

    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(
            WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel
            .getInstance();
    private static Movie starWars;
    private static Movie red;
    private static Movie pulpFiction;
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityScenarioRule =
            new ActivityScenarioRule<>(
            MovieManagerActivity.class);

    @BeforeClass
    public static void initStorage() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch(
                MovieManagerActivity.class);
        activityScenario.onActivity(activity -> {
            openStorage(activity);
            clearStorage();
            populateStorageWithMovies();
        });
    }

    private static void populateStorageWithMovies() {
        starWars = new Movie("Star Wars");
        starWars.setRating(4.5);
        model.addMovie(starWars);
        STORAGE.saveMovieToFile(starWars);

        red = new Movie("R.E.D.");
        red.setRating(4.0);
        Date today = DateUtils.nowAtMidnight();
        Date inAMonth = new Date(today.getTime() + 31 * 24 * 60 * 60 * 1000L);
        model.addMovie(red);
        STORAGE.saveMovieToFile(red);

        pulpFiction = new Movie("Pulp Fiction");
        pulpFiction.setRating(5.0);
        Date inTheFuture = new Date(inAMonth.getTime() + 24 * 60 * 60 * 10000L);
        model.addMovie(pulpFiction);
        STORAGE.saveMovieToFile(pulpFiction);
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    @Test
    public void testSortMoviesByTitleDescending() {
        clickCriterionButton();
        onView(withText("Title")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(1,
                        hasDescendant(withText(starWars.getTitle()))),
                        atPosition(3, hasDescendant(withText(red.getTitle()))),
                        atPosition(5, hasDescendant(
                                withText(pulpFiction.getTitle()))))));
    }

    @Test
    public void testSortMoviesByRatingAscending() {
        selectCriterionAndDirection("Rating", false);
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(1,
                        hasDescendant(withText(pulpFiction.getTitle()))),
                        atPosition(3,
                                hasDescendant(withText(starWars.getTitle()))),
                        atPosition(5,
                                hasDescendant(withText(red.getTitle()))))));
    }

    @Test
    public void testSortMoviesByRatingDescending() {
        selectCriterionAndDirection("Rating", true);
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(5,
                        hasDescendant(withText(pulpFiction.getTitle()))),
                        atPosition(3,
                                hasDescendant(withText(starWars.getTitle()))),
                        atPosition(1,
                                hasDescendant(withText(red.getTitle()))))));
    }

    @Test
    public void testSortMoviesByOverallRatingAscending() {
        selectCriterionAndDirection("Overall Rating", false);
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(1,
                        hasDescendant(withText(pulpFiction.getTitle()))),
                        atPosition(3,
                                hasDescendant(withText(starWars.getTitle()))),
                        atPosition(5,
                                hasDescendant(withText(red.getTitle()))))));
    }

    @Test
    public void testSortMoviesByOverallRatingDescending() {
        selectCriterionAndDirection("Overall Rating", true);
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(5,
                        hasDescendant(withText(pulpFiction.getTitle()))),
                        atPosition(3,
                                hasDescendant(withText(starWars.getTitle()))),
                        atPosition(1,
                                hasDescendant(withText(red.getTitle()))))));
    }

    @Test
    public void testFilterMoviesByTitle() {
        selectCriterionAndDirection("Title", false);
        onView(withId(R.id.filter)).perform(typeText(starWars.getTitle()));
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(not(hasDescendant(withText(pulpFiction.getTitle()))),
                        not(hasDescendant(withText(red.getTitle()))))));
        onView(withId(R.id.model_objects_with_image))
                .check(matches(hasDescendant(withText(starWars.getTitle()))));
    }
}
