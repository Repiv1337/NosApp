package de.uhd.ifi.se.moviemanager.wiki.query;

enum QueryResultFormat {
    XML, JSON;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
