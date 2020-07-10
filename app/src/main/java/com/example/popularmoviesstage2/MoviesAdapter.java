package com.example.popularmoviesstage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

// the Recycler View
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    final String imageBaseUrl = "http://image.tmdb.org/t/p/";
    final String moviePosterMainSize = "w185";
    String[] mMovieData;


    //defines the clickhandler interface and attribute
    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(String mMovieData);
    }

    //default constructor that takes a clickHandler
    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    //the ViewHolder that implements OnClickListener
    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieImageView;
        //ViewHolder Constructor gets the ImageView id
        public MoviesAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);
            //set the onclick listener
            view.setOnClickListener(this);
        }

        //Override onClick to pass correct data
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String clickMovieData = mMovieData[adapterPosition];
            mClickHandler.onClick(clickMovieData);
        }

    }
    //Override onCreateViewHolder to inflate the movie_main.xml
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_main;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    //override OnBindVIewHolder to set the source for the image view
    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {

        String jsonstringMovieDisplayed = mMovieData[position];
        try {
            JSONObject jsonMovieDisplayed = new JSONObject(jsonstringMovieDisplayed);
            String posterPathMovieDisplayed = jsonMovieDisplayed.getString("poster_path");
            String fullPosterPathMovieDisplayed = imageBaseUrl + "/" + moviePosterMainSize + "/" + posterPathMovieDisplayed;
            Picasso.get().load(fullPosterPathMovieDisplayed).into(moviesAdapterViewHolder.mMovieImageView);
//            String titleMovieDisplayed = jsonMovieDisplayed.getString("title");
//            moviesAdapterViewHolder.mMovieTextViewTitle.setText(titleMovieDisplayed);
//            String yearMovieDisplayed = jsonMovieDisplayed.getString("release_date");
//            moviesAdapterViewHolder.mMovieTextViewYear.setText(yearMovieDisplayed);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //override getItemCount
    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.length;
    }

    //set the data within the adapter
    public void setMovieData(String[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

}
