package de.uhd.ifi.se.moviemanager.ui.performer;

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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
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
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.openStorage;
import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.selectMenuItemAndEnterEdit;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class ChangePerformerDataTest {
    @ClassRule
    public static final GrantPermissionRule PERMISSION_RULE = grant(WRITE_EXTERNAL_STORAGE);
    private static final MovieManagerModel model = MovieManagerModel.getInstance();
    @Rule
    public ActivityScenarioRule<MovieManagerActivity> activityScenarioRule = new ActivityScenarioRule<>(MovieManagerActivity.class);

    @BeforeClass
    public static void initStorage() {
        ActivityScenario<MovieManagerActivity> activityScenario = launch( MovieManagerActivity.class );
        activityScenario.onActivity( activity -> {
            grant( WRITE_EXTERNAL_STORAGE );
            openStorage( activity );
            clearStorage();
            populateStorage();
        } );
    }

    @Before
    public void InitBeforeTesting() {
        clearStorage();
        populateStorage();
        changeToPerformerView();
    }

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    private static void populateStorage() {
        Movie starWars = STORAGE.saveMovieToFile(new Movie("Star Wars"));
        model.addMovie(starWars);

        Movie pulpFiction = STORAGE.saveMovieToFile(new Movie("Pulp Fiction"));
        model.addMovie(pulpFiction);

        Performer performer1 = new Performer("Harrison Ford");
        model.addPerformer(performer1);

        Performer performer2 = new Performer("Samuel Leroy Jackson");
        model.addPerformer(performer2);

        starWars.link(performer1);
        pulpFiction.link(performer2);

        STORAGE.savePerformerToFile(performer1);
        STORAGE.savePerformerToFile(performer2);
    }

    @Test
    public void testChangePerformerDetailDataSaveChanges() {
        selectMenuItemAndEnterEdit( "Harrison Ford" );

        onView(withId(R.id.performer_name_input))
                .perform(clearText(),
                        typeText("HF Save Changes"),
                        closeSoftKeyboard()
                );

        onView(withId(R.id.commit)).perform(click());

        // test
        onView(withText("HF Save Changes")).check(matches(isDisplayed()));
    }

    @Test
    public void testChangePerformerDetailDiscardChanges() {
        selectMenuItemAndEnterEdit( "Harrison Ford" );

        onView(withId(R.id.performer_name_input))
                .perform(clearText(),
                        typeText("HF Save Changes"),
                        closeSoftKeyboard()
                );

        pressBack();

        onView(withText("Do you really want to discard changes?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("YES"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // test
        onView(withText("Harrison Ford")).check(matches(isDisplayed()));
    }




    public static void changeToPerformerView() {
        onView(withId( R.id.bottom_navigation_performers)).perform(click());
    }
}
