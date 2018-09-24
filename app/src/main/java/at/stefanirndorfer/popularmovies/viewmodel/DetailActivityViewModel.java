package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.MovieQueryResponse;
import at.stefanirndorfer.popularmovies.network.RequestMoviesService;
import at.stefanirndorfer.popularmovies.network.RetrofitClientInstance;
import at.stefanirndorfer.popularmovies.utils.ApiConstants;
import at.stefanirndorfer.popularmovies.utils.AppExecutors;
import at.stefanirndorfer.popularmovies.utils.PopularMoviesConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityViewModel extends InternetAwareLiveDataViewModel {

    private static final String TAG = DetailActivityViewModel.class.getName();
    AppDataBase mDataBase;


    MutableLiveData<Bitmap> mImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsFavorite = new MutableLiveData<>();
    private Movie mMovie;

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

    public void requestTrailerUrl(){
        RequestMoviesService service = RetrofitClientInstance.getRetrofitInstance().create(RequestMoviesService.class);
        Call<String> call = service.getTrailerUrl(String.valueOf(mMovie.getId()));
        Log.d(TAG, call.request().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "Received trailer url");
                if (response.body() != null) {
                    String result = response.body();
                    if (result != null && !TextUtils.isEmpty(result)) {
                       Log.d(TAG, "Trailer url received: " + result);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Error calling for a movie trailer: " + t.getMessage());
            }
        });
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
    public void checkIfMovieIsFavorite(){
        AppExecutors.getInstance().diskIO().execute(() -> {
           Movie m =  mDataBase.movieDao().loadMovieById(mMovie.getId());
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


}
