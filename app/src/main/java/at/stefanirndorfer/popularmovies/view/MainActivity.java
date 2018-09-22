package at.stefanirndorfer.popularmovies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.adapter.ThumbnailsAdapter;
import at.stefanirndorfer.popularmovies.database.AppDataBase;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity implements ThumbnailsAdapter.ThumbnailsAdapterOnClickHandler, InternetDialogListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getName();
    private static final String INTERNET_DIALOG_TAG = "internet_dialog_tag";

    private MainActivityViewModel viewModel;
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
        setupSharedPreferences();
        viewModel.checkInternetConnection();
    }


    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        viewModel.setSortMoviesBy(sharedPreferences.getString(getString(R.string.pref_order_key),
                getResources().getString(R.string.pref_order_popular_value)));

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.setDataBase(AppDataBase.getInstance(getApplicationContext()));
        viewModel.doFavoriteMovieDatabaseQuery();
        subscribeOnLiveData();
    }

    private void subscribeOnLiveData() {
        subscribeOnNetworkMovieList();
        subscribeOnFavoriteMovieList();
        subscribeOnInternetObserver();
    }

    private void subscribeOnInternetObserver() {
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

    private void subscribeOnFavoriteMovieList() {
        Log.d(TAG, "Subscribing to favorite movie list");
        final Observer<List<Movie>> favoriteMovieDataObserver = movies -> updateAdapterData(movies);
        viewModel.getFavoritesMovieList().observe(this, favoriteMovieDataObserver);
    }

    private void subscribeOnNetworkMovieList() {
        Log.d(TAG, "Subscribing to network movie list");
        final Observer<List<Movie>> networkMovieDataObserver = movieData -> {
            updateAdapterData(movieData);
        };
        viewModel.getNetworkMovieList().observe(this, networkMovieDataObserver);
    }

    private void updateAdapterData(List<Movie> movieData) {
        if (null != movieData && !movieData.isEmpty()) {
            Log.d(TAG, "Updating the adapter with a list of " + movieData.size() + " items.");
            mThumbnailsAdapter.setMovieData(movieData.toArray(new Movie[0]));
            mCurrentlyLoading = false;
        }
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
    public void onClick(Movie selectedMovie) {
        Log.d(TAG, "movie with id " + selectedMovie.getId() + " clicked!");
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(this, destinationClass);
        intentToStartDetailActivity.putExtra("MOVIE", selectedMovie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onDialogDone() {
        viewModel.checkInternetConnection();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_order_key))) {
            String newOrder = sharedPreferences.getString(getString(R.string.pref_order_key), getResources().getString(R.string.pref_order_popular_value));
            Log.d(TAG, "Setting movie order to " + newOrder);
            String currentOrder = viewModel.getSortMoviesBy().toString();
            if (!currentOrder.equals(newOrder)) {
                viewModel.setSortMoviesBy(newOrder);
                viewModel.checkInternetConnection();
            }
        }
    }

    /**
     * Life Cycle Handling
     */

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // in case we come back from details view and favorites have changed
        if (viewModel.getSortMoviesBy().toString().equals(R.string.pref_order_my_favorites_value)){
            requestMovieData();
        }
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
        //unregister listener
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }


}
