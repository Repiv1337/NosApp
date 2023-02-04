package de.uhd.ifi.se.moviemanager.model;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static de.uhd.ifi.se.moviemanager.util.DateUtils.normDate;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Data class which models a movie with zero to many {@link Performer}s.
 */
@JsonIgnoreProperties({"name"})
// because the name of the movie is its title
public class Movie extends ModelObjectWithImage implements Rateable {

    private Date watchDate;

    public Date getDuedate() {
        return duedate;
    }


    private Date duedate;

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }
    private boolean isavaliable;
    private String description;
    private List<String> languages;
    private List<MovieRelease> releases;
    private int runtime;
    private List<String> productionLocations;
    private double rating;
    private double overallRating;

    @JsonCreator
    public Movie(@JsonProperty("id") int id) {
        super(id);
        watchDate = null;
        description = "";
        languages = new ArrayList<>();
        releases = new ArrayList<>();
        runtime = 0;
        productionLocations = new ArrayList<>();
        rating = -1.;
        overallRating = -1.;
        duedate = null;
        isavaliable = true;
        if (getId() <= -1) {
            int freeId = Identifiable.findFreeId(model.getMovies());
            setId(freeId);
        }
    }

    public Movie() {
        this(-1);
    }

    public Movie(String title) {
        this();
        setTitle(title);
    }

    public String getTitle() {
        return getName();
    }

    public void setTitle(String title) {
        setName(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<MovieRelease> getReleases() {
        return releases;
    }

    public void setReleases(List<MovieRelease> releases) {
        this.releases = releases;
    }

    public boolean addRelease(MovieRelease release) {
        return releases.add(release);
    }

    public Date getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(Date watchDate) {
        this.watchDate = normDate(watchDate);
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getProductionLocations() {
        return productionLocations;
    }

    public void setProductionLocations(List<String> productionLocations) {
        this.productionLocations = productionLocations;
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
    public List<Performer> getPerformers() {
        return model.getMoviePerformerAssociations().get(this).stream()
                .sorted(comparing(Performer::getName,
                        String::compareToIgnoreCase)).collect(toList());
    }

    public boolean link(Performer performer) {
        if (performer != null) {
            return model.getMoviePerformerAssociations().put(this, performer);
        } else {
            return false;
        }
    }

    public void link(List<Performer> performers) {
        performers.forEach(this::link);
    }

    public boolean unlink(Performer performer) {
        if (performer != null) {
            return model.getMoviePerformerAssociations()
                    .remove(this, performer);
        } else {
            return false;
        }
    }

    public double calculateOverallRating() {
        overallRating = getRating();
        List<Performer> performers = getPerformers();
        if (!performers.isEmpty() && isRated()) {
            DoubleSummaryStatistics stats = performers.stream()
                    .filter(Rateable::isRated).map(Rateable::getRating)
                    .collect(Collectors.summarizingDouble(Double::doubleValue));
            if (stats.getCount() > 0) {
                double performersRating = stats.getAverage();
                overallRating = (getRating() + performersRating) / 2.0;
            }
        }
        return overallRating;
    }

    public double getOverallRating() {
        return overallRating > -1 ? overallRating : calculateOverallRating();
    }

    @JsonIgnore
    public String getOverallRatingInStars() {
        return Rateable.ratingToText(getOverallRating(), true);
    }

    public boolean hasPerformer(Performer performer) {
        return model.getMoviePerformerAssociations()
                .containsEntry(this, performer);
    }

    @Override
    public boolean equals(@Nullable Object otherMovie) {
        return super.equals(otherMovie);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    public void recieveMovie(){
        this.isavaliable = true;
        duedate = null;
    }
    public void lendMovie(Date deadline){
        Date today = new Date();

        this.isavaliable = false;
        duedate = deadline;
    }


    /**
     * Necessary to pass objects from one Android activity to another activity
     * via {@link android.content.Intent}s as {@link Parcel}s.
     */
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public boolean isIsavaliable() {
        return isavaliable;
    }

    public void setIsavaliable(boolean isavaliable) {
        this.isavaliable = isavaliable;
    }

    /**
     * Necessary to pass objects from one Android activity to another activity
     * via {@link android.content.Intent}s as {@link Parcel}s.
     */
    private Movie(Parcel in) {
        super(in);
        languages = new ArrayList<>();
        productionLocations = new ArrayList<>();
        releases = new ArrayList<>();
        description = in.readString();
        in.readStringList(languages);
        in.readTypedList(releases, MovieRelease.CREATOR);
        watchDate = (Date) in.readSerializable();
        runtime = in.readInt();
        in.readStringList(productionLocations);
        rating = in.readDouble();
        overallRating = in.readDouble();
        duedate = (Date)in.readSerializable();
        isavaliable = (Boolean) in.readBoolean();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(description);
        dest.writeStringList(languages);
        dest.writeTypedList(releases);
        dest.writeSerializable(watchDate);
        dest.writeInt(runtime);
        dest.writeStringList(productionLocations);
        dest.writeDouble(rating);
        dest.writeDouble(overallRating);
        dest.writeSerializable(duedate);
        dest.writeBoolean(isavaliable);
    }
}
