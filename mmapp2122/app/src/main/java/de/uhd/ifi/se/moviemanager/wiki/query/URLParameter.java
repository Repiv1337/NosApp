package de.uhd.ifi.se.moviemanager.wiki.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to manage URL parameters (name, various attributes).
 */
class URLParameter {
    private final String name;
    private final Map<String, String> attributes;

    URLParameter(String name) {
        this.name = name;
        this.attributes = new HashMap<>();
    }

    void putAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    String getName() {
        return this.name;
    }

    String getAttributeString() {
        return attributes.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }
}
