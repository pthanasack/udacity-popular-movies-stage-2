package com.example.popularmoviesstage2.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorites")
public class Favorite {

    @PrimaryKey
    @NonNull private String movieid;
    private String movieJson;


    public Favorite(String movieid, String movieJson){

        this.movieid = movieid;
        this.movieJson = movieJson;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    public String getMovieJson() {
        return movieJson;
    }

    public void setMovieJson(String movieJson) {
        this.movieJson = movieJson;
    }

}
