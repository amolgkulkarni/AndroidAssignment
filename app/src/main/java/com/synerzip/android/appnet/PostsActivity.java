package com.synerzip.android.appnet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.synerzip.android.appnet.adapter.PostsAdapter;
import com.synerzip.android.appnet.model.PostItem;
import com.synerzip.android.appnet.utils.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for showing list of posts.
 * Gets data from Server. Retrieves required information and displays using RecyclerView
 */
public class PostsActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "AppNetPosts";
    private final String DATA_URL = "https://alpha-api.app.net/stream/0/posts/stream/global";

    private List<PostItem> mPostItemList = new ArrayList<PostItem>();
    private RecyclerView mPostsView;
    private SwipeRefreshLayout mSwipeLayout;

    /**
     * overrides onCreate of Activity.
     * Initializes views and start downloading data.
     * @param savedInstanceState data supplied in onSaveInstanceState or null otherwise.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        /* Initialize recyclerview */
        mPostsView = (RecyclerView) findViewById(R.id.recycler_view);
        mPostsView.setLayoutManager(new LinearLayoutManager(this));
        mPostsView.addItemDecoration(new SpaceItemDecoration(3));

        /* Initialize Swipe Layout */
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setEnabled(true);
        /* Listen for Refresh event */
        mSwipeLayout.setOnRefreshListener(this);
        // TODO: workaround for https://code.google.com/p/android/issues/detail?id=77712
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        /* Set color for ProgressBar */
        // NOTE: setColorSchemeColors expect color integers (e.g Color.BLUE)
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        ImageButton btnAlpha = (ImageButton)findViewById(R.id.postCommand);
        btnAlpha.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
                    arg0.startAnimation(animAlpha);
                }
            }}
        );

        // Connect to onScrollListener of RecyclerView to get scroll position
        mPostsView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                // mSwipeLayout.canChildScrollUp() will always return false as we are using
                // FrameLayout to add Floating button to RecyclerView and Framelayout matches parent height.
                if (0 == mPostsView.getChildPosition(mPostsView.getChildAt(0))) {
                    mSwipeLayout.setEnabled(true);
                } else {
                    mSwipeLayout.setEnabled(false);
                }
            }
        });
        /* Download Data */
        initiateRefresh();
    }

    /**
     * overrides onCreateOptionsMenu for Activity.
     * initializes options menu.
     * @param menu options menu for this activity
     * @return true to display menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Refresh Event Listener for SwipeRefreshLayout.
     */
    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
        // Scroll only if at top.
        if (false == mSwipeLayout.canChildScrollUp()) {
            initiateRefresh();
        }
    }

    /**
     * Starts downloading data in background thread.
     */
    private void initiateRefresh() {
        Log.i(TAG, "initiateRefresh");
        // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
        if (!mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(true);
        }

        // Check for network status
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (true || isConnected) {
            /* Downloading data from public url */
            new AsyncHttpTask().execute(DATA_URL);
        } else {
            mSwipeLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "No network connectivity!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Download data.
     */
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection = null;
            try {
                /* forming java.net.URL object */
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();
                Log.d(TAG, "Status: " + statusCode);
                /* 200 represents Success */
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                }else{
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        /**
         * Request Complete
         * @param result response status
         */
        @Override
        protected void onPostExecute(Integer result) {
            // Stop the refreshing indicator
            mSwipeLayout.setRefreshing(false);
            if (result == 1) {
                /* Success! */
                mPostsView.setAdapter(new PostsAdapter(PostsActivity.this, mPostItemList));
            } else {
                Log.e(TAG, "Failed to fetch data!");
                Toast.makeText(getApplicationContext(), "Network failure!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Parses received response and retrieves required information like username, post
     * and user image url. Response follows following schema
     * result: {
     *   meta: {},
     *   data: [
     *     {
     *       user: {
     *           username: <string>,
     *           avatar_image: {
     *               url: <string>,
     *               width: <int>
     *           }
     *       },
     *       text: <string>
     *     }
     *   ]}
     * @param result received response from server
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("data");
            /*Initialize array if null*/
            if (null == mPostItemList) {
                mPostItemList = new ArrayList<PostItem>();
            }
            for (int i = 0; i < posts.length(); i++) {
                PostItem item = new PostItem();
                // Get current post
                JSONObject post = posts.optJSONObject(i);
                item.setPost(post.optString("text"));

                // Get User object for current post
                JSONObject user = post.optJSONObject("user");
                item.setName(user.optString("username"));

                // Get Avatar object for current user
                JSONObject avatar = user.optJSONObject("avatar_image");
                item.setAvatar(avatar.optString("url"));
                mPostItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
