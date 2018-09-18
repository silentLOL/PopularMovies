package at.stefanirndorfer.popularmovies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.databinding.ActivityDetailBinding;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.viewmodel.LiveDataDetailActivityViewModel;

public class DetailActivity extends AppCompatActivity implements InternetDialogListener {

    private static final String TAG = DetailActivity.class.getName();
    private static final String INTERNET_DIALOG_TAG = "internet_dialog_tag";

    private LiveDataDetailActivityViewModel viewModel;
    private ActivityDetailBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
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
            mBinding.pbLoadingProgress.setVisibility(View.GONE);
            mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
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
                viewModel.fetchMovieImage(mBinding.ivMoviePoster);
                populateTextViews();
            }
        };
        viewModel.isInternetConnected().observe(this, hasInternetObserver);
    }

    private void subscribeOnImage() {
        final Observer<Bitmap> movieImageObserver = bitmap -> {
            mBinding.pbLoadingProgress.setVisibility(View.GONE);
            if (bitmap == null) {
                mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvErrorMessage.setVisibility(View.GONE);
                mBinding.ivMoviePoster.setVisibility(View.VISIBLE);
            }
            mBinding.ivMoviePoster.setImageBitmap(bitmap);
        };
        viewModel.getImage().observe(this, movieImageObserver);
    }

    private void populateTextViews() {
        Movie movie = viewModel.getMovie();
        if (movie == null) {
            throw new IllegalArgumentException("Movie should not be null at this point!");
        }
        handleStringResult(viewModel.getVoteCountString(), mBinding.voteCountTv);
        handleStringResult(viewModel.getVoteAverageString(), mBinding.averageVoteTv);
        handleStringResult(viewModel.getPopularityString(), mBinding.popularityTv);
        handleStringResult(viewModel.getOriginalLanguageString(), mBinding.originalLanguageTv);
        handleStringResult(viewModel.getOriginalTitleString(), mBinding.originalTitleTv);
        handleStringResult(viewModel.getGenresString(), mBinding.genresTv);
        handleStringResult(viewModel.getOverviewString(), mBinding.overviewTv);
        handleStringResult(viewModel.getReleaseDate(), mBinding.releaseDateTv);
    }

    /**
     * sets the given string as text into textView
     * if string is empty "no data available" is set
     *
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
