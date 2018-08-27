package at.stefanirndorfer.popularmovies.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.model.MoviesOrder;
import at.stefanirndorfer.popularmovies.viewmodel.SettingsActivityViewModel;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getName();
    private SettingsActivityViewModel viewModel;

    private RadioButton mPopularityRadioButton;
    private RadioButton mVoteAverageRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mPopularityRadioButton = findViewById(R.id.rb_popularity);
        mVoteAverageRadioButton = findViewById(R.id.rb_vote_average);
        initViewModel();
        extractIntentAndSetValue();
    }

    private void extractIntentAndSetValue() {
        Intent intent = getIntent();
        if (intent != null) {
            String order = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (order.equals(MoviesOrder.POPULAR.toString())) {
                mPopularityRadioButton.setChecked(true);
                mVoteAverageRadioButton.setChecked(false);
                viewModel.setSortBy(MoviesOrder.POPULAR);
            } else {
                if (order.equals(MoviesOrder.TOP_RATED.toString())) {
                    mPopularityRadioButton.setChecked(false);
                    mVoteAverageRadioButton.setChecked(true);
                    viewModel.setSortBy(MoviesOrder.TOP_RATED);
                } else {
                    Log.e(TAG, "No valid sort-movies-by data passed in!");
                }
            }
        }
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SettingsActivityViewModel.class);
    }

    public void onPopularitySelected(View view) {
        viewModel.setSortBy(MoviesOrder.POPULAR);
    }

    public void onVoteAverageSelected(View view) {
        viewModel.setSortBy(MoviesOrder.TOP_RATED);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "BackPressed -- returning: " + viewModel.getSortBy().toString());
        returnSelection();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d(TAG, "Option item home pressed");
            returnSelection();
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }

    private void returnSelection() {
        Intent intent = new Intent();
        intent.putExtra("movieOrder", viewModel.getSortBy().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
