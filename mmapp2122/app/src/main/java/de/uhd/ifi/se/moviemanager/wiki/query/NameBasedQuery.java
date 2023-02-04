package de.uhd.ifi.se.moviemanager.wiki.query;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static de.uhd.ifi.se.moviemanager.wiki.query.QueryResultFormat.JSON;

import java.io.UnsupportedEncodingException;

import de.uhd.ifi.se.moviemanager.util.Log;

/**
 * Class to manage the building of a query based around a partial or complete
 * title of a Wiki page
 */
public class NameBasedQuery {
    private static final String ACTION_QUERY = "query";

    private static final String PARAM_CATEGORIES = "categories";
    private static final String PARAM_INFO = "info";
    private static final String PARAM_SEARCH = "search";

    private static final String CATEGORY_PREFIX = "cl";
    private static final String INFO_PREFIX = "in";
    private static final String GENERATOR_PREFIX = "g";
    private static final String SEARCH_PREFIX = "sr";

    private static final String LIMIT = "limit";
    private static final String PROPERTY = "prop";
    private static final String SEARCH = "search";

    private static String encoding = UTF_8.name();

    private NameBasedQuery() {

    }

    public static void setEncoding(String encoding) {
        NameBasedQuery.encoding = encoding;
    }

    /**
     * Creates an URL-query for the WikiMedia-API, which collects 10 pages. Each
     * page title is correlated to the given partial or complete name and
     * additional properties of each page like the categories are retrieved as
     * well.<br> Example URL for retrieving actors matching 'johnny d':<br>
     * <a href="https://en.wikipedia.org/w/api.php?action=query&amp;prop=categories|info&amp;cllimit=max&amp;inprop=url&amp;generator=search&gsrlimit=10&amp;gsrsearch=johnny+d&amp;format=jsonfm">Show
     * in browser</a>
     *
     * @param wiki the name of the Wiki
     * @param name partial or complete name for the search
     * @return an URL containing the query for the WikiMedia-API
     */
    public static String createQueryURL(String wiki, String name) {
        return new QueryBuilder(wiki).setAction(ACTION_QUERY)
                .addProperty(categories()).addProperty(info())
                .setGenerator(generator(name)).setFormat(JSON).build();
    }

    private static URLParameter categories() {
        return new URLParameterBuilder(PARAM_CATEGORIES)
                .addAttribute(CATEGORY_PREFIX + LIMIT, "max").build();
    }

    private static URLParameter info() {
        return new URLParameterBuilder(PARAM_INFO)
                .addAttribute(INFO_PREFIX + PROPERTY, "url").build();
    }

    private static URLParameter generator(String name) {
        return new URLParameterBuilder(PARAM_SEARCH)
                .addAttribute(GENERATOR_PREFIX + SEARCH_PREFIX + LIMIT, "10")
                .addAttribute(GENERATOR_PREFIX + SEARCH_PREFIX + SEARCH,
                        encodeForURL(name)).build();
    }

    private static String encodeForURL(String name) {
        try {
            return encode(name, encoding);
        } catch (UnsupportedEncodingException uee) {
            Log.e("Wiki sync", uee.getMessage());
        }
        return "";
    }
}