package de.uhd.ifi.se.moviemanager.ui.adapter.selection;

class SelectionProxy<T> {
    private final T obj;
    private boolean enabled;

    SelectionProxy(T obj) {
        this(obj, false);
    }

    private SelectionProxy(T obj, boolean enabled) {
        this.obj = obj;
        this.enabled = enabled;
    }

    T getObj() {
        return obj;
    }

    boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void enable() {
        setEnabled(true);
    }

    void disable() {
        setEnabled(false);
    }
}
