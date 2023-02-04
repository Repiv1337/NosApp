package de.uhd.ifi.se.moviemanager.util;

@FunctionalInterface
public interface BiBooleanConsumer<E> {
    void accept(E elem, boolean b);
}
