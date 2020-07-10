package com.example.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {
    private TextView tvMoviesData;

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoadingIndicator;

    private String mBaseUrlCurrentlyUsed;
    private String BASE_URL_CURRENTLY_USED = "BASE_URL_CURRENTLY_USED";

    //function to adjust number of columns for gridlayoutmanager
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the item
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2; //to keep the grid aspect
        return nColumns;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMoviesData = (TextView) findViewById(R.id.tv_movie_data);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movies);
        //Create a GridLayoutManager

        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, numberOfColumns(), GridLayoutManager.VERTICAL, false);

        //Set the layoutManager on RecyclerView
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //new MoviesAdapter
        mMoviesAdapter = new MoviesAdapter(this);
        //set the adapter to the RecyclerView
        mRecyclerView.setAdapter(mMoviesAdapter);

        //load the data according to wether there is a sort type already used
        String mApiKey = getString(R.string.API_KEY);
        String mApiKeyQueryParam = getString(R.string.APIKEY_QUERY_PARAM);
        if(savedInstanceState!=null)
        {
            mBaseUrlCurrentlyUsed = savedInstanceState.getString(BASE_URL_CURRENTLY_USED);
        }

        if (mBaseUrlCurrentlyUsed == null) {
            //get the Movies Data - by default, sorted by popular
            //String mBaseUrl = getString(R.string.POPULAR_MOVIES_BASE_URL);
            String mBaseUrlCurrentlyUsed = getString(R.string.POPULAR_MOVIES_BASE_URL);
            loadMoviesData(mApiKey, mBaseUrlCurrentlyUsed, mApiKeyQueryParam);
        } else{
            loadMoviesData(mApiKey, mBaseUrlCurrentlyUsed, mApiKeyQueryParam);
        }
    }


    //Override MovieAdapterClickHolder onClick method
    @Override
    public void onClick(String mMovieData) {
        //Intent parameters: context, destination class
        Context context = this;
        Class destinationClass = DetailActivity.class;
        //new intent with extra_text
        Intent startDetailedActivityIntent = new Intent(context, destinationClass);
        startDetailedActivityIntent.putExtra(Intent.EXTRA_TEXT, mMovieData);
        //start new activity with Intent
        startActivity(startDetailedActivityIntent);
        //Toast.makeText(context, mMovieData, Toast.LENGTH_SHORT).show();
    }

    //to show the recyclerview if all is ok
    private void showMovieData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        tvMoviesData.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        tvMoviesData.setVisibility(View.VISIBLE);
    }

    //inflate the sort menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.sort, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    //reload with corresponding url on sort item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_popular) {
            //get the Movies Data - sorted by popular
            String mApiKey = getString(R.string.API_KEY);
            //String mBaseUrl = getString(R.string.POPULAR_MOVIES_BASE_URL);
            mBaseUrlCurrentlyUsed = getString(R.string.POPULAR_MOVIES_BASE_URL);
            String mApiKeyQueryParam = getString(R.string.APIKEY_QUERY_PARAM);
            loadMoviesData(mApiKey, mBaseUrlCurrentlyUsed, mApiKeyQueryParam);
            return true;
        }
        if (id == R.id.sort_top_rated) {
            //get the Movies Data - sorted by top rated
            String mApiKey = getString(R.string.API_KEY);
           // String mBaseUrl = getString(R.string.TOP_RATED_MOVIE_BASE_URL);
            mBaseUrlCurrentlyUsed = getString(R.string.TOP_RATED_MOVIE_BASE_URL);
            String mApiKeyQueryParam = getString(R.string.APIKEY_QUERY_PARAM);
            loadMoviesData(mApiKey, mBaseUrlCurrentlyUsed, mApiKeyQueryParam);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO initiate mBaseUrlCurrentlyUsed also when coming back from detail activity?
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the mBaseUrlCurrentlyUsed
        String urlToSave = mBaseUrlCurrentlyUsed;
        outState.putString(BASE_URL_CURRENTLY_USED, urlToSave);
    }

    //method to load Movies Data according to url chosen (popular or top rated)
    private void loadMoviesData(String mApiKey, String mBaseUrl, String mApiKeyQueryParam) {
        URL mMoviesDataUrl = NetworkUtilities.getMoviesDataURL(mApiKey, mBaseUrl, mApiKeyQueryParam);
        new getMoviesData().execute(mMoviesDataUrl);
    }

    public class getMoviesData extends AsyncTask<URL, Void, String> {
        //Override OnPreExecute to show loading indicator
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }


        //Override doInBackground to fetch data
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String mMoviesDataString = null;


            try {
                mMoviesDataString = NetworkUtilities.getResponseFromHttpUrl(url);

            } catch (IOException io) {
                io.printStackTrace();
            }
            return mMoviesDataString;

        }

        @Override
        protected void onPostExecute(String mMoviesDataString) {
            //hide loading indicator since loading is done
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            //if there is data
            if (mMoviesDataString != null && !mMoviesDataString.equals("")) {
                //convert the data in array format from JSON STring
                String[] mArrayMovieData = NetworkUtilities.getArrayFromMovieJSON(mMoviesDataString);
                // String dictText = "";
                //change the textview text to debug
//                for (int i = 0; i < mArrayMovieData.length; i++) {
//                    dictText += mArrayMovieData[i] + "\n\n";
//                }
//                tvMoviesData.setText(dictText);
//                tvMoviesData.setVisibility(View.VISIBLE);

                //pass the MovieData to the Adapter then show recyclerview
                mMoviesAdapter.setMovieData(mArrayMovieData);
                showMovieData();

            } else {
                tvMoviesData.setText("Error Catching Movie Data");
                showErrorMessage();
            }

        }
    }
}
