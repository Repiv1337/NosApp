package de.uhd.ifi.se.moviemanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Models a movie release consisting of a release date and a location (country). A {@link Movie} can
 * have one to many movie releases.
 */
public class MovieRelease implements Parcelable {

    private String location;
    private Date date;

    @JsonCreator
    public MovieRelease(@JsonProperty("location") String location, @JsonProperty("date") Date date) {
        this.location = location;
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return location + ": " + date;
    }

    /**
     * Necessary to pass objects from one Android activity to another activity via {@link
     * android.content.Intent}s as {@link Parcel}s.
     */
    @JsonIgnore
    public static final Creator<MovieRelease> CREATOR = new Creator<MovieRelease>() {
        @Override
        public MovieRelease createFromParcel(Parcel in) {
            return new MovieRelease(in);
        }

        @Override
        public MovieRelease[] newArray(int size) {
            return new MovieRelease[size];
        }
    };

    /**
     * Necessary to pass objects from one Android activity to another activity via {@link
     * android.content.Intent}s as {@link Parcel}s.
     */
    private MovieRelease(Parcel parcel) {
        location = parcel.readString();
        date = (Date) parcel.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeSerializable(date);
    }
}
