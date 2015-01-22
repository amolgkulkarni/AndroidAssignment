package com.synerzip.android.appnet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

import com.synerzip.android.appnet.R;
import com.synerzip.android.appnet.model.PostItem;
import com.synerzip.android.appnet.utils.ImageDownloader;

/**
 * Adapter for RecyclerView.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostHolder> {
    private List<PostItem> mPostItemList;
    private Context mContext;

    public PostsAdapter(Context context, List<PostItem> postItemList) {
        this.mPostItemList = postItemList;
        this.mContext = context;
    }

    /**
     * Creates RecyclerView Holder for item.
     * @param viewGroup {ViewGroup} view group
     * @param position {Integer} position
     * @return {PostHolder} newly created Holder.
     */
    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.posts_item, null);
        PostHolder ph = new PostHolder(v);
        return ph;
    }

    /**
     * Updates view according to current item.
     * @param postHolder {PostHolder} Holder for item
     * @param position {Integer} position
     */
    @Override
    public void onBindViewHolder(PostHolder postHolder, int position) {
        PostItem postItem = mPostItemList.get(position);
        postHolder.name.setText(postItem.getName());
        postHolder.post.setText(postItem.getPost());
        // Set placeholderImage
        postHolder.avatar.setImageResource(R.drawable.placeholder);
        ImageDownloader imageDownloader = new ImageDownloader(postHolder.avatar, mContext,
                postItem.getName());
        // Assign this task for current Holder.
        postHolder.setCurrentTask(imageDownloader);
        // Trigger background task.
        imageDownloader.execute(postItem.getAvatar());
    }

    /**
     * Returns total count of items.
     * @return {Integer} count of items
     */
    @Override
    public int getItemCount() {
        return (null != mPostItemList ? mPostItemList.size() : 0);
    }

}