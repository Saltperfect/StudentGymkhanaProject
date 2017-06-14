package in.ac.iiti.gymakhanaiiti.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.other.Vars;

/**
 * Created by ankit on 11/2/17.
 */

public class VotesTextView extends TextView {
    private int numOfVotes;
    public VotesTextView(Context context) {
        super(context);
    }

    public VotesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VotesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setNumOfVotes(int numOfVotes)
    {
        this.numOfVotes = numOfVotes;
        setText(""+numOfVotes);
    }


    public void setVoteStatus(boolean isUpvoted)
    {

        if(!isUpvoted)
        {
            setTextColor(ContextCompat.getColor(getContext(), R.color.default_text_color));
        }
        else
        {
            setTextColor(ContextCompat.getColor(getContext(),R.color.upvote_color));
        }
    }
}
