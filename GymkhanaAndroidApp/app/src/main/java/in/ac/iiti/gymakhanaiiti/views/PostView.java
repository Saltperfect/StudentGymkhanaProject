package in.ac.iiti.gymakhanaiiti.views;

import android.app.Activity;
import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.imageManipulation.CircleTransform;
import in.ac.iiti.gymakhanaiiti.models.Post;
import in.ac.iiti.gymakhanaiiti.other.CustomLinkMethod;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.Vars;

/**
 * Created by ankit on 11/2/17.
 */

public class PostView {
    private Context mContext;
    private Activity mActivity;
    private Post post;
    private View mView;
    private View.OnClickListener onClickListener;
    private String Tag = Vars.globalTag;
    private TextView
            emailTv,
            autherNameTv,
            timeStampTv,
            titleTv,
            bodyTv,
            commentsTv,
            viewsTv;
    private VotesTextView votesTextView;
    private LinearLayout
            commentLayout,
            upvoteLayout,
            autherInfoLayout,
            postMainLayout,
            postMenuButtonLayout;
    private ImageView autherImageView, postPreviewImage;
    private UpvoteImageView upvoteImageView;
    //postmainlayout is linear layout containing complete single post layout which  when clicked whole post will be shown;

    private OnPostViewClickListener listener;
    public PostView(Activity activity,Post post)
    {
        mContext  = activity;
        mActivity = activity;
        this.post = post;

        mView = mActivity.getLayoutInflater().inflate(R.layout.single_post_layout,null,false);

        this.onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onItemClick(v);
            }
        };

        initViews();



    }
    private void initViews()
    {
        emailTv = (TextView)mView.findViewById(R.id.post_auther_email_TV);
        autherNameTv = (TextView) mView.findViewById(R.id.post_auther_name_TV);
        timeStampTv = (TextView) mView.findViewById(R.id.post_time_TV);
        titleTv  = (TextView) mView.findViewById(R.id.post_title_TV);
        bodyTv = (TextView)mView.findViewById(R.id.post_body_TV);
        votesTextView  = (VotesTextView)mView.findViewById(R.id.numVotesTV);
        commentLayout = (LinearLayout)mView.findViewById(R.id.post_commment_layout);
        upvoteLayout = (LinearLayout)mView.findViewById(R.id.upvote_post_layout);
        autherInfoLayout = (LinearLayout)mView.findViewById(R.id.auther_name_layout);
        postMainLayout = (LinearLayout)mView.findViewById(R.id.post_main_layout);
        postMenuButtonLayout = (LinearLayout)mView.findViewById(R.id.post_menu_button_layout);
        postPreviewImage = (ImageView)mView.findViewById(R.id.post_preview_image);
        upvoteImageView = (UpvoteImageView)mView.findViewById(R.id.upvote_button);
        autherImageView  = (ImageView)mView.findViewById(R.id.auther_profilepic);
        commentsTv = (TextView)mView.findViewById(R.id.numCommentsTv);
        viewsTv = (TextView)mView.findViewById(R.id.numViewsTv);


        commentLayout.setOnClickListener(onClickListener);
        upvoteLayout.setOnClickListener(onClickListener);
        autherInfoLayout.setOnClickListener(onClickListener);
        postMainLayout.setOnClickListener(onClickListener);
        postMenuButtonLayout.setOnClickListener(onClickListener);

        upvoteImageView.setUpvoteStatus(post.isUpvoted());
        emailTv.setText(post.getAuthorEmail());
        autherNameTv.setText(post.getAuthorName());
        timeStampTv.setText(post.getTimeStamp());
        votesTextView.setNumOfVotes(post.getNumVotes());
        votesTextView.setVoteStatus(post.isUpvoted());
        commentsTv.setText(""+post.getNumComments());
        viewsTv.setText(""+post.getNumViews());


        //set auther image
        loadAuthorImage();

        //set title if not null;
        if(post.getTitle()!=null&&post.getTitle().length()>0)
        {
            titleTv.setText(post.getTitle());
        }else{
            titleTv.setVisibility(View.GONE);
        }

        if(post.getBody()!=null&&post.getBody().length()>0)
        {
            bodyTv.setText(Methods.stripUnderlines(post.getBody()));
            //bodyTv.setMovementMethod(new CustomLinkMethod()); //TODO set it when add link method is added;

        }else{
            bodyTv.setVisibility(View.GONE);
        }

        if(post.getImageUrls()!=null&&post.getImageUrls().size()>0)
        {
            String frstImageUrl = post.getImageUrls().get(0);

            //now loading first image in previewimageView

            loadFirstImageInPostPreviewImageView(frstImageUrl);

        }else{
            //if no image then hiding preview image view
            postPreviewImage.setVisibility(View.GONE);

            //if no image then increase the size of the body content
            titleTv.setMaxLines(4);
            bodyTv.setMaxLines(8);
        }


    }


    private void loadAuthorImage()
    {
        String profileUrl = post.getAuthorProfileUrl();
       //Log.d(Tag,"author profile url "+profileUrl);


        Glide.with(mContext).load(profileUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CircleTransform(mContext))
                .into(autherImageView);

    }

    private void loadFirstImageInPostPreviewImageView(String url)
    {
        Glide.with(mContext).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (e != null)
                            Log.d(Tag, e.toString());
                        postPreviewImage.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(postPreviewImage);
    }




    private void onItemClick(View v)
    {
        switch (v.getId())
        {
            case R.id.post_commment_layout:
                listener.onCommentLayoutClick(post);
                break;
            case R.id.upvote_post_layout:
                post.setUpvoteStatus(!post.isUpvoted()); //changing post upvote status basically post upvote is tooggled on click so
                votesTextView.setVoteStatus(post.isUpvoted()); //post upvote status already changed in above line
                votesTextView.setNumOfVotes(post.getNumVotes());
                upvoteImageView.setUpvoteStatus(post.isUpvoted());
                listener.onUpvoteClick(post);
                break;
            case R.id.auther_name_layout:
                listener.onAutherNameClick(post);
                break;
            case R.id.post_main_layout:
                listener.onLayoutClick(post);
                break;
            case R.id.post_menu_button_layout:
                popUpMenu();
                break;
        }
    }




    private void popUpMenu()
    {
        PopupMenu popup = new PopupMenu(mActivity,postMenuButtonLayout);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                listener.onPostMenuClick(post,item.getTitle().toString());
                Toast.makeText(mContext,item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, (MenuBuilder) popup.getMenu(), postMenuButtonLayout);
        menuHelper.show();
    }


    public View getView()
    {
        return mView;
    }



    public interface  OnPostViewClickListener{
        public abstract void onUpvoteClick(Post post);
        public abstract void onCommentLayoutClick(Post post);
        public abstract void onPostMenuClick(Post post,String action);
        public abstract void onLayoutClick(Post post);
        public abstract void onAutherNameClick(Post post);
    }




    public void setOnPostViewClickListener(OnPostViewClickListener onPostViewClickListener)
    {
        this.listener = onPostViewClickListener;
    }


}
