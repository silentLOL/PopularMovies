package at.stefanirndorfer.popularmovies.network;

import at.stefanirndorfer.popularmovies.model.MovieQueryResponse;
import at.stefanirndorfer.popularmovies.model.TrailerQueryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestMoviesService {

    @GET("/3/movie/{sort_by}")
    Call<MovieQueryResponse> getMovies(
            @Path("sort_by") String sortBy,
            @Query("api_key") String api_key,
            @Query("page") Integer page
    );

    @GET("3/movie/{id}/videos")
    Call<TrailerQueryResponse> getTrailerData(
            @Path("id") String id,
            @Query("api_key") String api_key
    );

    @GET("3/movie/{id}/reviews")
    Call<TrailerQueryResponse> getReviewData(
            @Path("id") String id,
            @Query("api_key") String api_key
    );
}
