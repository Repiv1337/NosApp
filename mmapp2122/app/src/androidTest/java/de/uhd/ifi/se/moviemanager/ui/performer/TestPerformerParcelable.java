package de.uhd.ifi.se.moviemanager.ui.performer;

import android.os.Parcel;

import org.junit.AfterClass;
import org.junit.Test;

import de.uhd.ifi.se.moviemanager.model.Performer;

import static de.uhd.ifi.se.moviemanager.ui.util.UiTestUtils.clearStorage;
import static org.junit.Assert.assertEquals;

public class TestPerformerParcelable {

    /**
     * @see <a href="https://jira-se.ifi.uni-heidelberg.de/browse/MMAPPR-159>Jira-Issue</a>
     */

    @AfterClass
    public static void clearStorageAfterClass() {
        clearStorage();
    }

    @Test
    public void testPerformerParcelable() {
        // Create Performer
        Performer p1 = new Performer(0);

        // Obtain Parcel object
        Parcel parcel = Parcel.obtain();

        // write Performer p1 to parcel
        p1.writeToParcel(parcel, 0);

        // After writing, we need to reset the parcel for reading
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        Performer p2 = Performer.CREATOR.createFromParcel(parcel);

        //check equality
        assertEquals(p2, p1);
    }
}
