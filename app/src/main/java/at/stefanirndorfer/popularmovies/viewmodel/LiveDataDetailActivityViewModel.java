package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.utils.PopularMoviesConstants;

public class LiveDataDetailActivityViewModel extends InternetAwareLiveDataViewModel {

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
