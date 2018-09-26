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
import at.stefanirndorfer.popularmovies.model.TrailerData;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private static final String TAG = TrailersAdapter.class.getName();

    final private TrailerListItemClickListener mOnClickListener;
    private TrailerData[] mTrailerData;

    public TrailersAdapter(TrailerListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TrailersViewHolder viewHolder = new TrailersViewHolder(view);
        return viewHolder;
    }

    public void setTrailerData(TrailerData[] trailerData) {
        Log.d(TAG, "Setting mTrailerData. Length: " + trailerData.length);
        this.mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersViewHolder holder, int position) {
        TrailerData currElem = mTrailerData[position];
        String trailer_item_text = holder.itemView.getContext().getString(R.string.trailer_list_item_text);

        String trailerCaption = trailer_item_text + " " + (position + 1);
        holder.mTitleTextView.setText(trailerCaption);
        holder.mKey = currElem.getKey();
    }

    @Override
    public int getItemCount() {
        if (mTrailerData != null) {
            Log.d(TAG, "Trailer array length: " + mTrailerData.length);
        }
        return mTrailerData == null ? 0 : mTrailerData.length;
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView mTitleTextView;
        public String mKey;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.trailer_item_title_tv);
            mKey = null;
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onTrailerListItemClick(clickedPosition, mKey);
        }
    }

    public interface TrailerListItemClickListener {
        void onTrailerListItemClick(int clickedItemIndex, String key);
    }
}
