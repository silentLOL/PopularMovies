package at.stefanirndorfer.popularmovies.model;

public enum MoviesOrder {
    POPULAR("popularity.desc"),
    TOP_RATED("vote_average.desc");

    private final String name;

    MoviesOrder(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
