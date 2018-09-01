package at.stefanirndorfer.popularmovies.model;

public enum MoviesOrder {
    POPULAR("popular"),
    TOP_RATED("top_rated");

    private final String name;

    MoviesOrder(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
