package at.stefanirndorfer.popularmovies.model;

public enum MoviesOrder {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    FAVORITES("favorites");

    private final String name;

    MoviesOrder(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MoviesOrder getMovieOrderByString(String string){
        for (MoviesOrder currElem : MoviesOrder.values()) {
            if (currElem.toString().equalsIgnoreCase(string)) {
                return currElem;
            }
        }
        return null;
    }

}
