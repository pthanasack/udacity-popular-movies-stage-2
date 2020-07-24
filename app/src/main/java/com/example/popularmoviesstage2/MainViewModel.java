package com.example.popularmoviesstage2;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmoviesstage2.database.Favorite;
import com.example.popularmoviesstage2.database.FavoriteDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Favorite>> allFavoritesList;

    public MainViewModel(Application application) {
        super(application);
        //instantiate favorite database
        FavoriteDatabase mFavoriteDatabase = FavoriteDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the favorites from the DataBase");
        allFavoritesList = mFavoriteDatabase.favoriteDao().loadAllFavorites();
    }

    public LiveData<List<Favorite>> getAllFavoritesList() {
        return allFavoritesList;
    }
}
