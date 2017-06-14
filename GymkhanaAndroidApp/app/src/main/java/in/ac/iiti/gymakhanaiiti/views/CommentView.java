package in.ac.iiti.gymakhanaiiti.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.models.Comment;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.Vars;

/**
 * Created by ankit on 18/2/17.
 */

public class CommentView {


    private String TAG = Vars.globalTag;

    private Activity mActivity;
    private Context context;
    private Comment comment;
    private View mView;
    private TextView authorNameTv;
    private TextView commentTimeTv;
    private TextView commentContentTv;
    private TextView numVotesTv;
    private UpvoteImageView upvoteImageView;
    private LinearLayout upvoteLayout;
    private LinearLayout replyLayout;
    private LinearLayout authorInfoLayout;
    private LinearLayout fullLayout; //complete comment layout to handle long click to show menu for the comment

    private View.OnClickListener onClickListener;
    private OnCommentItemClickListener listener;

    public CommentView(Activity  activity, Comment comment)
    {
        mActivity = activity;
        context  = activity;
        this.comment = comment;
        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v);
            }
        };
        mView  = mActivity.getLayoutInflater().inflate(R.layout.single_comment_layout,null,false);
        initViews();
    }


    public interface OnCommentItemClickListener{
        public abstract  void onUpvoteClick(Comment comment);
        public abstract  void onAuthorInfoClick(Comment comment);
        public abstract void  onReplyLayoutClick(Comment comment);
        public abstract void onPopUpItemClick(Comment comment, String action);
    }

    public void setOnCommentItemClickListener(OnCommentItemClickListener onCommentItemClickListener)
    {
        this.listener = onCommentItemClickListener;
    }

    public View getView()
    {
        return mView;
    }


    private void initViews()
    {
        authorNameTv = (TextView)mView.findViewById(R.id.comment_author_name_TV);
        commentTimeTv  = (TextView)mView.findViewById(R.id.comment_time_TV);
        commentContentTv = (TextView)mView.findViewById(R.id.comment_content_TV);
        numVotesTv = (TextView)mView.findViewById(R.id.numVotesTV);
        upvoteLayout = (LinearLayout)mView.findViewById(R.id.upvote_comment_layout);
        upvoteImageView = (UpvoteImageView)mView.findViewById(R.id.upvote_button);
        replyLayout = (LinearLayout) mView.findViewById(R.id.comment_reply_layout);
        authorInfoLayout = (LinearLayout)mView.findViewById(R.id.comment_author_layout);
        fullLayout = (LinearLayout)mView.findViewById(R.id.full_comment_layout);

        upvoteLayout.setOnClickListener(onClickListener);
        replyLayout.setOnClickListener(onClickListener);
        authorInfoLayout.setOnClickListener(onClickListener);
        //show pop up menu containing report, delete and edit option on long click of comment layout;
        fullLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popupmenu();
                return true;
            }
        });


        //setting views values
        authorNameTv.setText(comment.getAuthorName());
        commentTimeTv.setText(comment.getCommentTime());
        commentContentTv.setText(Methods.fromHtml(comment.getCommentContent()));
        numVotesTv.setText(""+comment.getNumUpvotes());
        upvoteImageView.setUpvoteStatus(comment.isUpvoted());
    }

    private void onItemClick(View v)
    {
        switch (v.getId())
        {
            case R.id.upvote_comment_layout:
                listener.onUpvoteClick(comment);
                break;
            case R.id.comment_reply_layout:
                listener.onReplyLayoutClick(comment);
                break;
            case R.id.comment_author_layout:
                listener.onAuthorInfoClick(comment);
                break;
        }
    }

    private void popupmenu()
    {
        //TODO complete;
    }
}
