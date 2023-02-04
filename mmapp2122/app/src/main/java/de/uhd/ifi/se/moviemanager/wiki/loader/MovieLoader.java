package de.uhd.ifi.se.moviemanager.wiki.loader;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.uhd.ifi.se.moviemanager.model.Movie;

/**
 * Singleton of the movie loader which is responsible for the data extraction
 * for movies.
 */
public final class MovieLoader extends AbstractLoader<Movie> {
    private static final String COUNTRY_ATTRIBUTE = "country";
    private static final String COUNTRIES_ATTRIBUTE = "countries";
    private static final String RUNNING_TIME_ATTRIBUTE = "running time";
    private static final String STARRING_ATTRIBUTE = "starring";
    private static final String LANGUAGE_ATTRIBUTE = "language";
    private static final String RELEASE_DATE_ATTRIBUTE = "release date";

    public static final List<String> TABULAR_ATTRIBUTES = unmodifiableList(
            asList(COUNTRY_ATTRIBUTE, COUNTRIES_ATTRIBUTE,
                    RUNNING_TIME_ATTRIBUTE, STARRING_ATTRIBUTE,
                    LANGUAGE_ATTRIBUTE, RELEASE_DATE_ATTRIBUTE));

    private static final MovieLoader INSTANCE = new MovieLoader();
    private static final String NAME_SELECTOR = "th[class=infobox-above " +
            "summary]";
    private static final String TABLE_SELECTOR = "table[class=infobox vevent]";

    private MovieLoader() {
        super(TABULAR_ATTRIBUTES);
    }

    /**
     * @return the only MovieLoader instance available (Singleton object).
     */
    public static MovieLoader getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Movie> loadDataFromWikiHTML(Document doc) {
        if (doc == null || getTable(doc) == null) {
            return Optional.empty();
        }
        Movie movieFromWiki = new Movie(getTitle(doc));
        movieFromWiki.setImage(getImageUrl(doc));
        movieFromWiki.setDescription(getIntroduction(doc));
        Map<String, List<String>> attributes = extractAttributesFromTable(
                getTable(doc));
        movieFromWiki.setLanguages(attributes.get(LANGUAGE_ATTRIBUTE));
        movieFromWiki.setProductionLocations(parseCountries(attributes));
        movieFromWiki.setRuntime(
                parseRuntime(attributes.get(RUNNING_TIME_ATTRIBUTE)));

        return Optional.of(movieFromWiki);
    }

    protected List<String> parseCountries(
            Map<String, List<String>> attributes) {
        List<String> countries = attributes.get(COUNTRY_ATTRIBUTE);
        if (countries == null || countries.isEmpty()) {
            countries = attributes.get(COUNTRIES_ATTRIBUTE);
        }
        return countries;
    }

    protected int parseRuntime(List<String> runtimeList) {
        String runtimeText = runtimeList.get(0);
        String[] runtimeArray = runtimeText.split(" ");
        if (runtimeArray.length > 0) {
            runtimeText = runtimeArray[0];
        }
        return parseInt(runtimeText);
    }

    @Override
    protected String getNameSelector() {
        return NAME_SELECTOR;
    }

    @Override
    protected String getTableSelector() {
        return TABLE_SELECTOR;
    }
}
