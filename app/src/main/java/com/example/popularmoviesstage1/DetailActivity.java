package com.example.popularmoviesstage1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    final String imageBaseUrl = "http://image.tmdb.org/t/p/";
    final String moviePosterMainSize = "w342";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get the intent and display related info
        Intent mDetailIntent = getIntent();
        if (mDetailIntent.hasExtra(Intent.EXTRA_TEXT)) {

            String jsonMovieString = mDetailIntent.getStringExtra(Intent.EXTRA_TEXT);
            try {
                JSONObject jsonMovieDisplayed = new JSONObject(jsonMovieString);
                //display movie poster in full
                String posterPathMovieDisplayed = jsonMovieDisplayed.getString("poster_path");
                String fullPosterPathMovieDisplayed = imageBaseUrl + "/" + moviePosterMainSize + "/" + posterPathMovieDisplayed;
                ImageView mMoviePoster = (ImageView) findViewById(R.id.iv_detail_poster);
                Picasso.get().load(fullPosterPathMovieDisplayed).into(mMoviePoster);

                //display movie original title
                String detailMovieDisplayedString = jsonMovieDisplayed.getString("original_title");
                TextView mMovieDetail = (TextView) findViewById(R.id.tv_original_title);
                mMovieDetail.setText("Original Title: " + detailMovieDisplayedString);

                //display movie overview
                detailMovieDisplayedString = jsonMovieDisplayed.getString("overview");
                mMovieDetail = (TextView) findViewById(R.id.tv_overview);
                mMovieDetail.setText("Movie Overview: " + detailMovieDisplayedString);

                //display movie release date
                detailMovieDisplayedString = jsonMovieDisplayed.getString("release_date");
                mMovieDetail = (TextView) findViewById(R.id.tv_release_date);
                mMovieDetail.setText("Release Date: " + detailMovieDisplayedString);

                //display movie vote_average
                detailMovieDisplayedString = jsonMovieDisplayed.getString("vote_average");
                mMovieDetail = (TextView) findViewById(R.id.tv_vote_average);
                mMovieDetail.setText("User Rating (Vote Average): " + detailMovieDisplayedString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
