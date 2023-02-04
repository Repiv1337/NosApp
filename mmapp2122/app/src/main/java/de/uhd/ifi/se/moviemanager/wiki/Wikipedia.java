package de.uhd.ifi.se.moviemanager.wiki;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Light weighted wrapper class for accessing Wikipedia-related online data.
 * This class was introduced to have a central point of access, in such way that
 * a child of this class could simulate a non existing connection.
 */
public class Wikipedia {
    private static final int DEFAULT_TIMEOUT = 10000;

    private final String name;
    private final int timeout;

    /**
     * Creates a new Wikipedia instance with the default timeout of 10 seconds.
     *
     * @param name base url of the Wiki, e.g. 'en.wikipedia.org'
     */
    public Wikipedia(String name) {
        this(name, DEFAULT_TIMEOUT);
    }

    /**
     * Creates a new Wikipedia instance with a custom timeout
     *
     * @param name    base url part of the Wiki like 'en.wikipedia.org'
     * @param timeout the maximum time after which a response must be retrieved
     */
    private Wikipedia(String name, int timeout) {
        this.name = name;
        this.timeout = timeout;
    }

    /**
     * @return name base url of the Wiki, e.g. 'en.wikipedia.org'.
     */
    public String getName() {
        return name;
    }

    /**
     * @return "https://" + name
     */
    public String getHomeURL() {
        return "https://" + name;
    }

    /**
     * @param relativePath relative path of a page or resource
     * @return absolute path of the page or resource. The relative path is
     * expected to be relative to the {@link Wikipedia#getHomeURL()}
     */
    public String asAbsolutePath(String relativePath) {
        return getHomeURL() + relativePath;
    }

    /**
     * @param url URL of the html page
     * @return the parsed html page as a document
     * @throws IOException if the URL is not a valid one or the connection time
     *                     outed
     */
    public Document getHTMLDocument(String url) throws IOException {
        return Jsoup.parse(new URL(url), timeout);
    }

    /**
     * @param url URL of the file
     * @return a line break separated String of the complete text base file from
     * the given url.
     * @throws IOException if the url is invalid or read failed
     */
    public String getTextFile(String url) throws IOException {
        try (InputStream input = new URL(url)
                .openStream(); InputStreamReader reader = new InputStreamReader(
                input,
                UTF_8.name()); BufferedReader bReader = new BufferedReader(
                reader)) {
            return bReader.lines().collect(joining("\n"));
        }
    }

    @Override
    public String toString() {
        return format("Wikipedia{name=\"%s\", timeout='%s ms'}", name, timeout);
    }
}
