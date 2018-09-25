package at.stefanirndorfer.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.model.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private static final String TAG = ReviewsAdapter.class.getName();

    final private ReviewListItemClickListener mOnClickListener;
    private Review[] mReviewData;

    public ReviewsAdapter(ReviewListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    public void setReviewData(Review[] reviewData) {
        Log.d(TAG, "Setting mReviewData. Length: " + reviewData.length);
        this.mReviewData = reviewData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currElem = mReviewData[position];
        holder.mTitleTextView.setText(currElem.getContent());
        holder.mAuthorTextView.setText(currElem.getAuthor());
        holder.mUrl = currElem.getUrl();
    }

    @Override
    public int getItemCount() {
        if (mReviewData != null) {
            Log.d(TAG, "Review array length: " + mReviewData.length);
        }
        return mReviewData == null ? 0 : mReviewData.length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView mAuthorTextView;
        public final TextView mTitleTextView;
        public String mUrl;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = itemView.findViewById(R.id.review_author_tv);
            mTitleTextView = itemView.findViewById(R.id.review_title_tv);
            mUrl = null;
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onReviewListItemClick(clickedPosition, mUrl);
        }
    }

    public interface ReviewListItemClickListener {
        void onReviewListItemClick(int clickedItemIndex, String url);
    }
}
