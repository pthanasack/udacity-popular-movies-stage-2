package com.example.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.popularmoviesstage2.database.Favorite;
import com.example.popularmoviesstage2.database.FavoriteDatabase;
import com.example.popularmoviesstage2.utilities.AppExecutors;
import com.example.popularmoviesstage2.utilities.NetworkUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class DetailActivity extends AppCompatActivity {

    final String imageBaseUrl =  "http://image.tmdb.org/t/p/";
    final String moviePosterMainSize = "w342";
    final String youtubeBaseUrl = "http://youtu.be/";

    final String ADD_TO_FAVORITE="Add to favorites";
    final String REMOVE_FROM_FAVORITE="Remove from favorites";

    private ProgressBar mLoadingIndicator;
    private TextView mReviewsTv;
    private TextView mTrailersErrorTv;
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mMovieReleaseDate;
    private TextView mMovieRuntime;
    private TextView mMovieVoteAverage;
    private ImageView mOverviewDividerIv;
    private TextView mReviewsTitleTv;
    private ImageView mTrailersDividerIv;
    private TextView mTrailersTitleTv;
    private Button mAddFavoriteBtn;


    private RecyclerView mReviewsRv;
    private ReviewsAdapter mReviewsAdapter;

    private LinearLayout mTrailersLv;
    private FavoriteDatabase mFavoriteDatabase;

    private boolean mIsMovieAlreadyFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);
        //initiate all textviews and progress bar
        mLoadingIndicator = (ProgressBar) findViewById(R.id.reviews_loading_indicator);
        mMovieTitle = (TextView) findViewById(R.id.tv_original_title);
        mMovieOverview = (TextView) findViewById(R.id.tv_overview);
        mReviewsTv = (TextView) findViewById(R.id.tv_reviews);
        mTrailersErrorTv = (TextView) findViewById(R.id.tv_trailers_error);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieRuntime = (TextView) findViewById(R.id.tv_runtime);
        mMovieVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        mOverviewDividerIv =(ImageView) findViewById(R.id.overview_divider);
        mReviewsTitleTv = (TextView) findViewById(R.id.tv_Reviews_title);
        mTrailersDividerIv = (ImageView) findViewById(R.id.trailers_divider);
        mTrailersTitleTv = (TextView) findViewById(R.id.tv_trailers_title);
        mAddFavoriteBtn = (Button)findViewById(R.id.add_favorite_button);

        //instantiate favorite database
        mFavoriteDatabase= FavoriteDatabase.getInstance(getApplicationContext());

        // get the intent and display related info
        Intent mDetailIntent = getIntent();
        if (mDetailIntent.hasExtra(Intent.EXTRA_TEXT)) {

            final String jsonMovieString = mDetailIntent.getStringExtra(Intent.EXTRA_TEXT);
            try {

                //retrieve movie ID from JSON
                JSONObject jsonMovieDisplayed = new JSONObject(jsonMovieString);
                final String movieID = jsonMovieDisplayed.getString("id");

                //set API parameters
                String mApiKey = getString(R.string.API_KEY);
                String mApiKeyQueryParam = getString(R.string.APIKEY_QUERY_PARAM);

                //load + display the movie details
                URL mMovieDetailsUrl = NetworkUtilities.getMovieDetailsURL(mApiKey, movieID, mApiKeyQueryParam);
                new getMovieDetails().execute(mMovieDetailsUrl);

                //load reviews
                URL mMovieReviewsUrl = NetworkUtilities.getMovieReviewsURL(mApiKey, movieID, mApiKeyQueryParam);
                new getMovieReviews().execute(mMovieReviewsUrl);

                //load + display trailers
                URL mMovieTrailersUrl = NetworkUtilities.getMovieTrailersURL(mApiKey, movieID, mApiKeyQueryParam);
                new getMovieTrailers().execute(mMovieTrailersUrl);

                this.isMovieAlreadyFavorite(movieID);
                //check if Movie isAlready a favorite and modify the button label
                if (mIsMovieAlreadyFavorite)
                    {
                        mAddFavoriteBtn.setText(REMOVE_FROM_FAVORITE);
                    }
                else {
                    mAddFavoriteBtn.setText(ADD_TO_FAVORITE);
                }
                //add onCLick Listener to Favorite button
                mAddFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaveButtonClicked(movieID, jsonMovieString) ;   }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //adapter + recycler view for reviews
        mReviewsRv = (RecyclerView) findViewById(R.id.recycler_view_reviews);
        //Create a GridLayoutManager
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //Set the layoutManager on RecyclerView
        mReviewsRv.setLayoutManager(linearLayoutManager);

        //new ReviewsAdapter
        mReviewsAdapter = new ReviewsAdapter();
        //set the adapter to the RecyclerView
        mReviewsRv.setAdapter(mReviewsAdapter);

        //same for trailers
        mTrailersLv = (LinearLayout) findViewById(R.id.linearlayout_trailers);
        //but data for trailers is set OnPostExecute because no need for a RecyclerView for the trailers


    }

    //method to know if the movie is already a favorite
    public void isMovieAlreadyFavorite(String inMovieidParam){
        final String inMovieid = inMovieidParam;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mFavoriteDatabase.favoriteDao().getFavoriteByMovieid(inMovieid) == null) {
                    mIsMovieAlreadyFavorite = false;
                } else
                {
                    mIsMovieAlreadyFavorite = true;
                }
            }
        });
    }



      //method to save/unsave a favorite
    public void onSaveButtonClicked(String inMovieidParam, String inMovieJsonParam) {
        final Favorite inFavorite = new Favorite(inMovieidParam, inMovieJsonParam);
        final String inMovieid = inMovieidParam;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mFavoriteDatabase.favoriteDao().getFavoriteByMovieid(inMovieid) == null) {
                    mFavoriteDatabase.favoriteDao().insertFavorite(inFavorite);
            } else
                {
                    mFavoriteDatabase.favoriteDao().deleteFavorite(inFavorite);
                }
                finish();
            }
        });

    }


    public class getMovieDetails extends AsyncTask<URL, Void, String> {
        //Override OnPreExecute to show loading indicator
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            //Hide Make all textviews invisble while poster is loading
            mMovieTitle.setVisibility(View.INVISIBLE);
            mMovieOverview.setVisibility(View.INVISIBLE);
            mMovieReleaseDate.setVisibility(View.INVISIBLE);
            mMovieRuntime.setVisibility(View.INVISIBLE);
            mMovieVoteAverage.setVisibility(View.INVISIBLE);
            mOverviewDividerIv.setVisibility(View.INVISIBLE);
            mReviewsTitleTv.setVisibility(View.INVISIBLE);
            mTrailersDividerIv.setVisibility(View.INVISIBLE);
            mTrailersTitleTv.setVisibility(View.INVISIBLE);
            mAddFavoriteBtn.setVisibility(View.INVISIBLE);
        }


        //Override doInBackground to fetch data
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String mMoviesDetails = null;

            try {
                mMoviesDetails = NetworkUtilities.getResponseFromHttpUrl(url);

            } catch (IOException io) {
                io.printStackTrace();
            }
            return mMoviesDetails;

        }

        @Override
        protected void onPostExecute(String mMoviesDataString) {
            //hide loading indicator since loading is done
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            //if there is data
            if (mMoviesDataString != null && !mMoviesDataString.equals("")) {
                try {
                    JSONObject jsonMovieDisplayed = new JSONObject(mMoviesDataString);
                    //pass the MovieData to the Adapter then show recyclerview
                    //display movie poster in full
                    String posterPathMovieDisplayed = jsonMovieDisplayed.getString("poster_path");
                    String fullPosterPathMovieDisplayed = imageBaseUrl + "/" + moviePosterMainSize + "/" + posterPathMovieDisplayed;
                    ImageView mMoviePoster = (ImageView) findViewById(R.id.iv_detail_poster);
                    // show progress bar and hide everything whil picture is loading
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    Picasso.get().load(fullPosterPathMovieDisplayed).into(mMoviePoster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (mLoadingIndicator != null) {
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                mMovieTitle.setVisibility(View.VISIBLE);
                                mMovieOverview.setVisibility(View.VISIBLE);
                                mMovieReleaseDate.setVisibility(View.VISIBLE);
                                mMovieRuntime.setVisibility(View.VISIBLE);
                                mMovieVoteAverage.setVisibility(View.VISIBLE);
                                mOverviewDividerIv.setVisibility(View.VISIBLE);
                                mReviewsTitleTv.setVisibility(View.VISIBLE);
                                mTrailersDividerIv.setVisibility(View.VISIBLE);
                                mTrailersTitleTv.setVisibility(View.VISIBLE);
                                mAddFavoriteBtn.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    //display movie original title
                    String detailMovieDisplayedString = jsonMovieDisplayed.getString("title");
                    mMovieTitle.setText(detailMovieDisplayedString);

                    //display movie overview
                    detailMovieDisplayedString = jsonMovieDisplayed.getString("overview");
                    mMovieOverview.setText(detailMovieDisplayedString);

                    //display movie release date
                    detailMovieDisplayedString = jsonMovieDisplayed.getString("release_date");
                    mMovieReleaseDate.setText(detailMovieDisplayedString.substring(0,4));

                    //display movie vote_average
                    detailMovieDisplayedString = jsonMovieDisplayed.getString("vote_average");
                    mMovieVoteAverage.setText(detailMovieDisplayedString + "/10");

                    //display movie runtime
                    detailMovieDisplayedString = Integer.toString(jsonMovieDisplayed.getInt("runtime"));
                    mMovieRuntime.setText(detailMovieDisplayedString + " min");
                    // showMovieData();

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mReviewsTv.setText("Error Catching Movie Data");
                // showErrorMessage();
            }


        }
    }

    public class getMovieReviews extends AsyncTask<URL, Void, String> {
        //Override OnPreExecute to show loading indicator
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }


        //Override doInBackground to fetch data
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String mMoviesDetails = null;

            try {
                mMoviesDetails = NetworkUtilities.getResponseFromHttpUrl(url);

            } catch (IOException io) {
                io.printStackTrace();
            }
            return mMoviesDetails;

        }

        @Override
        protected void onPostExecute(String mMoviesDataString) {
            //hide loading indicator since loading is done
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            //if there is data
            if (mMoviesDataString != null && !mMoviesDataString.equals("")) {
                //convert the data in array format from JSON STring
                String[] mMoviesDetails = NetworkUtilities.getArrayFromMovieJSON(mMoviesDataString);

                //pass the MovieData to the Adapter then show recyclerview
                mReviewsAdapter.setReviewsData(mMoviesDetails);
                mReviewsRv.setVisibility(View.VISIBLE);

            } else {
                mReviewsTv.setVisibility(View.VISIBLE);
                mReviewsTv.setText("Error Catching Movie Data");

            }

        }
    }

    public class getMovieTrailers extends AsyncTask<URL, Void, String> {


        //Override OnPreExecute to show loading indicator
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }


        //Override doInBackground to fetch data
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String mMoviesDetails = null;

            try {
                mMoviesDetails = NetworkUtilities.getResponseFromHttpUrl(url);

            } catch (IOException io) {
                io.printStackTrace();
            }

            return mMoviesDetails;

        }


        @Override
        protected void onPostExecute(String mMoviesDataString) {
            //hide loading indicator since loading is done
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            //if there is data
            if (mMoviesDataString != null && !mMoviesDataString.equals("")) {
                //convert the data in array format from JSON STring
                String[] mtrailersArray = NetworkUtilities.getArrayFromMovieJSON(mMoviesDataString);
                //for each trailer, create a row
               for (int i=0;i<mtrailersArray.length;i++){
                    LinearLayout row = new LinearLayout(DetailActivity.this);
                    row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    try {
                        //get the trailer data from the JSON
                        JSONObject trailerData = new JSONObject(mtrailersArray[i]);
                        String trailerKey = trailerData.getString("key");
                        String trailerName = trailerData.getString("name");
                        String trailerType = trailerData.getString("type");
                        //create a new play button for the trailer
                        Button playBtn = new Button(DetailActivity.this);
                        playBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        //create a new label textview for the trailer
                        TextView trailerNameTv = new TextView(DetailActivity.this);
                        //build the URI for the Intent
                        Uri.Builder youtubeUriBuilder = Uri.parse(youtubeBaseUrl).buildUpon()
                                .appendPath(trailerKey);
                        Uri youtubeUri = youtubeUriBuilder.build();
                        final Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                        //apply the intent to a OnClickListener applied to the button
                        playBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (youtubeIntent.resolveActivity(getPackageManager()) != null){
                                    startActivity(youtubeIntent);
                                }
                            }
                        });
                        //set the text for the button and the label
                        playBtn.setText("PLAY ");
                        trailerNameTv.setText(trailerType + ": " + trailerName);
                        //add the button and label to the row
                        row.addView(playBtn);
                        row.addView(trailerNameTv , LinearLayout.LayoutParams.MATCH_PARENT);
                        //add the full row to the Trailer LinearLayout
                        mTrailersLv.addView(row);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mTrailersLv.setVisibility(View.VISIBLE);
            }

            else {
                mTrailersErrorTv.setText("Error Catching Movie Data");
                mTrailersErrorTv.setVisibility(View.VISIBLE);
                // showErrorMessage();
            }

        }




    }


}
