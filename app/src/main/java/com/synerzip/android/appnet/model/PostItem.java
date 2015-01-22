package com.synerzip.android.appnet.model;

/**
 * Model for item to displayed in RecyclerView.
 */
public class PostItem {
    private String mName;
    private String mPost;
    private String mAvatar;

    /**
     * Returns user name for current user.
     * @return {String} user name for current post.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets user name.
     * @param name {String} user name for current post
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Returns message;
     * @return {String} message for current post
     */
    public String getPost() {
        return mPost;
    }

    /**
     * Sets message.
     * @param post {String} message for current post
     */
    public void setPost(String post) {
        this.mPost = post;
    }

    /**
     * Returns url for user image
     * @return {String} image url
     */
    public String getAvatar() {
        return mAvatar;
    }

    /**
     * Sets image url for user.
     * @param avatar {String} image url
     */
    public void setAvatar(String avatar) {
        this.mAvatar = avatar;
    }
}