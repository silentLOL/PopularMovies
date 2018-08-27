package at.stefanirndorfer.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import at.stefanirndorfer.popularmovies.utils.PopularMoviesUtils;

public abstract class InternetAwareLiveDataViewModel extends ViewModel {

    private static final String TAG = InternetAwareLiveDataViewModel.class.getName();
    private MutableLiveData<Boolean> mHasInternetConnection = new MutableLiveData<>();

    public void checkInternetConnection(){
        boolean isOnline = PopularMoviesUtils.isOnline();
        Log.d(TAG, "Device online: " + isOnline);
        mHasInternetConnection.postValue(isOnline);
    }

    public LiveData<Boolean> isInternetConnected() {
        return mHasInternetConnection;
    }

}
