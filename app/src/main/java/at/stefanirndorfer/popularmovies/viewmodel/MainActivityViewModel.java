package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;

import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.MovieQueryResponse;
import at.stefanirndorfer.popularmovies.model.MoviesOrder;
import at.stefanirndorfer.popularmovies.model.ThumbnailWrapper;
import at.stefanirndorfer.popularmovies.network.RequestMoviesService;
import at.stefanirndorfer.popularmovies.network.RetrofitClientInstance;
import at.stefanirndorfer.popularmovies.utils.ApiConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends InternetAwareLiveDataViewModel {

    private static final String TAG = MainActivityViewModel.class.getName();
    public static final MoviesOrder DEFAULT_ORDER = MoviesOrder.POPULAR;

    private AppDataBase mDataBase;

    private MoviesOrder mSortBy;
    private MutableLiveData<ThumbnailWrapper[]> mThumbnailsResults = new MutableLiveData<>();
    private MovieQueryResponse mLatestQueryData;
    private ArrayList<Movie> mGlobalMovieList;

    public MainActivityViewModel() {
        this.mSortBy = DEFAULT_ORDER;
    }

    /**
     * triggers a network call to refresh our movie data
     */
    public void requestMovieData() {
        int page = 1;
        if (mLatestQueryData != null) {
            // if movies are null the viewModel was just created or the sorting criteria wa changed
            // and the data were thwrowen away
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
                        updateGlobalMoviesList();
                        updateThumbnailWrappers();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieQueryResponse> call, Throwable t) {
                Log.e(TAG, "Error calling for movies: " + t.getMessage());
            }
        });
    }

    private void updateGlobalMoviesList() {
        if (mLatestQueryData.getPage() == 1) {
            mGlobalMovieList = new ArrayList<>();
        }
        for (Movie currElem :
                mLatestQueryData.getMovies()) {
            mGlobalMovieList.add(currElem);
        }
    }

    /**
     * extracts the ThumbnailWrappers from the global Movies list
     * and posts the newly generated array to the view
     */
    private void updateThumbnailWrappers() {
        ThumbnailWrapper[] newThumbnails = new ThumbnailWrapper[mGlobalMovieList.size()];
        for (int i = 0; i < mGlobalMovieList.size(); i++) {
            newThumbnails[i] = new ThumbnailWrapper(mGlobalMovieList.get(i).getId(), (mGlobalMovieList.get(i).getPosterPath()));
        }
        mThumbnailsResults.postValue(newThumbnails);
    }


    /**
     * @param movieId
     * @return the full Movie object of the given movieId
     * null in case the id does not match any of our cached movie data
     */
    public Movie getMovieById(int movieId) {
        for (Movie currElement : mGlobalMovieList) {
            if (currElement.getId() == movieId) {
                return currElement;
            }
        }
        //this should never be the case
        Log.e(TAG, "Selected Movie data not available!");
        return null;
    }

    //
    // LiveData Getters
    //
    public LiveData<ThumbnailWrapper[]> getMovieResults() {
        return mThumbnailsResults;
    }

    //
    // Getters and Setters
    //
    public MoviesOrder getSortMoviesBy() {
        return mSortBy;
    }

    public void setSortMoviesBy(String sortMoviesBy) {
        //TODO: override equals method in MovieOrder
        Log.d(TAG, "SortMoviesBy is set to: " + sortMoviesBy);
        if (!sortMoviesBy.equals(this.mSortBy.toString())) {
            this.mSortBy = MoviesOrder.getMovieOrderByString(sortMoviesBy);
            Log.d(TAG, "Sort criteria has changed to: " + sortMoviesBy);
            // this will cause the next request to query for page 1
            resetData();
        }
    }

    private void resetData() {
        mLatestQueryData = null;
        if (mGlobalMovieList != null && !mGlobalMovieList.isEmpty()) {
            mGlobalMovieList.clear();
            mGlobalMovieList = null;
        }
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
