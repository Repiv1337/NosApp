package de.uhd.ifi.se.moviemanager.ui.performer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.rule.GrantPermissionRule.grant;
import static org.hamcrest.Matchers.allOf;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.STORAGE;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.atPosition;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.openStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.selectCriterionAndDirection;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import org.junit.AfterClass;
import org.junit.Before;
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

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SortAndFilterPerformersTest {

    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(
            WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel
            .getInstance();
    private static Performer performerHarrisonFord;
    private static Performer performerSamualJackson;
    private static Performer performerKarlUrban;

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
            populateStorageWithMoviesAndPerformers();
        });
    }

    public static void populateStorageWithMoviesAndPerformers() {
        Movie starWars = STORAGE.saveMovieToFile(new Movie("Star Wars"));
        model.addMovie(starWars);

        Movie red = STORAGE.saveMovieToFile(new Movie("R.E.D."));
        model.addMovie(red);

        Movie pulpFiction = STORAGE.saveMovieToFile(new Movie("Pulp Fiction"));
        model.addMovie(pulpFiction);

        performerHarrisonFord = new Performer("Harrison Ford");
        model.addPerformer(performerHarrisonFord);

        performerSamualJackson = new Performer("Samuel Leroy Jackson");
        model.addPerformer(performerSamualJackson);

        performerKarlUrban = new Performer("Karl Urban");
        model.addPerformer(performerKarlUrban);

        starWars.link(performerHarrisonFord);
        pulpFiction.link(performerSamualJackson);
        red.link(performerKarlUrban);
        starWars.link(performerSamualJackson);
        STORAGE.savePerformerToFile(performerHarrisonFord);
        STORAGE.savePerformerToFile(performerSamualJackson);
        STORAGE.savePerformerToFile(performerKarlUrban);
    }

    @Before
    public void initBeforeTesting() {
        changeToPerformerView();
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    @Test
    public void testSortPerformersByNameDescending() {
        selectCriterionAndDirection("Name", true);
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(1, hasDescendant(
                        withText(performerHarrisonFord.getName()))),
                        atPosition(3, hasDescendant(
                                withText(performerKarlUrban.getName()))),
                        atPosition(5, hasDescendant(
                                withText(performerSamualJackson.getName()))))));
    }

    @Test
    public void testSortPerformersByNameAscending() {
        selectCriterionAndDirection("Name", false);
        onView(withId(R.id.model_objects_with_image)).check(matches(
                allOf(atPosition(5, hasDescendant(
                        withText(performerHarrisonFord.getName()))),
                        atPosition(3, hasDescendant(
                                withText(performerKarlUrban.getName()))),
                        atPosition(1, hasDescendant(
                                withText(performerSamualJackson.getName()))))));
    }

    public void changeToPerformerView() {
        onView(withId(R.id.bottom_navigation_performers)).perform(click());
    }
}
