package at.stefanirndorfer.popularmovies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.viewmodel.LiveDataDetailActivityViewModel;

public class DetailActivity extends AppCompatActivity implements InternetDialogListener {

    private static final String TAG = DetailActivity.class.getName();
    private static final String INTERNET_DIALOG_TAG = "internet_dialog_tag";

    private LiveDataDetailActivityViewModel viewModel;

    // image layout refs
    private ImageView mMoviePoster;
    private ProgressBar mLoadingProgress;
    private TextView mErrorTextView;

    // movie data layout refs
    private TextView mVoteCountTextView;
    private TextView mAverageVote;
    private TextView mPopularity;
    private TextView mOriginalLanguage;
    private TextView mOriginalTitle;
    private TextView mGenres;
    private TextView mOverview;
    private TextView mReleaseDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // image layout refs
        mMoviePoster = findViewById(R.id.iv_movie_poster);
        mLoadingProgress = findViewById(R.id.pb_loading_progress);
        mErrorTextView = findViewById(R.id.tv_error_message);

        // movie data layout refs
        mVoteCountTextView = findViewById(R.id.vote_count_tv);
        mAverageVote = findViewById(R.id.average_vote_tv);
        mPopularity = findViewById(R.id.popularity_tv);
        mOriginalLanguage = findViewById(R.id.original_language_tv);
        mOriginalTitle = findViewById(R.id.original_title_tv);
        mGenres = findViewById(R.id.genres_tv);
        mOverview = findViewById(R.id.overview_tv);
        mReleaseDate = findViewById(R.id.release_date_tv);

        Movie movie = extractMovieFromIntent();
        if (movie != null && movie.getTitle() != null) {
            setTitle(movie.getTitle());
        }
        initViewModel(movie);
        subscribeOnViewModelDataUpdates();
        viewModel.checkInternetConnection();
    }


    private void initViewModel(Movie movie) {
        viewModel = ViewModelProviders.of(this).get(LiveDataDetailActivityViewModel.class);

        if (movie != null) {
            viewModel.setMovie(movie);
        } else {
            mLoadingProgress.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    private Movie extractMovieFromIntent() {
        Intent intentToGetHere = getIntent();
        if (intentToGetHere.hasExtra("MOVIE")) {
            return (Movie) intentToGetHere.getParcelableExtra("MOVIE");
        } else {
            Log.e(TAG, "No movie object available!");
        }
        return null;
    }

    private void subscribeOnViewModelDataUpdates() {
        subscribeOnImage();
        subscribeOnHasInternetConnection();
    }

    private void subscribeOnHasInternetConnection() {
        final Observer<Boolean> hasInternetObserver = (Boolean hasInternetConnection) -> {
            if (hasInternetConnection != null && !hasInternetConnection) {
                Log.d(TAG, "Internet connection: " + hasInternetConnection);
                showInternetDialogue();
            } else {
                // we only request remote data if internet connection is given
                viewModel.fetchMovieImage(mMoviePoster);
                populateTextViews();
            }
        };
        viewModel.isInternetConnected().observe(this, hasInternetObserver);
    }

    private void subscribeOnImage() {
        final Observer<Bitmap> movieImageObserver = bitmap -> {
            mLoadingProgress.setVisibility(View.GONE);
            if (bitmap == null) {
                mErrorTextView.setVisibility(View.VISIBLE);
            } else {
                mErrorTextView.setVisibility(View.GONE);
                mMoviePoster.setVisibility(View.VISIBLE);
            }
            mMoviePoster.setImageBitmap(bitmap);
        };
        viewModel.getImage().observe(this, movieImageObserver);
    }

    private void populateTextViews() {
        Movie movie = viewModel.getMovie();
        if (movie == null) {
            Log.e(TAG, "Movie should not be null at this point!");
            return;
        }
        handleStringResult(viewModel.getVoteCountString(), mVoteCountTextView);
        handleStringResult(viewModel.getVoteAverageString(), mAverageVote);
        handleStringResult(viewModel.getPopularityString(), mPopularity);
        handleStringResult(viewModel.getOriginalLanguageString(), mOriginalLanguage);
        handleStringResult(viewModel.getOriginalTitleString(), mOriginalTitle);
        handleStringResult(viewModel.getGenresString(), mGenres);
        handleStringResult(viewModel.getOverviewString(), mOverview);
        handleStringResult(viewModel.getReleaseDate(), mReleaseDate);
    }

    /**
     *  sets the given string as text into textView
     *  if string is empty "no data available" is set
     * @param string
     * @param textView
     */
    private void handleStringResult(String string, TextView textView) {
        if (TextUtils.isEmpty(string)) {
            textView.setText(R.string.not_available);
        } else {
            textView.setText(string);
        }
    }

    private void showInternetDialogue() {
        InternetDialog greetingDialog = InternetDialog.newInstance(this);
        greetingDialog.show(getSupportFragmentManager(), INTERNET_DIALOG_TAG);
    }


    @Override
    public void onDialogDone() {
        viewModel.checkInternetConnection();
    }
}
