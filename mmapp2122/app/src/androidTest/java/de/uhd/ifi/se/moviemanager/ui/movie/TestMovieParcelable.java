package de.uhd.ifi.se.moviemanager.ui.movie;

import android.os.Parcel;

import org.junit.AfterClass;
import org.junit.Test;

import de.uhd.ifi.se.moviemanager.model.Movie;

import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static org.junit.Assert.assertEquals;

public class TestMovieParcelable {

    /**
     * @see <a href="https://jira-se.ifi.uni-heidelberg.de/browse/MMAPPR-158>Jira-Issue</a>
     */

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    @Test
    public void testMovieParcelable() {
        // Create new Movie
        Movie m1 = new Movie(0);

        // Obtain Parcel object
        Parcel parcel = Parcel.obtain();

        // write Movie m1 to parcel
        m1.writeToParcel(parcel, 0);

        // After writing, we need to reset the parcel for reading
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        Movie m2 = Movie.CREATOR.createFromParcel(parcel);

        //check equality
        assertEquals(m2, m1);
    }
}
