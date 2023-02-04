/**
 * Contains the data classes and the overall {@link de.uhd.ifi.se.moviemanager.model.MovieManagerModel}.
 *
 * Contains single data classes such as {@link de.uhd.ifi.se.moviemanager.model.Movie} and {@link
 * de.uhd.ifi.se.moviemanager.model.Performer}
 *
 * The model classes need to implement the {@link android.os.Parcelable} interface so that their
 * objects can be passed from one Android activity to another activity via {@link
 * android.content.Intent}s as {@link android.os.Parcel}s.
 *
 * The entire movie manager model is managed in the {@link de.uhd.ifi.se.moviemanager.model.MovieManagerModel}
 * class.
 */
package de.uhd.ifi.se.moviemanager.model;