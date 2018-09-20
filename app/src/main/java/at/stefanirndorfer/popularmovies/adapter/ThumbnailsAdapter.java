package at.stefanirndorfer.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import at.stefanirndorfer.popularmovies.R;
import at.stefanirndorfer.popularmovies.model.Movie;
import at.stefanirndorfer.popularmovies.utils.PopularMoviesConstants;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailsAdapterViewHolder> {

    private static final String TAG = ThumbnailsAdapter.class.getName();
    private Movie[] mThumbnailData;
    private final ThumbnailsAdapterOnClickHandler mClickHandler;

    public interface ThumbnailsAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    public class ThumbnailsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mThumbnailImageView;

        ThumbnailsAdapterViewHolder(View itemView) {
            super(itemView);
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie sleectedMovie = mThumbnailData[adapterPosition];
            mClickHandler.onClick(sleectedMovie);
        }
    }

    public ThumbnailsAdapter(ThumbnailsAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public void setMovieData(Movie[] mThumbnailData) {
        Log.d(TAG, "Setting mThumbnailData. Length: " + mThumbnailData.length);
        this.mThumbnailData = mThumbnailData;
        notifyDataSetChanged();
    }

    @Override
    public ThumbnailsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.thumbnail_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ThumbnailsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ThumbnailsAdapterViewHolder holder, int position) {
        Movie currElem = mThumbnailData[position];
        String path = PopularMoviesConstants.BASIC_POSTER_PATH + PopularMoviesConstants.DEFAULT_IMAGE_WIDTH + currElem.getPosterPath();
        Log.d(TAG, "Requesting Image: " + path);
        Picasso.with(holder.itemView.getContext())
                .load(path)
                .into(holder.mThumbnailImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "success loading poster for id: " + currElem.getId());
                        ImageView imageView = holder.mThumbnailImageView.findViewById(R.id.iv_thumbnail);
                        imageView.setVisibility(View.VISIBLE);
                        View parentView = (View) imageView.getParent();
                        parentView.findViewById(R.id.tv_error_message).setVisibility(View.GONE);
                        parentView.findViewById(R.id.pb_thumbnail_image_progress).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "Error loading image for id: " + currElem.getId());
                        ImageView imageView = holder.mThumbnailImageView.findViewById(R.id.iv_thumbnail);
                        imageView.setVisibility(View.GONE);
                        View parentView = (View) imageView.getParent();
                        parentView.findViewById(R.id.pb_thumbnail_image_progress).setVisibility(View.GONE);
                        parentView.findViewById(R.id.tv_error_message).setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mThumbnailData != null) {
            Log.d(TAG, "Thumbnail array length: " + mThumbnailData.length);
        }
        return mThumbnailData == null ? 0 : mThumbnailData.length;
    }
}
