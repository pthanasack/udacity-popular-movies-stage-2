package com.example.popularmoviesstage2.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorites")
public class Favorite {

    @PrimaryKey
    @NonNull
    private String movieid;
    private String movieTitle;
    private String movieJson;


    public Favorite(String movieid, String movieTitle, String movieJson) {
        this.movieTitle = movieTitle;
        this.movieid = movieid;
        this.movieJson = movieJson;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieJson() {
        return movieJson;
    }

    public void setMovieJson(String movieJson) {
        this.movieJson = movieJson;
    }

}
