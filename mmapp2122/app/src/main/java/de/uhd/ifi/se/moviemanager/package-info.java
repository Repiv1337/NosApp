/**
 * The MovieManagerApp models movies and performers, offering the ability to
 * create associations between them, eg. creating a link between a movie and a
 * performer who plays in the movie.
 *
 * The {@link de.uhd.ifi.se.moviemanager.MovieManagerActivity} is responsible
 * for starting the MovieManagerApp and creating the {@link
 * de.uhd.ifi.se.moviemanager.ui.master.MovieMasterFragment} and the {@link
 * de.uhd.ifi.se.moviemanager.ui.master.PerformerMasterFragment}.
 *
 * The fragments provide multiple functionalities: - showing the list of
 * elements - searching, filtering & sorting the elements - access to the
 * attributes of the elements - creation & deletion of elements
 *
 * The attributes of the elements are shown in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity}
 * and in {@link de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailActivity}
 * and can be edited in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailEditActivity}
 * and in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailEditActivity}.
 *
 * The data objects and their associations are managed in the {@link
 * de.uhd.ifi.se.moviemanager.model.MovieManagerModel} class.
 */
package de.uhd.ifi.se.moviemanager;