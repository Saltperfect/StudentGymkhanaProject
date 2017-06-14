package in.ac.iiti.gymakhanaiiti.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import in.ac.iiti.gymakhanaiiti.R;

/**
 * Created by ankit on 9/2/17.
 */

public class DownvoteImageView extends ImageView {
    private boolean isDownvoted = false;
    public DownvoteImageView(Context context) {
        super(context);
    }

    public DownvoteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownvoteImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDownvoteStatus(boolean isDownvoted)
    {
       this.isDownvoted = isDownvoted;
      if(isDownvoted)
      {
          setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.discussion_downvoted_icon));
      }else {
          setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.discussion_downvote_icon));
      }
    }
    public boolean isDownvoted() { return isDownvoted;}
}
