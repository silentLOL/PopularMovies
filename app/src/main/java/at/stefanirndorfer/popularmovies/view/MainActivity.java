package at.stefanirndorfer.popularmovies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.adapter.ThumbnailsAdapter;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.model.MoviesOrder;
import at.stefanirndorfer.popularmovies.model.ThumbnailWrapper;
import at.stefanirndorfer.popularmovies.viewmodel.LiveDataMainActivityViewModel;

public class MainActivity extends AppCompatActivity implements ThumbnailsAdapter.ThumbnailsAdapterOnClickHandler, InternetDialogListener {

    private static final String TAG = MainActivity.class.getName();
    private static final String INTERNET_DIALOG_TAG = "internet_dialog_tag";
    private static final int REQUEST_CODE_SORT_BY = 1;

    private LiveDataMainActivityViewModel viewModel;
    private RecyclerView mRecyclerViewMovies;
    private GridLayoutManager mGridLayoutManager;
    private ThumbnailsAdapter mThumbnailsAdapter;
    private boolean mCurrentlyLoading;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        setUpRecycleView();
        initViewModel();
        viewModel.checkInternetConnection();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult is being called.");
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SORT_BY) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra("movieOrder")) {
                    String sortBy = data.getStringExtra("movieOrder");
                    Log.d(TAG, "Received sort-by from settings: " + sortBy);
                    boolean needsRequest = false;
                    if (!sortBy.equals(viewModel.getSortMoviesBy().toString())) {
                        //TODO: Show spinner
                        needsRequest = true;
                    }
                    if (sortBy.equals(MoviesOrder.POPULAR.toString())) {
                        viewModel.setSortMoviesBy(MoviesOrder.POPULAR);
                    } else {
                        viewModel.setSortMoviesBy(MoviesOrder.TOP_RATED);
                    }
                    if (needsRequest) {
                        //this will trigger the request after successful internet-check
                        viewModel.checkInternetConnection();
                    }
                }
            }
        }
    }

    private void requestMovieData() {
        Log.d(TAG, "Requesting Movie Data");
        mCurrentlyLoading = true;
        viewModel.requestMovieData();
    }


    private void setUpRecycleView() {
        Log.d(TAG, "Setting up RecyclerView");
        mRecyclerViewMovies = findViewById(R.id.recyclerview_movies);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewOnScrollListener = initOnScrollListener();
        mRecyclerViewMovies.setLayoutManager(mGridLayoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);
        mThumbnailsAdapter = new ThumbnailsAdapter(this);
        mRecyclerViewMovies.setAdapter(mThumbnailsAdapter);
        mRecyclerViewMovies.addOnScrollListener(mRecyclerViewOnScrollListener);
    }

    private RecyclerView.OnScrollListener initOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mGridLayoutManager.getChildCount();
                    totalItemCount = mGridLayoutManager.getItemCount();
                    pastVisibleItems = mGridLayoutManager.findFirstVisibleItemPosition();

                    if (!mCurrentlyLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            Log.v(TAG, "Requesting more movie data");
                            viewModel.checkInternetConnection();
                        }
                    }
                }
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LiveDataMainActivityViewModel.class);
        subscribe();
    }

    private void subscribe() {
        Log.d(TAG, "Subcribeing to observable values");
        final Observer<ThumbnailWrapper[]> movieThumbnailsOutputObserver = thumbnailWrappers -> {
            Log.d(TAG, "Movie data (re)loaded");
            mThumbnailsAdapter.setThumbnailData(thumbnailWrappers);
            mCurrentlyLoading = false;
        };
        viewModel.getMovieResults().observe(this, movieThumbnailsOutputObserver);

        final Observer<Boolean> hasInternetObserver = (Boolean hasInternetConnection) -> {
            if (hasInternetConnection != null && !hasInternetConnection) {
                Log.d(TAG, "Internet connection: " + hasInternetConnection);
                showInternetDialogue();
            } else {
                // we only request movie data if internet connection is given
                Log.d(TAG, "We have Internet! Requesting Movie data");
                requestMovieData();
            }
        };
        viewModel.isInternetConnected().observe(this, hasInternetObserver);
    }

    private void showInternetDialogue() {
        InternetDialog greetingDialog = InternetDialog.newInstance(this);
        greetingDialog.show(getSupportFragmentManager(), INTERNET_DIALOG_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int movieId) {
        Log.d(TAG, "movie with id " + movieId + " clicked!");
        Movie selectedMovie = viewModel.getMovieById(movieId);
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(this, destinationClass);
        intentToStartDetailActivity.putExtra("MOVIE", selectedMovie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onDialogDone() {
        viewModel.checkInternetConnection();
    }

    /**
     * Life Cycle Handling
     */

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
