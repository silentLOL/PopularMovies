package at.stefanirndorfer.popularmovies.model;

public class ThumbnailWrapper {

    private int id;
    private String posterPath;

    public ThumbnailWrapper(int id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
