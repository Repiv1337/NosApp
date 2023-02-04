package de.uhd.ifi.se.moviemanager.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Manages all data objects and their associations. Uses the Singleton design
 * pattern. The Singleton pattern restricts the instantiation of a class to one
 * "single" instance.
 */
public class MovieManagerModel {

    // sets of data objects
    // @decision Sets are used instead of lists, so that objects cannot be
    // duplicated.
    private Set<Movie> movies;
    private Set<Performer> performers;
    // associations between classes, allows for duplicated keys
    // @decision Multimaps of guava package are used because they allow to
    // insert a key more  than once. This is important since, for example, a
    // movie can be linked to two different performers.
    private Multimap<Movie, Performer> moviePerformerAssociations;

    /**
     * Singleton instance of this model.
     */
    private static final MovieManagerModel INSTANCE = new MovieManagerModel();

    /**
     * Private constructor of the movie manager model. Use {@link
     * #getInstance()} method to create and access the singleton instance.
     */
    private MovieManagerModel() {
        // @decision Tree sets are used because they sort the objects by
        // their ids.
        // The data classes need to implement the Comparable interface.
        movies = new TreeSet<>();
        performers = new TreeSet<>();

        // @decision HashMultimaps are used because they prevent duplicated
        // key-value pairs.
        moviePerformerAssociations = HashMultimap.create();
    }

    /**
     * Provides the singleton instance of the MovieManager model. Creates a new
     * instance in case it is null.
     *
     * @return {@link MovieManagerModel} singleton instance.
     */
    public static MovieManagerModel getInstance() {
        return INSTANCE;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public boolean addMovie(Movie movie) {
        return movies.add(movie);
    }

    public boolean removeMovie(Movie movie) {
        List<Performer> linkedPerformers = movie.getPerformers();
        linkedPerformers.forEach(performer -> performer.unlink(movie));
        return movies.remove(movie);
    }

    public Set<Performer> getPerformers() {
        return performers;
    }

    public boolean addPerformer(Performer performer) {
        return performers.add(performer);
    }

    public boolean removePerformer(Performer performer) {
        List<Movie> linkedMovies = performer.getMovies();
        linkedMovies.forEach(movie -> movie.unlink(performer));
        return performers.remove(performer);
    }

    public Multimap<Movie, Performer> getMoviePerformerAssociations() {
        return moviePerformerAssociations;
    }

    public void setMoviePerformerAssociations(
            Multimap<Movie, Performer> moviePerformerAssociations) {
        this.moviePerformerAssociations = moviePerformerAssociations;
    }

    public Optional<Movie> getMovieById(int id) {
        return getObjectById(id, movies);
    }

    public Optional<Performer> getPerformerById(int id) {
        return getObjectById(id, performers);
    }

    /**
     * Returns an object in a set of {@link Movie}s, {@link Performer}s, {@link
     * Tag}s or {@link Renter}s by its id.
     *
     * @param id  of the object as an integer, e.g. 0.
     * @param set of {@link Movie}s, {@link Performer}s, {@link Tag}s or {@link
     *            Renter}s.
     * @param <T> class that implements {@link Identifiable} interface, e.g.,
     *            {@link Movie}, {@link Performer}, {@link Tag} or {@link
     *            Renter}.
     * @return object in a set by its id
     */
    private <T> Optional<T> getObjectById(int id, Set<T> set) {
        Optional<T> opt = Optional.empty();
        for (T object : set) {
            if (((Identifiable) object).getId() == id) {
                opt = Optional.of(object);
            }
        }
        return opt;
    }

    /**
     * Removes all objects and their associations in the entire movie manager.
     */
    public void clear() {
        movies.clear();
        performers.clear();

        moviePerformerAssociations.clear();
    }
}
