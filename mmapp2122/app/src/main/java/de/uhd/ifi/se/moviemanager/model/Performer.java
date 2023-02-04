package de.uhd.ifi.se.moviemanager.model;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static de.uhd.ifi.se.moviemanager.util.DateUtils.normDate;
import static de.uhd.ifi.se.moviemanager.util.DateUtils.now;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Data class which models a performer in one or more {@link Movie}s.
 */
public class Performer extends ModelObjectWithImage implements Rateable {
    private String birthName;
    private String biography;
    private Date dateOfBirth;
    private List<String> occupations;
    private double rating;

    @JsonCreator
    public Performer(@JsonProperty("id") int id) {
        super(id);
        birthName = "";
        biography = "";
        dateOfBirth = null;
        occupations = new ArrayList<>();
        rating = -1.;
        if (getId() <= -1) {
            int freeId = Identifiable.findFreeId(model.getPerformers());
            setId(freeId);
        }
    }

    public Performer() {
        this(-1);
    }

    public Performer(String name) {
        this();
        setName(name);
    }

    @JsonIgnore
    public String getFirstName() {
        String[] parts = getName().split(" ");
        if (parts.length > 1) {
            return parts[0];
        }
        return getName();
    }

    @JsonIgnore
    public List<String> getMiddleNames() {
        String[] parts = getName().split(" ");
        if (parts.length > 2) {
            return Arrays.asList(parts).subList(1, parts.length - 1);
        }
        return Collections.emptyList();
    }

    @JsonIgnore
    public String getLastName() {
        String[] parts = getName().split(" ");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        }
        return getName();
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = normDate(dateOfBirth);
    }

    @JsonIgnore
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return DateUtils.differenceInYears(now(), dateOfBirth);
    }

    public List<String> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<String> occupations) {
        this.occupations = occupations;
    }

    @Override
    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public double getRating() {
        return rating;
    }

    @JsonIgnore
    public List<Movie> getMovies() {
        List<Movie> movies = new ArrayList<>();
        for (Map.Entry<Movie, Performer> entry : model
                .getMoviePerformerAssociations().entries()) {
            if (equals(entry.getValue())) {
                movies.add(entry.getKey());
            }
        }
        return movies.stream()
                .sorted(comparing(Movie::getName, String::compareToIgnoreCase))
                .collect(toList());
    }

    /**
     * @return true if the performer has at least one linked movie.
     */
    public boolean hasOneOrMoreMoviesLinked() {
        List<Movie> movies = getMovies();
        return !movies.isEmpty();
    }

    public boolean isPerformerIn(Movie movie) {
        return movie.hasPerformer(this);
    }

    public boolean link(Movie movie) {
        if (movie != null) {
            return movie.link(this);
        } else {
            return false;
        }
    }

    public boolean unlink(Movie movie) {
        if (movie != null) {
            return movie.unlink(this);
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(@Nullable Object otherPerformer) {
        return super.equals(otherPerformer);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Necessary to pass objects from one Android activity to another activity
     * via {@link android.content.Intent}s as {@link Parcel}s.
     */
    public static final Creator<Performer> CREATOR = new Creator<Performer>() {
        @Override
        public Performer createFromParcel(Parcel source) {
            return new Performer(source);
        }

        @Override
        public Performer[] newArray(int size) {
            return new Performer[size];
        }
    };

    /**
     * Necessary to pass objects from one Android activity to another activity
     * via {@link android.content.Intent}s as {@link Parcel}s.
     */
    private Performer(Parcel in) {
        super(in);
        occupations = new ArrayList<>();
        birthName = in.readString();
        biography = in.readString();
        dateOfBirth = (Date) in.readSerializable();
        in.readStringList(occupations);
        rating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(birthName);
        dest.writeString(biography);
        dest.writeSerializable(dateOfBirth);
        dest.writeStringList(occupations);
        dest.writeDouble(rating);
    }
}
