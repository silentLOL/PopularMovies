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

import java.util.List;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.adapter.ReviewsAdapter;
import at.stefanirndorfer.popularmovies.adapter.TrailersAdapter;
import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.databinding.ActivityDetailBinding;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.Review;
import at.stefanirndorfer.popularmovies.model.ReviewsQueryResponse;
import at.stefanirndorfer.popularmovies.model.TrailerData;
import at.stefanirndorfer.popularmovies.viewmodel.DetailActivityViewModel;

public class DetailActivity extends AppCompatActivity implements InternetDialogListener, ReviewsAdapter.ReviewListItemClickListener, TrailersAdapter.TrailerListItemClickListener {

    private static final String TAG = DetailActivity.class.getName();
    private static final String INTERNET_DIALOG_TAG = "internet_dialog_tag";
    public static final String VND_YOUTUBE = "vnd.youtube:";
    public static final String WEB_YOUTUBE = "http://www.youtube.com/watch?v=";

    private DetailActivityViewModel viewModel;
    private ActivityDetailBinding mBinding;

    /* Review Recycler View*/
    private RecyclerView mRecyclerViewReviews;
    private LinearLayoutManager mLinearLayoutManagerReviews;
    private ReviewsAdapter mReviewsAdapter;

    /* Trailers Recycler View*/
    private RecyclerView mRecyclerViewTrailers;
    private LinearLayoutManager mLinearLayoutManagerTrailers;
    private TrailersAdapter mTrailersAdapter;

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
        setUpReviewsRecycleView();
        setUpTrailersRecycleView();
        initViewModel(movie);
        subscribeOnViewModelDataUpdates();
        // we want to find out if the displayed movie comes from our favorite database by
        viewModel.checkIfMovieIsFavorite();
        viewModel.checkInternetConnection();
    }

    private void setUpTrailersRecycleView() {
        Log.d(TAG, "Setting up TrailerRecyclerView");
        mRecyclerViewTrailers = findViewById(R.id.recyclerview_trailers);
        mLinearLayoutManagerTrailers = new LinearLayoutManager(this);
        mRecyclerViewTrailers.setLayoutManager(mLinearLayoutManagerTrailers);
        mRecyclerViewTrailers.setHasFixedSize(true);
        mTrailersAdapter = new TrailersAdapter(this);
        mRecyclerViewTrailers.setAdapter(mTrailersAdapter);
    }

    private void setUpReviewsRecycleView() {
        Log.d(TAG, "Setting up ReviewsRecyclerView");
        mRecyclerViewReviews = findViewById(R.id.recyclerview_reviews);
        mLinearLayoutManagerReviews = new LinearLayoutManager(this);
        mRecyclerViewReviews.setLayoutManager(mLinearLayoutManagerReviews);
        mRecyclerViewReviews.setHasFixedSize(true);
        mReviewsAdapter = new ReviewsAdapter(this);
        mRecyclerViewReviews.setAdapter(mReviewsAdapter);
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
        subscribeOnTrailerLoadingError();
        subscribeOnReviewData();
        subscribeOnReviewsLoadingError();
        subscribeOnIsFavoriteMovie();
    }

    private void subscribeOnReviewsLoadingError() {
        final Observer<Throwable> reviewsErrorResponseObserver = (Throwable t) -> {
            Log.e(TAG, "Error loading review data: " + t.getMessage());
            setUpNoReviewsAvailableUI();
        };
        viewModel.getReviewRequestError().observe(this, reviewsErrorResponseObserver);
    }

    private void subscribeOnTrailerLoadingError() {
        final Observer<Throwable> trailerErrorResponseObserver = (Throwable t) -> {
            Log.e(TAG, "Error loading trailer data: " + t.getMessage());
            setUpNoTrailerAvailableUI();
        };
        viewModel.getTrailerRequestError().observe(this, trailerErrorResponseObserver);
    }

    private void subscribeOnTrailerData() {
        final Observer<List<TrailerData>> trailerQueryResponseObserver = (List<TrailerData> response) -> {
            if (response != null && !response.isEmpty()) {
                setUpTrailerUIVisible();
                mTrailersAdapter.setTrailerData(response.toArray(new TrailerData[0]));
            } else {
                setUpNoTrailerAvailableUI();
            }
        };
        viewModel.getTrailerQueryResponse().observe(this, trailerQueryResponseObserver);
    }


    private void subscribeOnReviewData() {
        final Observer<ReviewsQueryResponse> reviewsQueryResponseObserver = (ReviewsQueryResponse response) -> {
            if (response != null && response.getReviews() != null && !response.getReviews().isEmpty()) {
                setUpReviewsUIVisible();
                mReviewsAdapter.setReviewData(response.getReviews().toArray(new Review[0]));
            } else {
                setUpNoReviewsAvailableUI();
            }
        };
        viewModel.getReviewsQueryResponse().observe(this, reviewsQueryResponseObserver);
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

    private void requestReviews() {
        viewModel.requestReviewData();
        //set LoadingSpinner visible
        mBinding.reviewsLayout.reviewsErrorTv.setVisibility(View.GONE);
        mBinding.reviewsLayout.reviewsLoadingPb.setVisibility(View.VISIBLE);
        mBinding.reviewsLayout.reviewLabelTv.setVisibility(View.GONE);
    }

    private void setUpReviewsUIVisible() {
        mBinding.reviewsLayout.reviewsErrorTv.setVisibility(View.GONE);
        mBinding.reviewsLayout.reviewsLoadingPb.setVisibility(View.GONE);
        mBinding.reviewsLayout.reviewLabelTv.setVisibility(View.VISIBLE);
    }

    private void setUpNoReviewsAvailableUI() {
        mBinding.reviewsLayout.reviewsErrorTv.setVisibility(View.VISIBLE);
        mBinding.reviewsLayout.reviewsLoadingPb.setVisibility(View.GONE);
        mBinding.reviewsLayout.reviewLabelTv.setVisibility(View.GONE);
    }


    private void requestTrailers() {
        viewModel.requestTrailerData();
        //set LoadingSpinner visible
        mBinding.trailerLayout.trailerErrorTv.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLoadingPb.setVisibility(View.VISIBLE);
        mBinding.trailerLayout.trailerLabelTv.setVisibility(View.GONE);
    }

    private void setUpTrailerUIVisible() {
        mBinding.trailerLayout.trailerErrorTv.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLoadingPb.setVisibility(View.GONE);
        mBinding.trailerLayout.trailerLabelTv.setVisibility(View.VISIBLE);
    }

    private void setUpNoTrailerAvailableUI() {
        mBinding.trailerLayout.trailerErrorTv.setVisibility(View.VISIBLE);
        mBinding.trailerLayout.trailerLoadingPb.setVisibility(View.GONE);
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

    @Override
    public void onReviewListItemClick(int clickedItemIndex, String url) {
        Log.d(TAG, "Click event on trailer received");
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onTrailerListItemClick(int clickedItemIndex, String key) {
        Log.d(TAG, "Click event on review received");
        Log.d(TAG, WEB_YOUTUBE + key);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VND_YOUTUBE + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(WEB_YOUTUBE + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
