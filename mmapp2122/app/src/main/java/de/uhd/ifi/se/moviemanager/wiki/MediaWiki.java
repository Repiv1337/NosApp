package de.uhd.ifi.se.moviemanager.wiki;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.StreamSupport.stream;
import static de.uhd.ifi.se.moviemanager.wiki.query.NameBasedQuery.createQueryURL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.wiki.loader.MovieLoader;
import de.uhd.ifi.se.moviemanager.wiki.loader.PerformerLoader;

/**
 * Central class for data retrieval from Wikipedia. The default wiki that is
 * used is the
 * <a href="https://en.wikipedia.org">English Wikipedia</a> wrapped with the
 * {@link Wikipedia} class - a light weighted access point.
 *
 * Because of the partial name match, which will be performed during the query
 * often more than one (possibly even without the correct) match is collected.
 * The user can select the correct match for data retrieval.
 */
public final class MediaWiki {
    // private constructor to prevent instantiation
    private MediaWiki() {
    }

    private static Wikipedia wikipedia;

    static {
        resetToEnglishWikipedia();
    }

    /**
     * Resets the underlying Wiki to the default english. This call is analog
     * to: {@code useOtherWiki(new Wikipedia("en.wikipedia.org")); }
     */
    public static void resetToEnglishWikipedia() {
        useOtherWiki(new Wikipedia("en.wikipedia.org"));
    }

    /**
     * @return the current underlying wiki instance
     */
    public static Wikipedia getCurrentWikipedia() {
        return wikipedia;
    }

    /**
     * Use the given Wiki as the new underlying wiki instance.
     *
     * @param wiki must be non <i>null</i>
     * @throws NullPointerException if the given wiki is <i>null</i>
     */
    public static void useOtherWiki(Wikipedia wiki) {
        wikipedia = requireNonNull(wiki);
    }

    /**
     * @param name partial or complete name of the desired performer
     * @return list of possible performer Wiki pages matching the given name,
     * sorted by similarity where the first element has the greatest similarity
     * to the given name. If an error occurred during data retrieval the
     * Optional is empty, if only non-performer pages match the given name, the
     * list is empty. To retrieve the performer data from a page see {@link
     * MediaWiki#getPerformerDataFromWikiPage}
     */
    public static Optional<List<JsonObject>> getPerformerWikiPagesByName(
            String name) {
        Optional<String> opt = retrieveJsonByName(name);
        return opt.map(json -> collectPages(name, json,
                MediaWiki::isPerformerPage));
    }

    /**
     * @param title partial or complete title of the desired movie
     * @return list of possible Movie Wiki pages matching the given title,
     * sorted by similarity where the first element has the greatest similarity
     * to the given name. If an error occurred during data retrieval the
     * Optional is empty, if only non-movie pages match the given title, the
     * list is empty. To retrieve the movie data from a page see {@link
     * MediaWiki#getMovieDataFromWikiPage}
     */
    public static Optional<List<JsonObject>> getMovieWikiPagesByTitle(
            String title) {
        Optional<String> opt = retrieveJsonByName(title);
        return opt
                .map(json -> collectPages(title, json, MediaWiki::isMoviePage));
    }

    /**
     * @param name partial or complete name for the search
     * @return a line break separated String of the complete file if the url is
     * valid
     */
    private static Optional<String> retrieveJsonByName(String name) {
        String url = createQueryURL(wikipedia.getName(), name);
        return retrieveJsonFromURL(url);
    }

    /**
     * @param url url of a wikipedia page
     * @return a line break separated String of the complete file if the url is
     * valid
     */
    private static Optional<String> retrieveJsonFromURL(String url) {
        try {
            return Optional.of(wikipedia.getTextFile(url));
        } catch (IOException ioe) {
            return Optional.empty();
        }
    }

    /**
     * @param page wiki page as a {@link JsonObject}.
     * @return true if the page is about a performer.
     */
    private static boolean isPerformerPage(JsonObject page) {
        Predicate<String> performerCriterion = s -> s.contains("actors") || s
                .contains("actress");
        return isPageOfCategory(page, performerCriterion);
    }

    /**
     * Checks if the given page meta data matches a title criterion.
     *
     * @param page      meta data of the page.
     * @param criterion criterion for the page title.
     * @return true if matching criterion, false otherwise.
     */
    static boolean isPageOfCategory(JsonObject page,
                                    Predicate<String> criterion) {
        JsonArray categories = page.getAsJsonArray("categories");
        if (categories == null) {
            return false;
        }
        return range(0, categories.size()).mapToObj(categories::get)
                .map(JsonElement::getAsJsonObject).filter(Objects::nonNull)
                .map(o -> o.get("title").getAsString()).anyMatch(criterion);

    }

    /**
     * @param page wiki page as a {@link JsonObject}.
     * @return true if the page is about a movie.
     */
    private static boolean isMoviePage(JsonObject page) {
        Predicate<String> movieCriterion = s -> s
                .matches("Category:\\d{4} films");
        return isPageOfCategory(page, movieCriterion);
    }

    /**
     * @param name       partial or complete title of the desired movie
     * @param jsonString data from which pages are extracted
     * @param criterion  criterion by which pages are filtered
     * @return list of possible Wiki pages as Json Objects matching the given
     * title, sorted by similarity where the first element has the greatest
     * similarity to the given name.
     */
    private static List<JsonObject> collectPages(String name, String jsonString,
                                                 Predicate<JsonObject> criterion) {
        try {
            JsonObject result = JsonParser.parseString(jsonString)
                    .getAsJsonObject();
            JsonObject query = result.getAsJsonObject("query");
            JsonObject pages = query.getAsJsonObject("pages");

            ToDoubleFunction<JsonObject> keyExtractor = p -> comparePageToName(
                    p, name);

            return stream(pages.keySet().spliterator(), false)
                    .map(pages::getAsJsonObject).filter(criterion)
                    .sorted(comparing(keyExtractor::applyAsDouble))
                    .collect(toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * @param page wiki page
     * @param name String to which similarity is measured (partial title)
     * @return double indicating how similar the page title is to name.
     */
    private static double comparePageToName(JsonObject page, String name) {
        String title = page.get("title").getAsString();
        return new LevenshteinDistance()
                .apply(title.toLowerCase(), name.toLowerCase());
    }

    /**
     * Loads the html document from the given meta data of the page and extracts
     * the movie data from this document.
     *
     * @param page metadata of the movie page.
     * @return returns Optional of the movie data, empty if something went
     * wrong.
     */
    public static Optional<Movie> getMovieDataFromWikiPage(JsonObject page) {
        return getDataFromWikiPage(page,
                MovieLoader.getInstance()::loadDataFromWikiHTML);
    }

    /**
     * Loads the html document from the given meta data of the page and extracts
     * the performer data from this document.
     *
     * @param page metadata of the performer page.
     * @return returns Optional of the performer data, empty if something went
     * wrong.
     */
    public static Optional<Performer> getPerformerDataFromWikiPage(
            JsonObject page) {
        return getDataFromWikiPage(page,
                PerformerLoader.getInstance()::loadDataFromWikiHTML);
    }

    private static <P> Optional<P> getDataFromWikiPage(JsonObject page,
                                                       Function<Document,
                                                               Optional<P>> load) {
        try {
            String url = page.get("fullurl").getAsString();
            Document doc = wikipedia.getHTMLDocument(url);
            return load.apply(doc);
        } catch (IOException ioe) {
            return Optional.empty();
        }
    }
}


