package in.ac.iiti.gymakhanaiiti.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.User.AuthenticationHeader;
import in.ac.iiti.gymakhanaiiti.imageManipulation.CircleTransform;
import in.ac.iiti.gymakhanaiiti.models.Comment;
import in.ac.iiti.gymakhanaiiti.models.Post;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.Vars;
import in.ac.iiti.gymakhanaiiti.views.CommentView;
import in.ac.iiti.gymakhanaiiti.views.UpvoteImageView;
import in.ac.iiti.gymakhanaiiti.views.VotesTextView;

public class SinglePostActivity extends AppCompatActivity {

    private String TAG = Vars.globalTag;
    private Post post;

    private TextView
            emailTv,
            autherNameTv,
            timeStampTv,
            titleTv,
            bodyTv,
            numcommentsTv,
            numviewsTv;
    private VotesTextView votesTextView;
    private LinearLayout
            commentLayout,
            upvoteLayout,
            autherInfoLayout,
            postMainLayout,
            postMenuButtonLayout;
    private ImageView autherImageView;
    private UpvoteImageView upvoteImageView;
    private LinearLayout imagesContainerLayout;
    private ImageButton reloadImageButton;
    private LinearLayout commentContainerLayout;

    private ArrayList<Integer> commentIds = new ArrayList<Integer>();
    private int currentDownloadedCommentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        post = Post.getCurrentPost();

        if(post==null)
        {
            Log.d(TAG,"Post is null in Single Post Activity , FINISH()");
            finish();
        }
        initViews();


