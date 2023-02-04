package de.uhd.ifi.se.moviemanager.wiki.query;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to build an URL containing the query for the WikiMedia-API
 */
class QueryBuilder {
    private final String wiki;
    private final List<URLParameter> properties;
    private String action;
    private URLParameter generator;
    private QueryResultFormat format;

    QueryBuilder(String wiki) {
        this.wiki = wiki;
        this.properties = new ArrayList<>();
    }

    QueryBuilder setAction(String action) {
        this.action = action;
        return this;
    }

    QueryBuilder addProperty(URLParameter prop) {
        this.properties.add(prop);
        return this;
    }

    QueryBuilder setGenerator(URLParameter generator) {
        this.generator = generator;
        return this;
    }

    QueryBuilder setFormat(QueryResultFormat format) {
        this.format = format;
        return this;
    }

    String build() {
        String propString = properties.stream().map(URLParameter::getName)
                .collect(joining("|"));

        String attrs = properties.stream().map(URLParameter::getAttributeString)
                .collect(joining("&"));

        return "https://" + wiki + "/w/api.php?" + "action=" + action + "&" + "prop=" + propString + "&" + attrs + "&" + "generator=" + generator
                .getName() + "&" + generator
                .getAttributeString() + "&" + "format=" + format;
    }
}
