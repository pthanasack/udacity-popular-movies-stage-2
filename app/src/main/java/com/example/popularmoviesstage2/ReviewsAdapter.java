package com.example.popularmoviesstage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

//Reviews Adapter
class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    String[] reviewsData;

    //Viewholder
    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            this.mReviewAuthor = (TextView) itemView.findViewById(R.id.review_author_tv);
            this.mReviewContent = (TextView) itemView.findViewById(R.id.review_content_tv);
        }
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.reviews_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        String jsonstringReviewsData = reviewsData[position];


        try {
            JSONObject jsonReviewsData = new JSONObject(jsonstringReviewsData);
            String ReviewAuthor = jsonReviewsData.getString("author");
            reviewsAdapterViewHolder.mReviewAuthor.setText(ReviewAuthor);
            String ReviewContent = jsonReviewsData.getString("content");
            reviewsAdapterViewHolder.mReviewContent.setText(ReviewContent);
//
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (reviewsData == null) return 0;
        return reviewsData.length;
    }

    //to set the data in the adapter
    public void setReviewsData(String[] reviewsData) {
        this.reviewsData = reviewsData;
        notifyDataSetChanged();
    }
}
