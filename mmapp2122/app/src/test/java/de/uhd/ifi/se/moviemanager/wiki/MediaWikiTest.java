package de.uhd.ifi.se.moviemanager.wiki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static de.uhd.ifi.se.moviemanager.wiki.MediaWiki.getMovieDataFromWikiPage;
import static de.uhd.ifi.se.moviemanager.wiki.MediaWiki.getPerformerDataFromWikiPage;
import static de.uhd.ifi.se.moviemanager.wiki.MediaWiki.getPerformerWikiPagesByName;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.wiki.loader.PerformerLoader;
import de.uhd.ifi.se.moviemanager.wiki.query.NameBasedQuery;

class MediaWikiTest {

    private final String moviePartialTitle = "lord of the rings: The " +
            "Fellowship";
    private final String movieTitle = "The Lord of the Rings: The Fellowship "
            + "of the Ring";

    private final String performerPartialName = "johnny d";
    private final String performerName = "Johnny Depp";

    @BeforeEach
    void init() {
        MediaWiki.resetToEnglishWikipedia();
        NameBasedQuery.setEncoding(UTF_8.name());
    }

    @Test
    void testPerformerPageRetrieval() throws JSONException {
        String performerURL = "https://en.wikipedia.org/wiki/Johnny_Depp";
        testPageRetrieval(MediaWiki::getPerformerWikiPagesByName,
                performerPartialName, performerName, performerURL);
    }

    private <P> void testPageRetrieval(
            Function<String, Optional<List<JsonObject>>> retrieve,
            String partial, String name, String url) throws JSONException {
        Optional<List<JsonObject>> pagesOpt = retrieve.apply(partial);
        assertTrue(pagesOpt.isPresent());
        List<JsonObject> pages = pagesOpt.get();
        assertTrue(pages.size() >= 1);
        JsonObject topPage = pages.get(0);
        assertEquals(name, topPage.get("title").getAsString());
        assertEquals(url, topPage.get("fullurl").getAsString());
    }

    @Test
    void testMoviePageRetrieval() throws JSONException {
        String movieURL = "https://en.wikipedia" + ".org/wiki" +
                "/The_Lord_of_the_Rings:_The_Fellowship_of_the_Ring";
        testPageRetrieval(MediaWiki::getMovieWikiPagesByTitle,
                moviePartialTitle, movieTitle, movieURL);
    }

    @Test
    void testInvalidFormatForQuery() {
        NameBasedQuery.setEncoding("x");
        assertEquals(new ArrayList<>(),
                MediaWiki.getMovieWikiPagesByTitle(moviePartialTitle).get());
    }

    @Test
    void testMissingInternetConnection() {
        JsonObject performerPage = getPerformerWikiPagesByName(
                performerPartialName).get().get(0);
        JsonObject moviePage = MediaWiki
                .getMovieWikiPagesByTitle(moviePartialTitle).get().get(0);

        Wikipedia mockWiki = new NotConnectedWiki();
        MediaWiki.useOtherWiki(mockWiki);
        assertEquals(mockWiki, MediaWiki.getCurrentWikipedia());

        assertFalse(MediaWiki.getMovieWikiPagesByTitle(moviePartialTitle)
                .isPresent());
        assertFalse(
                getPerformerWikiPagesByName(performerPartialName).isPresent());
        assertFalse(getMovieDataFromWikiPage(moviePage).isPresent());
        assertFalse(getPerformerDataFromWikiPage(performerPage).isPresent());
    }

    @Test
    void testLoadPerformerWithInternetConnection() {
        JsonObject performerPage = getPerformerWikiPagesByName(
                performerPartialName).get().get(0);
        Optional<Performer> performerOpt = getPerformerDataFromWikiPage(
                performerPage);
        assertTrue(performerOpt.isPresent());
        Performer performer = performerOpt.get();

        assertEquals(performerName, performer.getName());
        assertEquals("John Christopher Depp II", performer.getBirthName());
        assertTrue(performer.getBiography().length() > 0);
        assertEquals(asList("actor", "producer", "musician"),
                performer.getOccupations());
        assertEquals("Sun Jun 09 00:00:00 CET 1963",
                performer.getDateOfBirth().toString());
        assertEquals("Performer{id=0, name=Johnny Depp}", performer.toString());
    }

    @Test
    void testLoadMovieWithInternetConnection() {
        JsonObject moviePage = MediaWiki
                .getMovieWikiPagesByTitle(moviePartialTitle).get().get(0);
        Optional<Movie> movieOpt = getMovieDataFromWikiPage(moviePage);
        assertTrue(movieOpt.isPresent());
        Movie movie = movieOpt.get();

        assertEquals(movieTitle, movie.getTitle());
        assertTrue(movie.getDescription().length() > 0);
        assertEquals(singletonList("English"), movie.getLanguages());
        assertEquals(178, movie.getRuntime());
//        assertEquals(
//                asList("New Zealand", "United States"),
//                movie.getProductionLocations());
//        assertEquals(3, movie.getReleases().size());
//        assertEquals(
//                "Odeon Leicester Square",
//                movie.getReleases().get(0).getLocation());

        assertEquals(
                "Movie{id=0, name=The Lord of the Rings: The Fellowship of " + "the Ring}",
                movie.toString());
    }

    @Test
    void testDefaultWiki() {
        String expectedHome = "https://en.wikipedia.org";
        String performerPath = "/wiki/Elijah_Wood";
        Wikipedia actualWiki = MediaWiki.getCurrentWikipedia();

        assertEquals(expectedHome, actualWiki.getHomeURL());
        assertEquals(expectedHome + performerPath,
                actualWiki.asAbsolutePath(performerPath));
    }

    @Test
    void testEmptyJSONPageForCategoryCheck() {
        JsonObject page = new JsonObject();
        assertFalse(MediaWiki.isPageOfCategory(page, String::isEmpty));
    }

    @Test
    void testNonExistingDocumentForPerformer() {
        PerformerLoader loader = PerformerLoader.getInstance();
        Optional<Performer> opt = loader
                .loadDataFromWikiHTML(new Document("nonExisting.html"));
        assertFalse(opt.isPresent());
    }
}

class NotConnectedWiki extends Wikipedia {

    NotConnectedWiki() {
        super("no.wiki.atall");
    }

    @Override
    public Document getHTMLDocument(String url) throws IOException {
        throw new IOException("");
    }

    @Override
    public String getTextFile(String url) throws IOException {
        throw new IOException("");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotConnectedWiki;
    }

    @Override
    public int hashCode() {
        return 0xAFFE;
    }
}
