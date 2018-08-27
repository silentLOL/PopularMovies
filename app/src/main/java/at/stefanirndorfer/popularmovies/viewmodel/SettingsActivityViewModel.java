package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import at.stefanirndorfer.popularmovies.model.MoviesOrder;
import at.stefanirndorfer.popularmovies.view.SettingsActivity;

public class SettingsActivityViewModel extends ViewModel {

    private static final String TAG = SettingsActivity.class.getName();
    private MoviesOrder mSortBy;

    public MoviesOrder getSortBy() {
        return mSortBy;
    }

    public void setSortBy(MoviesOrder sortBy) {
        Log.d(TAG, "Setting sortBy to: "+ sortBy);
        this.mSortBy = sortBy;
    }
}