        loadComments();

    }


    private void loadComments()
    {
      /*  loadComments --> getCommentsIds brings ids of the comments, downloadComment(id) will
       *  download comment with id, commentDonwloaded will handle the response and make Comment object
       *  and if comment object is made successfully it will call loadCommentInContainer(comment);
       */

        commentIds = new ArrayList<Integer>();

        getCommentIds();

    }

    private void  getCommentIds()
    {
        currentDownloadedCommentIndex = 0;
        int postId = post.getPostId();
        String url = Vars.GetCommmentIdsUrl+ postId;
        StringRequest getCommentIdsRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"comment ids response :"+response);
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isError = jsonResponse.getBoolean(Vars.KeyError);
                    if(isError)
                    {
                        //TODO handle this and inform user;
                        Log.d(TAG,"error while getting comments");
                    }else{
                        //finally load ids in array;
                        JSONArray ids = jsonResponse.getJSONArray(Vars.KeyCommentIds);
                        for(int i = 0 ; i < ids.length() ; i++)
                        {
                            commentIds.add((int)ids.get(i));
                        }
                        //now we will download comments one by one and load in container here we will start with first comment;
                        if(commentIds.size()>0)
                        downloadComment(commentIds.get(0));

                    }
                }catch (JSONException e)
                {
                    Log.d(TAG,"error while getting json commentids response :"+Log.getStackTraceString(e));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"error while getting comment ids "+error.networkResponse.statusCode);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }

        };
        AppController.getInstance().addToRequestQueue(getCommentIdsRequest);
    }

    private void downloadComment(final int commentId)
    {
        String url = Vars.GetCommentUrl+commentId;

        StringRequest getCommentIdsRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"comment ids response :"+response);
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isError = jsonResponse.getBoolean(Vars.KeyError);
                    if(isError)
                    {
                        //TODO handle this and inform user;
                        Log.d(TAG,"error while getting comment with id "+commentId);
                    }else{
                        commentDownloaded(jsonResponse);
                        //download next comment
                        currentDownloadedCommentIndex++;
                        if(currentDownloadedCommentIndex<commentIds.size())
                        downloadComment(commentIds.get(currentDownloadedCommentIndex));
                        else return;
                    }
                }catch (JSONException e)
                {
                    Log.d(TAG,"error while getting json commentids response :"+Log.getStackTraceString(e));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"error while getting comment ids "+error.networkResponse.statusCode);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }

        };
        AppController.getInstance().addToRequestQueue(getCommentIdsRequest);
    }

    //make comment object and load it into container;
    private void commentDownloaded(JSONObject response)
    {
        try{
            int commentId = response.getInt(Vars.KeyCommentId);
            int authorId = response.getInt(Vars.KeyAuthorId);
            String authorName = response.getString(Vars.KeyAuthorName);
            String timeStamp = response.getString(Vars.KeyTimeStamp);
            String commentContent = response.getString(Vars.KeyCommentContent);
            int currentUserVoteValue = response.getInt(Vars.KeyCurrentUserVoteValue);
            int numofvote = response.getInt(Vars.KeyNumVotes);
            Comment comment = new Comment(authorId,commentId,authorName,numofvote,timeStamp,commentContent,currentUserVoteValue);
            loadCommentInContainer(comment);
        }catch (JSONException e)
        {
            Log.d(TAG,"error while making comment object in commentDownloaded method"+Log.getStackTraceString(e));
        }
    }
    // load donwloaded comment in comment container;
    private void loadCommentInContainer(Comment comment)
    {
        CommentView commentView = new CommentView(this,comment);
        commentView.setOnCommentItemClickListener(new CommentView.OnCommentItemClickListener() {
            @Override
            public void onUpvoteClick(Comment comment) {
                //set comment vote on server;
                setCommentVoteStatusOnTheServer(comment);
            }

            @Override
            public void onAuthorInfoClick(Comment comment) {
                showAuthorInfo(comment.getAuthorId());
            }

            @Override
            public void onReplyLayoutClick(Comment comment) {
                Intent intent = new Intent(SinglePostActivity.this,CommentActivity.class);
                intent.putExtra("todo","replyOnComment");
                Comment.setCurrentComment(comment); //to access content in CommentActivity class
                startActivity(intent);
            }

            @Override
            public void onPopUpItemClick(Comment comment, String action) {

            }
        });
        commentContainerLayout.addView(commentView.getView());
    }

    //setting comment vote when user clicked on upvote layout of the
    private void setCommentVoteStatusOnTheServer(final Comment comment)
    {
        int userVoteValue = comment.getCurrentUserVoteValue();
        String url  = Vars.AddCommentVote + comment.getCommentId();
        StringRequest addCommentVoteRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"commented on vote :"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"error while voting on comment volley :"+error.networkResponse.data);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String ,String> params = new HashMap<String,String>();
                params.put(Vars.KeyCurrentUserVoteValue,""+comment.getCurrentUserVoteValue());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(addCommentVoteRequest);
    }


    private void initViews()
    {
        emailTv = (TextView)findViewById(R.id.post_auther_email_TV);
        autherNameTv = (TextView) findViewById(R.id.post_auther_name_TV);
        timeStampTv = (TextView) findViewById(R.id.post_time_TV);
        titleTv  = (TextView) findViewById(R.id.post_title_TV);
        bodyTv = (TextView)findViewById(R.id.post_body_TV);
        votesTextView  = (VotesTextView)findViewById(R.id.numVotesTV);
        commentLayout = (LinearLayout)findViewById(R.id.post_commment_layout);
        upvoteLayout = (LinearLayout)findViewById(R.id.upvote_post_layout);
        autherInfoLayout = (LinearLayout)findViewById(R.id.auther_name_layout);
        postMainLayout = (LinearLayout)findViewById(R.id.post_main_layout);
        postMenuButtonLayout = (LinearLayout)findViewById(R.id.post_menu_button_layout);
        upvoteImageView = (UpvoteImageView)findViewById(R.id.upvote_button);
        autherImageView  = (ImageView)findViewById(R.id.auther_profilepic);
        numcommentsTv = (TextView)findViewById(R.id.numCommentsTv);
        numviewsTv = (TextView)findViewById(R.id.numViewsTv);
        imagesContainerLayout = (LinearLayout)findViewById(R.id.images_container);
        commentContainerLayout = (LinearLayout)findViewById(R.id.comments_container);
        reloadImageButton = (ImageButton)findViewById(R.id.reload_post_button);

        upvoteImageView.setUpvoteStatus(post.isUpvoted());
        emailTv.setText(post.getAuthorEmail());
        autherNameTv.setText(post.getAuthorName());
        timeStampTv.setText(post.getTimeStamp());
        votesTextView.setNumOfVotes(post.getNumVotes());
        votesTextView.setVoteStatus(post.isUpvoted());
        numcommentsTv.setText(""+post.getNumComments());
        numviewsTv.setText(""+post.getNumViews());

        if(post.getTitle()!=null&&post.getTitle().length()>0)
        {
            titleTv.setText(post.getTitle());
        }else{
            titleTv.setVisibility(View.GONE);
        }

        if(post.getBody()!=null&&post.getBody().length()>0)
        {
            bodyTv.setText(Methods.stripUnderlines(post.getBody()));
            // bodyTv.setMovementMethod(CustomLinkMethod.getInstance()); //TODO set it when you have implemente add link method correctly;
        }else{
            bodyTv.setVisibility(View.GONE);
        }

        //Load all the images one by one;
        if(post.getImageUrls()!=null&&post.getImageUrls().size()>0) {
            ArrayList<String> imageUrls = post.getImageUrls();

            //loading all images related to the post in image container using glide library;
            for(int i = 0 ; i< imageUrls.size(); i++)
            {
                String url = imageUrls.get(i);
                if(!url.contains("http://")&&!url.contains("www"))
                {
                    //if this then image is on the server itself in studentApp folder;
                    url = Vars.HOST + url;  //url initially was like /studentApp/data/images/ so adding host;
                }
                loadImageInImageContainer(url);
            }

        }

        setAuthorImageView();

    }

    private void loadImageInImageContainer(final String url)
    {
        //dynamically adding postCompleteImageView in the container
        View view = this.getLayoutInflater().inflate(R.layout.post_preview_image_view,null,false);
        ImageView imageView =(ImageView) view.findViewById(R.id.image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullImageIntent = new Intent(SinglePostActivity.this,ImageZoomInZoomOutActivity.class);
                fullImageIntent.putExtra("imageUrl",url);
                startActivity(fullImageIntent);
            }
        });
        imagesContainerLayout.addView(view);

        Glide.with(this).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (e != null)
                            Log.d(TAG, "In single post activity glide "+e.toString());
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                        Log.d(TAG,"downloaded image");
                        return false;
                    }
                }).into(imageView);
    }


    //when something is clicked in the layout
    public void onClickItem(View v)
    {
        Log.d(TAG,"on click item called");
        switch (v.getId())
        {
            case R.id.upvote_post_layout:
                post.setUpvoteStatus(!post.isUpvoted()); //changing post upvote status basically post upvote is tooggled on click so
                votesTextView.setVoteStatus(post.isUpvoted()); //post upvote status already changed in above line
                votesTextView.setNumOfVotes(post.getNumVotes());
                upvoteImageView.setUpvoteStatus(post.isUpvoted());
                setUserVoteOnServer(post.getClientVoteValue());
                break;
            case R.id.post_commment_layout:
                //TODO implement this as comment to post
                break;
            case R.id.auther_name_layout:
                showAuthorInfo(post.getAuthorId());
                break;
            case R.id.post_menu_button_layout:
                popUpMenu();
                break;
            case R.id.return_back_button:
                finish();
                break;
            case R.id.discussion_post_reply_fab:
                //setting Post.current post so that we can access from comment activity otherwise we can not pas Post object directly;
                Post.setCurrentPost(post);
                Intent intent = new Intent(this,CommentActivity.class);
                intent.putExtra("todo","commentOnPost"); //  or "replyOnComment"
                Post.setCurrentPost(post);
                startActivity(intent);
                break;
            case R.id.reload_post_button:
                reloadPost();
                break;
        }
    }
    private void reloadPost()
    {
        animateReloadButton(true);
    }

    private void setUserVoteOnServer(final int clientVoteValue)
    {
        int post_id = post.getPostId();
        String url = Vars.AddVotePostUrl + post_id;
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


    private void showAuthorInfo(int authorId)
    {

    }


    private void setAuthorImageView()
    {
        String profileUrl = post.getAuthorProfileUrl();
        //Log.d(Tag,"author profile url "+profileUrl);
        Glide.with(this).load(profileUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CircleTransform(this))
                .into(autherImageView);
    }


    private void animateReloadButton(boolean shouldAnimate)
    {
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.rotate);

        if(shouldAnimate)
        {
            reloadImageButton.startAnimation(rotateAnimation);
        }
        else{
            reloadImageButton.clearAnimation();
        }
    }



    private void popUpMenu()
    {
        PopupMenu popup = new PopupMenu(this,postMenuButtonLayout);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
