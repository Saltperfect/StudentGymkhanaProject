package in.ac.iiti.gymakhanaiiti.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.User.AuthenticationHeader;
import in.ac.iiti.gymakhanaiiti.activity.SinglePostActivity;
import in.ac.iiti.gymakhanaiiti.models.Post;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.Vars;
import in.ac.iiti.gymakhanaiiti.views.PostView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleTopicDiscussionFragment extends Fragment  {

    private String TAG  = Vars.globalTag;

    private Context mContext;
    private String topic = Vars.TopicGeneralDiscussion; //initialising in case of null pointer somewhere;
    String requestTag = "get_post_request";

    private View rootView;
    private LinearLayout post_container;
    private SwipeRefreshLayout swipeRefreshLayout;


    private ArrayList<Integer> postIds = new ArrayList<Integer>(); //postids for the current topic so that posts will be downloaded one by one;
    private int currentPostIndex = 0;
    public SingleTopicDiscussionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity(); //don't initialize  it in constructor as at that time fragment is not loaded so getActivity will return null;

        topic = getArguments().getString("topic");

        rootView = inflater.inflate(R.layout.fragment_single_topic, container, false);

        post_container = (LinearLayout) rootView.findViewById(R.id.posts_container);

        initSwipeRefreshLayout();

        loadPosts();

        return rootView;
    }

    private void loadPosts()
    {
        getAllPostIds(topic);

    }


    private ArrayList<Integer> getAllPostIds(String topic) {

        postIds = new ArrayList<Integer>(); //refreshing list;

        final String url = Vars.GetPostIdsUrl+ topic.replaceAll(" ","_");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response.toString());
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray array = jsonResponse.getJSONArray(Vars.KeyPostids);
                    if(array.length()>0) {
                        for (int i = 0; i < array.length(); i++) {
                            postIds.add((int) array.get(i));
                        }

                        currentPostIndex = 0; //we will download posts one by one; from 0th id to size()-1 th id;
                        if(postIds.size()>0)
                        downloadPost(postIds.get(currentPostIndex)); //here begin our journey of downloading posts from first post that is latest one

                    }
                }catch (JSONException e)
                {
                    Log.d(TAG,Log.getStackTraceString(e));
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.getMessage() + " "+url);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,requestTag);
        return postIds;
    }






    private void downloadPost(int id){

      //  Log.d(TAG,"downloading post with id "+id);

        final  String url = Vars.GetPostUrl+id; //url for the particular post with id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                       // Log.d(TAG,response.toString());

                        try {
                            boolean iserror = response.getBoolean(Vars.KeyError);
                            if(!iserror)
                            {
                                postDownloaded(response);
                            }

                        } catch (JSONException e) {
                            Log.d(TAG,"discussion fragment "+Log.getStackTraceString(e));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"could not get the post "+error.networkResponse.toString()+error.getMessage()+" "+url);

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }
        };

        //add request to the request queue; if you want to set priority set priority;
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,requestTag);

    }







    //getting data from the downloaded response and creating a Post Object;
    private void postDownloaded(JSONObject response)
    {
        //downloading next post;
        currentPostIndex++;
        if(currentPostIndex<postIds.size())// otherwise infinite loop and outofbounds exception;
        {
            downloadPost(postIds.get(currentPostIndex));
        }

        Post post = null;

        try {
            int authorId = response.getInt(Vars.KeyAuthorId);
            String authorName = response.getString(Vars.KeyAuthorName);
            String authorEmail = response.getString(Vars.KeyAuthorMail);
            int numVotes = response.getInt(Vars.KeyNumVotes);
            int currentUserVote = response.getInt(Vars.KeyCurrentUserVoteValue);
            int post_id = response.getInt(Vars.KeyPostId);
            int numComments = response.getInt(Vars.KeyNumComments);
            int numViews = response.getInt(Vars.KeyNumViews);
            String time_stamp = response.getString(Vars.KeyTimeStamp);
            String authorProfileUrl = response.getString(Vars.KeyAuthorProfileUrl);

            /*
            *  content is stored as json string so we will convert it back to json to get data from it.
            */

            String contentstring = response.getString(Vars.KeyPostContent);
            JSONObject content = new JSONObject(contentstring);

            String title = content.getString(Vars.KeyTitleVar);
            String body = content.getString(Vars.KeyBodyVar);

            JSONArray images = content.getJSONArray(Vars.KeyImagesVar);

            //converting jsonArray in arraylist;
            ArrayList<String> imageurls = new ArrayList<String>();

            if (images != null) {
                for (int i=0;i<images.length();i++){
                    String url = images.get(i).toString();
                    if(!url.contains("https://")&&!url.contains("http"))
                    {
                        //image url will be probably like /StudentApp/data/images/5151515.jpeg so adding host address;
                        url = Vars.HOST+ url;
                    }
                    imageurls.add(url);
                }
            }

            post = new Post(post_id,title,body,numVotes,currentUserVote,numComments,numViews,authorId,authorName,authorEmail,authorProfileUrl,time_stamp,imageurls);

           // Log.d(TAG,title + body+ imageurls.toString());

        }catch (JSONException e)
        {
            Log.d(TAG,"Error in  topic fragment in postDonwloaded "+Log.getStackTraceString(e));
        }
        try{
            if(post!=null) {

                //Finally LOADING THE POST IN CONTAINER;
                loadPostInContainer(post);
            }
        }catch (Exception e)
        {
            Log.d(TAG,"Error while loding post in container "+Log.getStackTraceString(e));
        }
    }





    //loading the post in container , finally it will be shown to user;
    private void loadPostInContainer(Post post)
    {
        PostView postView = new PostView(this.getActivity(),post);
        postView.setOnPostViewClickListener(new PostView.OnPostViewClickListener() {
            @Override
            public void onUpvoteClick(Post post) {
               setUserVoteOnServer(post.getPostId(),post.getClientVoteValue());
            }

            @Override
            public void onCommentLayoutClick(Post post) {
                 /*
                    We can not pass Post object directly to our activity so we will set currentPost as static object of Post class
                    and will access it from singlePostActivity
                    if static currentPost is null we will simply close our single post activity
                 */
                Post.setCurrentPost(post);
                Intent singlePostActivityIntent = new Intent(getContext(), SinglePostActivity.class);
                startActivity(singlePostActivityIntent);
            }

            @Override
            public void onPostMenuClick(Post post, String action) {

            }

            @Override
            public void onLayoutClick(Post post){
                /*
                    We can not pass Post object directly to our activity so we will set currentPost as static object of Post class
                    and will access it from singlePostActivity
                    if static currentPost is null we will simply close our single post activity
                 */
                Post.setCurrentPost(post);
                Intent singlePostActivityIntent = new Intent(getContext(), SinglePostActivity.class);
                startActivity(singlePostActivityIntent);

            }

            @Override
            public void onAutherNameClick(Post post) {
               showAuthorInfo();
            }
        });
        post_container.addView(postView.getView());
    }



    private void showAuthorInfo()
    {

    }



    private void setUserVoteOnServer(int postId, final int clientVoteValue)
    {
        String url = Vars.AddVotePostUrl + postId;
        StringRequest setVoteRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"upvote request response :"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"error while uploading vote status "+error.networkResponse.statusCode);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String,String>();
                params.put(Vars.KeyCurrentUserVoteValue,""+clientVoteValue);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(setVoteRequest);
    }



    private void initSwipeRefreshLayout()
    {
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setColorSchemeColors(Color.BLACK,Color.RED,Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO enable refresh layout only when scroll view is all up; and also disable it when a post is loaded;
                //swipeRefreshLayout.setEnabled(false);
                //swipeRefreshlayout.setRefreshing(false); stop animation
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        //cancelling all pending requests otherwise app will crash when response listener is invoked after fragment is stopped;
        AppController.getInstance().getRequestQueue().cancelAll(requestTag);
        Log.d(TAG,"on stop called in discussion fragment");
    }
}
