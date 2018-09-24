package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.List;

import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.MovieQueryResponse;
import at.stefanirndorfer.popularmovies.model.MoviesOrder;
import at.stefanirndorfer.popularmovies.network.RequestMoviesService;
import at.stefanirndorfer.popularmovies.network.RetrofitClientInstance;
import at.stefanirndorfer.popularmovies.utils.ApiConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends InternetAwareViewModel {

    private static final String TAG = MainActivityViewModel.class.getName();
    public static final MoviesOrder DEFAULT_ORDER = MoviesOrder.POPULAR;

    private AppDataBase mDataBase;

    private MoviesOrder mSortBy;
    private MovieQueryResponse mLatestQueryData;
    private LiveData<List<Movie>> mFavoriteMovieList;
    private MutableLiveData<List<Movie>> mNetworkMovieList = new MutableLiveData<>();

    public MainActivityViewModel() {
        this.mSortBy = DEFAULT_ORDER;
    }

    /**
     * triggers a network call or a db-query to refresh our movie data
     */
    public void requestMovieData() {
        if (mSortBy != MoviesOrder.FAVORITES) {
            doNetworkRequest();
        } else {
            doFavoriteMovieDatabaseQuery();
        }
    }

    /**
     * clears all preexisting data and calls the database for
     * all favorite movies
     */
    public void doFavoriteMovieDatabaseQuery() {
        Log.d(TAG, "Reqeuesting Favorite movies from database");
        mFavoriteMovieList = mDataBase.movieDao().loadAllFavoriteMovies();
    }

    private void doNetworkRequest() {
        int page = 1;
        if (mLatestQueryData != null) {
            // if movies are null the viewModel was just created or the sorting criteria wa changed
            // and the data were thrown away
            page = mLatestQueryData.getPage() + 1;
        }
        RequestMoviesService service = RetrofitClientInstance.getRetrofitInstance().create(RequestMoviesService.class);
        Call<MovieQueryResponse> call = service.getMovies(mSortBy.toString(), ApiConstants.API_KEY, page);
        Log.d(TAG, call.request().toString());
        call.enqueue(new Callback<MovieQueryResponse>() {
            @Override
            public void onResponse(Call<MovieQueryResponse> call, Response<MovieQueryResponse> response) {
                Log.d(TAG, "Received response");
                if (response.body() != null) {
                    MovieQueryResponse result = response.body();
                    if (result != null && !result.getMovies().isEmpty()) {
                        mLatestQueryData = result;
                        updateNetworkMovieList();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieQueryResponse> call, Throwable t) {
                Log.e(TAG, "Error calling for movies: " + t.getMessage());
            }
        });
    }


    private void updateNetworkMovieList() {
        if (mLatestQueryData != null && mLatestQueryData.getPage() == 1) {
            //this means we did a first fetch of data and given old data may be from an obsolete sort-order
            Log.d(TAG, "Populating new movie list with " + mLatestQueryData.getMovies().size() + " movie items");
            mNetworkMovieList.setValue(mLatestQueryData.getMovies());
        } else {
            Log.d(TAG, "Appending " + mLatestQueryData.getMovies().size() + " movies to existing movie list");
            // else we append the result of the latest request
            for (Movie currElem :
                    mLatestQueryData.getMovies()) {
                mNetworkMovieList.getValue().add(currElem);
            }
        }
        mNetworkMovieList.postValue(mNetworkMovieList.getValue());
    }

    private void resetNetworkMovieData() {
        Log.d(TAG, "Resetting Network Movie list");
        mLatestQueryData = null;
        if (mNetworkMovieList != null && mNetworkMovieList.getValue() != null) {
            mNetworkMovieList.getValue().clear();
        }
    }

    //
    // LiveData Getters
    //
    public MutableLiveData<List<Movie>> getNetworkMovieList() {
        return mNetworkMovieList;
    }

    public LiveData<List<Movie>> getFavoritesMovieList() {
        return mFavoriteMovieList;
    }

    //
    // Getters and Setters
    //
    public MoviesOrder getSortMoviesBy() {
        return mSortBy;
    }

    public void setSortMoviesBy(String sortMoviesBy) {
        Log.d(TAG, "SortMoviesBy is set to: " + sortMoviesBy);
        if (!sortMoviesBy.equals(this.mSortBy.toString())) {
            this.mSortBy = MoviesOrder.getMovieOrderByString(sortMoviesBy);
            Log.d(TAG, "Sort criteria has changed to: " + sortMoviesBy);
            // this will cause the next request to query for page 1
            resetNetworkMovieData();
        }
    }

    public void setDataBase(AppDataBase mDataBase) {
        this.mDataBase = mDataBase;
    }


    /**
     * LifeCycle handling
     */

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared");
    }
}
