package at.stefanirndorfer.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import at.stefanirndorfer.popularmovies.model.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorite_movie")
    List<Movie> loadAllFavoriteMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMovie(Movie movie);

    @Delete
    void deleteFavoriteMovie(Movie movie);
}
