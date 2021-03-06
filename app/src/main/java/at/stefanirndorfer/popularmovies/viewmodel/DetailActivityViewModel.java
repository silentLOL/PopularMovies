package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Iterator;
import java.util.List;

import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.ReviewsQueryResponse;
import at.stefanirndorfer.popularmovies.model.TrailerData;
import at.stefanirndorfer.popularmovies.model.TrailerQueryResponse;
import at.stefanirndorfer.popularmovies.network.RequestMoviesService;
import at.stefanirndorfer.popularmovies.network.RetrofitClientInstance;
import at.stefanirndorfer.popularmovies.utils.ApiConstants;
import at.stefanirndorfer.popularmovies.utils.AppExecutors;
import at.stefanirndorfer.popularmovies.utils.PopularMoviesConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityViewModel extends InternetAwareViewModel {

    private static final String TAG = DetailActivityViewModel.class.getName();
    public static final String PREFERRED_VIDEO_SITE = "YouTube";
    AppDataBase mDataBase;


    private MutableLiveData<Bitmap> mImage = new MutableLiveData<>();
    private MutableLiveData<List<TrailerData>> mTrailerDataList = new MutableLiveData<>();
    private MutableLiveData<ReviewsQueryResponse> mReviewsQueryResponse = new MutableLiveData<>();
    private MutableLiveData<Throwable> mTrailerRequestError = new MutableLiveData<>();
    private MutableLiveData<Throwable> mReviewsRequestError = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsFavorite = new MutableLiveData<>();
    private Movie mMovie;
    private String mTrailerKey;

    public void setMovie(Movie movie) {
        this.mMovie = movie;
    }

    public void fetchMovieImage(ImageView view) {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mImage.postValue(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        if (mMovie != null && !TextUtils.isEmpty(mMovie.getPosterPath())) {
            Picasso.with(view.getContext())
                    .load(PopularMoviesConstants.BASIC_POSTER_PATH + PopularMoviesConstants.DEFAULT_IMAGE_WIDTH + mMovie.getPosterPath())
                    .into(target);
        } else {
            //this will be handled by the view
            mImage.postValue(null);
        }
    }

    public void requestTrailerData() {
        RequestMoviesService service = RetrofitClientInstance.getRetrofitInstance().create(RequestMoviesService.class);
        Call<TrailerQueryResponse> call = service.getTrailerData(String.valueOf(mMovie.getId()), ApiConstants.API_KEY);
        Log.d(TAG, call.request().toString());
        call.enqueue(new Callback<TrailerQueryResponse>() {
            @Override
            public void onResponse(Call<TrailerQueryResponse> call, Response<TrailerQueryResponse> response) {
                Log.d(TAG, "Received trailer data");
                if (response.body() != null) {
                    TrailerQueryResponse result = response.body();
                    if (result != null) {
                        extractTrailerKeyAndPostData(result);
                    }
                } else if (response.errorBody() != null) {
                    mTrailerRequestError.postValue(new Throwable(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<TrailerQueryResponse> call, Throwable t) {
                Log.e(TAG, "Error calling for a movie trailer: " + t.getMessage());
                mTrailerRequestError.postValue(t);
            }
        });
    }

    public void requestReviewData() {
        RequestMoviesService service = RetrofitClientInstance.getRetrofitInstance().create(RequestMoviesService.class);
        Call<ReviewsQueryResponse> call = service.getReviewData(String.valueOf(mMovie.getId()), ApiConstants.API_KEY);
        Log.d(TAG, call.request().toString());
        call.enqueue(new Callback<ReviewsQueryResponse>() {
            @Override
            public void onResponse(Call<ReviewsQueryResponse> call, Response<ReviewsQueryResponse> response) {
                Log.d(TAG, "Received reviews data");
                if (response.body() != null) {
                    ReviewsQueryResponse result = response.body();
                    if (result != null) {
                        mReviewsQueryResponse.postValue(result);
                    }
                } else if (response.errorBody() != null) {
                    mReviewsRequestError.postValue(new Throwable(response.errorBody().toString()));
                }
            }

            @Override
            public void onFailure(Call<ReviewsQueryResponse> call, Throwable t) {
                Log.e(TAG, "Error calling for a movie reviews: " + t.getMessage());
                mReviewsRequestError.postValue(t);
            }
        });
    }

    private void extractTrailerKeyAndPostData(TrailerQueryResponse result) {
        List<TrailerData> temTrailerDataList = result.getTrailerData();
        Iterator<TrailerData> it = temTrailerDataList.iterator();
        while (it.hasNext()) {
            if (!it.next().getSite().equals(PREFERRED_VIDEO_SITE)) {
                it.remove();
            }
        }
        if (!temTrailerDataList.isEmpty()) {
            mTrailerDataList.postValue(temTrailerDataList);
        } else {
            mTrailerRequestError.postValue(new Throwable("No video of preferred type: " + PREFERRED_VIDEO_SITE + " found"));
        }
    }

    /**
     * this must be called after initialisation
     *
     * @param mDataBase
     */
    public void setDataBase(AppDataBase mDataBase) {
        this.mDataBase = mDataBase;
    }

    public void addCurrentMovieToDataBase() {
        Log.d(TAG, "Adding " + mMovie.getTitle() + " to the database");
        AppExecutors.getInstance().diskIO().execute(() -> mDataBase.movieDao().insertFavoriteMovie(mMovie));
        mIsFavorite.postValue(true);
    }

    public void removeCurrentMovieFromDataBase() {
        Log.d(TAG, "Removing " + mMovie.getTitle() + " from the database");
        AppExecutors.getInstance().diskIO().execute(() -> mDataBase.movieDao().deleteFavoriteMovie(mMovie));
        mIsFavorite.postValue(false);
    }

    /**
     * queries the favorite movies database to check if movie is stored
     */
    public void checkIfMovieIsFavorite() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            Movie m = mDataBase.movieDao().loadMovieById(mMovie.getId());
            mIsFavorite.postValue(m != null);
        });
    }

    //
    // live data getters
    //

    public MutableLiveData<Bitmap> getImage() {
        return mImage;
    }

    public MutableLiveData<Boolean> isFavorite() {
        return mIsFavorite;
    }

    public MutableLiveData<List<TrailerData>> getTrailerQueryResponse() {
        return mTrailerDataList;
    }

    public MutableLiveData<Throwable> getTrailerRequestError() {
        return mTrailerRequestError;
    }

    public MutableLiveData<Throwable> getReviewRequestError() {
        return mReviewsRequestError;
    }

    public LiveData<ReviewsQueryResponse> getReviewsQueryResponse() {
        return mReviewsQueryResponse;
    }

    //
    // movie data getters
    //

    public Movie getMovie() {
        return mMovie;
    }

    public String getVoteCountString() {
        return String.valueOf(mMovie.getVoteCount());
    }

    public String getVoteAverageString() {
        if (mMovie.getVoteAverage() != null) {
            return String.valueOf(mMovie.getVoteAverage());
        }
        return "";
    }

    public String getPopularityString() {
        if (mMovie.getPopularity() != null) {
            return String.valueOf(mMovie.getPopularity());
        }
        return "";
    }

    public String getOriginalLanguageString() {
        if (!TextUtils.isEmpty(mMovie.getOriginalLanguage())) {
            return mMovie.getOriginalLanguage();
        }
        return "";
    }

    public String getOriginalTitleString() {
        if (!TextUtils.isEmpty(mMovie.getOriginalTitle())) {
            return mMovie.getOriginalTitle();
        }
        return "";
    }

    public String getGenresString() {
        if (mMovie.getGenreIds() != null && !mMovie.getGenreIds().isEmpty()) {
            return PopularMoviesConstants.getGenreById(mMovie.getGenreIds());
        }
        return "";
    }

    public String getOverviewString() {
        if (!TextUtils.isEmpty(mMovie.getOverview())) {
            return mMovie.getOverview();
        }
        return "";
    }

    /**
     * we only want to thow the year and assume it is consistently the first 4 characters
     *
     * @return
     */
    public String getReleaseYear() {
        if (!TextUtils.isEmpty(mMovie.getReleaseDate())) {
            String fullDate = mMovie.getReleaseDate();
            return fullDate.substring(0, 4);
        }
        return "";
    }

    /**
     * @returns the Key of a movies trailer needed to start trailer-intent
     */
    public String getTrailerKey() {
        return mTrailerKey;
    }


}
