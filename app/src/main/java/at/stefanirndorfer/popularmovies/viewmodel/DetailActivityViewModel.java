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
import at.stefanirndorfer.popularmovies.utils.PopularMoviesConstants;

public class DetailActivityViewModel extends InternetAwareLiveDataViewModel {

    private static final String TAG = DetailActivityViewModel.class.getName();
    AppDataBase mDataBase;


    MutableLiveData<Bitmap> image = new MutableLiveData<>();
    private Movie mMovie;

    public void setMovie(Movie movie) {
        this.mMovie = movie;
    }

    public void fetchMovieImage(ImageView view) {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image.postValue(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        if (mMovie != null && !TextUtils.isEmpty(mMovie.getPosterPath())) {
            Picasso.with(view.getContext())
                    .load(PopularMoviesConstants.BASIC_POSTER_PATH + PopularMoviesConstants.DEFAULT_IMAGE_WIDTH + mMovie.getPosterPath())
                    .into(target);
        } else {
            //this will be handled by the view
            image.postValue(null);
        }
    }

    //
    // live data getters
    //

    public MutableLiveData<Bitmap> getImage() {
        return image;
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

    public String getReleaseDate() {
        if (!TextUtils.isEmpty(mMovie.getReleaseDate())) {
            return mMovie.getReleaseDate();
        }
        return "";
    }

    /**
     * this must be called after initialisation
     * @param mDataBase
     */
    public void setDataBase(AppDataBase mDataBase) {
        this.mDataBase = mDataBase;
    }

    public void addCurrentMovieToDataBase() {
        Log.d(TAG, "Adding " + mMovie.getTitle() + " to the database");
        mDataBase.movieDao().insertFavoriteMovie(mMovie);
    }

    public void removeCurrentMovieFromDataBase() {
        Log.d(TAG, "Removing " + mMovie.getTitle() + " from the database");
        mDataBase.movieDao().deleteFavoriteMovie(mMovie);
    }
}
