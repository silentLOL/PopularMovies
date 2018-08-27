package at.stefanirndorfer.popularmovies.network;

import at.stefanirndorfer.popularmovies.model.MovieQueryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestMoviesService {

    @GET("/3/discover/movie")
    Call<MovieQueryResponse> getMovies(
            @Query("api_key") String api_key,
            @Query("sort_by") String sortBy,
            @Query("include_video") Boolean includeVideo,
            @Query("page") Integer page);
}
