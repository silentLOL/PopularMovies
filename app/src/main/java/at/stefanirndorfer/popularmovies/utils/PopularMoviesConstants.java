package at.stefanirndorfer.popularmovies.utils;

import android.util.SparseArray;

import java.util.List;

public class PopularMoviesConstants {
    public static final SparseArray<String> genres = new SparseArray<>();
    static {
    genres.put(28, "Action");
    genres.put(12, "Adventure");
    genres.put(16, "Animation");
    genres.put(35, "Comedy");
    genres.put(80, "Crime");
    genres.put(99, "Documentary");
    genres.put(18, "Drama");
    genres.put(10751, "Family");
    genres.put(14, "Fantasy");
    genres.put(36, "History");
    genres.put(27, "Horror");
    genres.put(10402, "Music");
    genres.put(9648, "Mystery");
    genres.put(10749, "Romance");
    genres.put(878, "Science Fiction");
    genres.put(10770, "TV Movie");
    genres.put(53, "Thriller");
    genres.put(10752, "War");
    genres.put(37, "Western");
    }

    public static final String BASIC_POSTER_PATH = "http://image.tmdb.org/t/p/";
    public static final String DEFAULT_IMAGE_WIDTH = "w185";

    public static String getGenreById(List<Integer> genreList){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< genreList.size(); i++){
            if (i != 0){
                sb.append(",\n");
            }
            sb.append(genres.get(genreList.get(i)));
        }
        return sb.toString();
    }


}
