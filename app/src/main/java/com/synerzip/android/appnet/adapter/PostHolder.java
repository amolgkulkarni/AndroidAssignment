package com.synerzip.android.appnet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.synerzip.android.appnet.R;
import com.synerzip.android.appnet.utils.ImageDownloader;

/**
 * ViewHolder for RecyclerView
 */
public class PostHolder extends RecyclerView.ViewHolder {
    protected ImageView avatar;
    protected TextView name;
    protected TextView post;
    private Integer mCount = 0;
    private ImageDownloader mCurrentTask;

    public PostHolder(View view) {
        super(view);
        this.avatar = (ImageView) view.findViewById(R.id.avatar);
        this.name = (TextView) view.findViewById(R.id.name);
        this.post = (TextView) view.findViewById(R.id.post);
    }

    /**
     * Sets task used for getting data for current Holder.
     * Cancel current pending task if new task is triggered.
     * @param imageDownloader {ImageDownloader} Background task for retrieving data
     */
    public void setCurrentTask (ImageDownloader imageDownloader) {
        if (null != this.mCurrentTask) {
            this.mCurrentTask.cancel(true);
        }
        this.mCurrentTask = imageDownloader;
    }
}