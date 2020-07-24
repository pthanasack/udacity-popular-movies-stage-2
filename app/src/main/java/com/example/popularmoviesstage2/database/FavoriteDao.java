package com.example.popularmoviesstage2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY Movieid DESC")
    LiveData<List<Favorite>> loadAllFavorites();

    @Insert
    void insertFavorite(Favorite favorite);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);

    @Query("SELECT * FROM favorites WHERE Movieid = :id")
    Favorite getFavoriteByMovieid(String id);

}
