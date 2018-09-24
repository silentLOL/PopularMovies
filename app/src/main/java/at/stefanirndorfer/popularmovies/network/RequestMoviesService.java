package at.stefanirndorfer.popularmovies.network;

import at.stefanirndorfer.popularmovies.model.MovieQueryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestMoviesService {

    @GET("/3/movie/{sort_by}")
    Call<MovieQueryResponse> getMovies(
            @Path("sort_by") String sortBy,
            @Query("api_key") String api_key,
            @Query("page") Integer page);

    @GET("/movie/{id}/videos")
    Call<String> getTrailerUrl(
            @Path("id") String id;
    );
}
