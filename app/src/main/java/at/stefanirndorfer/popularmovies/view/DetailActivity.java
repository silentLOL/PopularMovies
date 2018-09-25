package at.stefanirndorfer.popularmovies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.adapter.ReviewsAdapter;
import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.databinding.ActivityDetailBinding;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.TrailerQueryResponse;
import at.stefanirndorfer.popularmovies.viewmodel.DetailActivityViewModel;

public class DetailActivity extends AppCompatActivity implements InternetDialogListener, ReviewsAdapter.ReviewListItemClickListener {

    private static final String TAG = DetailActivity.class.getName();
    private static final String INTERNET_DIALOG_TAG = "internet_dialog_tag";
    public static final String VND_YOUTUBE = "vnd.youtube:";
    public static final String WEB_YOUTUBE = "http://www.youtube.com/watch?v=";

    private DetailActivityViewModel viewModel;
    private ActivityDetailBinding mBinding;
    private RecyclerView mRecyclerViewReviews;
    private LinearLayoutManager mLinearLayoutManager;
    private ReviewsAdapter mReviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.d(TAG, "onCreate");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Movie movie = extractMovieFromIntent();
        if (movie != null && movie.getTitle() != null) {
            setTitle(movie.getTitle());
        }
        setUpRecycleView();
        initViewModel(movie);
        subscribeOnViewModelDataUpdates();
        // we want to find out if the displayed movie comes from our favorite database by
        viewModel.checkIfMovieIsFavorite();
        viewModel.checkInternetConnection();
    }

    private void setUpRecycleView() {
        Log.d(TAG, "Setting up RecyclerView");
        mRecyclerViewReviews = findViewById(R.id.recyclerview_reviews);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewReviews.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewReviews.setHasFixedSize(true);
        mReviewAdapter = new ReviewsAdapter(this);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);
    }


    private void initViewModel(Movie movie) {
        viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        viewModel.setDataBase(AppDataBase.getInstance(getApplicationContext()));
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
        subscribeOnTrailerData();
        subsdribeOnTrailerLoadingError();
        subscribeOnIsFavoriteMovie();
    }

    private void subsdribeOnTrailerLoadingError() {
        final Observer<Throwable> trailerErrorResponseObserver = (Throwable t) -> {
            Log.e(TAG, "Error loading trailer data: " + t.getMessage());
            setUpNoTrailerAvailableUI();
        };
        viewModel.getTrailerRequestError().observe(this, trailerErrorResponseObserver);
    }

    private void subscribeOnTrailerData() {
        final Observer<TrailerQueryResponse> trailerQueryResponseObserver = (TrailerQueryResponse response) -> {
            //TODO: Implement a RecyclerView and list all trailers -- for now we pick the first one
            if (response != null && response.getTrailerData() != null && !response.getTrailerData().isEmpty()) {
                setUpTrailerUIVisible();
            } else {
                setUpNoTrailerAvailableUI();
            }
        };
        viewModel.getTrailerQueryResponse().observe(this, trailerQueryResponseObserver);
    }


    private void subscribeOnIsFavoriteMovie() {
        final Observer<Boolean> isFavoriteMovieObserver = this::updateIsFavoirteMovieIndicator;
        viewModel.isFavorite().observe(this, isFavoriteMovieObserver);
    }

    private void subscribeOnHasInternetConnection() {
        final Observer<Boolean> hasInternetObserver = (Boolean hasInternetConnection) -> {
            if (hasInternetConnection != null && !hasInternetConnection) {
                Log.d(TAG, "Internet connection: " + hasInternetConnection);
                showInternetDialogue();
            } else {
                // we only request remote data if internet connection is given
                viewModel.fetchMovieImage(mBinding.ivMoviePoster);
                requestTrailers();
                requestReviews();
                populateUI();
            }
        };
        viewModel.isInternetConnected().observe(this, hasInternetObserver);
    }

    private void requestReviews() {
        viewModel.requestReviewData();
        //set LoadingSpinner visible
        mBinding.reviewsLayout.reviewsErrorTv.setVisibility(View.GONE);
        mBinding.reviewsLayout.reviewsLoadingPb.setVisibility(View.VISIBLE);
        mBinding.reviewsLayout.reviewLabelTv.setVisibility(View.GONE);
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

    private void populateUI() {
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
        handleStringResult(viewModel.getReleaseYear(), mBinding.releaseDateTv);
    }


    private void requestTrailers() {
        viewModel.requestTrailerData();
        //set LoadingSpinner visible
        mBinding.trailerLayout.trailerErrorTv.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLoadingPb.setVisibility(View.VISIBLE);
        mBinding.trailerLayout.playButton.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLabelTv.setVisibility(View.GONE);
    }

    private void setUpTrailerUIVisible() {
        mBinding.trailerLayout.trailerErrorTv.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLoadingPb.setVisibility(View.GONE);
        mBinding.trailerLayout.playButton.setVisibility(View.VISIBLE);
        mBinding.trailerLayout.trailerLabelTv.setVisibility(View.VISIBLE);
    }

    private void setUpNoTrailerAvailableUI() {
        mBinding.trailerLayout.trailerErrorTv.setVisibility(View.VISIBLE);
        mBinding.trailerLayout.trailerLoadingPb.setVisibility(View.GONE);
        mBinding.trailerLayout.playButton.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLabelTv.setVisibility(View.GONE);
    }

    private void updateIsFavoirteMovieIndicator(boolean isFavorite) {
        if (isFavorite) {
            mBinding.favButtons.btFavoritesOn.setVisibility(View.VISIBLE);
            mBinding.favButtons.btFavoritesOff.setVisibility(View.GONE);
        } else {
            mBinding.favButtons.btFavoritesOn.setVisibility(View.GONE);
            mBinding.favButtons.btFavoritesOff.setVisibility(View.VISIBLE);

        }
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

    /**
     * brings up a "connect-to-internet" dialog in case there is no internet available
     */
    private void showInternetDialogue() {
        InternetDialog greetingDialog = InternetDialog.newInstance(this);
        greetingDialog.show(getSupportFragmentManager(), INTERNET_DIALOG_TAG);
    }


    /**
     * No-Internet-Dialog was handled by the user.
     * if the dialog is done we check again if the internet connection is given now
     */
    @Override
    public void onDialogDone() {
        viewModel.checkInternetConnection();
    }

    public void addToFavouritesButtonClicked(View view) {
        viewModel.addCurrentMovieToDataBase();
    }

    public void removeFromFavouritesButtonClicked(View view) {
        viewModel.removeCurrentMovieFromDataBase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    public void playTrailer(View view) {
        String videoId = viewModel.getTrailerKey();
        Log.d(TAG, WEB_YOUTUBE + videoId);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VND_YOUTUBE + videoId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(WEB_YOUTUBE + videoId));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void onReviewListItemClick(int clickedItemIndex, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }
}
