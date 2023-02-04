/**
 * Manages storage of data objects (e.g. of {@link de.uhd.ifi.se.moviemanager.model.Movie}s and
 * {@link de.uhd.ifi.se.moviemanager.model.Performer}s), of their associations, and images.
 *
 * The Proxy design pattern is used: The class {@link de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess}
 * is a proxy that controls access to the {@link de.uhd.ifi.se.moviemanager.storage.StorageManagerImpl}
 * class. Both classes implement the {@link de.uhd.ifi.se.moviemanager.storage.StorageManager}
 * interface.
 */
package de.uhd.ifi.se.moviemanager.storage;
