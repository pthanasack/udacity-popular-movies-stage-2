package com.example.popularmoviesstage2;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtilities {

    //Build the URL to get the movies for Main activity
    public static URL getMoviesDataURL(String apiKey, String popularMoviesBaseUrl, String apiKeyQueryParam) {

        Uri.Builder builtUri = Uri.parse(popularMoviesBaseUrl).buildUpon()
                .appendQueryParameter(apiKeyQueryParam, apiKey);
        URL uMoviesDataUrl = null;
        try {
            uMoviesDataUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return uMoviesDataUrl;
    }
    // Build the URL to get the detail page for a given movieid
    public static URL getMovieReviewsURL(String apiKey, String movieID, String apiKeyQueryParam) {
        String movieReviewsById = "http://api.themoviedb.org/3/movie/" + movieID + "/reviews";
        Uri.Builder builtUri = Uri.parse(movieReviewsById).buildUpon()
                .appendQueryParameter(apiKeyQueryParam, apiKey);
        URL uMoviesDataUrl = null;
        try {
            uMoviesDataUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return uMoviesDataUrl;
    }
    // Build the URL to get the reviews for a given movieid
    public static URL getMovieDetailsURL(String apiKey, String movieID, String apiKeyQueryParam) {
        String movieReviewsById = "http://api.themoviedb.org/3/movie/" + movieID;
        Uri.Builder builtUri = Uri.parse(movieReviewsById).buildUpon()
                .appendQueryParameter(apiKeyQueryParam, apiKey);
        URL uMoviesDataUrl = null;
        try {
            uMoviesDataUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return uMoviesDataUrl;
    }
    //Build the URL to get the trailes for a given movieid
    public static URL getMovieTrailersURL(String apiKey, String movieID, String apiKeyQueryParam) {
        String movieReviewsById = "http://api.themoviedb.org/3/movie/" + movieID + "/videos";
        Uri.Builder builtUri = Uri.parse(movieReviewsById).buildUpon()
                .appendQueryParameter(apiKeyQueryParam, apiKey);
        URL uMoviesDataUrl = null;
        try {
            uMoviesDataUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return uMoviesDataUrl;
    }

    //generic method to get HttpResponse to URL request
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    //specific method to get an String[] Array from the movie data json in string format - each entry is json movie data
    public static String[] getArrayFromMovieJSON(String jsonString) {
        String[] arrayMovieData = null;


        try {
            JSONObject movieData = new JSONObject(jsonString);
            JSONArray movieDataResults = movieData.getJSONArray("results");
            arrayMovieData = new String[movieDataResults.length()];
            for (int i = 0; i < movieDataResults.length(); i++) {
                JSONObject movie = movieDataResults.getJSONObject(i);
                arrayMovieData[i] = movie.toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //if there is no result string in the JSON then try to get the youtube string
        if (arrayMovieData == null){
            try {
                JSONObject movieData = new JSONObject(jsonString);
                JSONArray movieDataResults = movieData.getJSONArray("youtube");
                arrayMovieData = new String[movieDataResults.length()];
                for (int i = 0; i < movieDataResults.length(); i++) {
                    JSONObject movie = movieDataResults.getJSONObject(i);
                    arrayMovieData[i] = movie.toString();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayMovieData;
    }



}
