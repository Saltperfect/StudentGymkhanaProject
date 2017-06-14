package in.ac.iiti.gymakhanaiiti.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import in.ac.iiti.gymakhanaiiti.R;

/**
 * Created by ankit on 9/2/17.
 */

public class UpvoteImageView extends ImageView {

    private boolean isUpvoted = false;
    public UpvoteImageView(Context context) {
        super(context);
    }

    public UpvoteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpvoteImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setUpvoteStatus(boolean isUpvoted)
    {
        this.isUpvoted = isUpvoted;
        if(isUpvoted)
        {
            setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.discussion_upvoted_icon));
        }else{
            setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.discussion_upvote_icon));
        }
    }
    public boolean isUpvoted() { return isUpvoted; }
}
