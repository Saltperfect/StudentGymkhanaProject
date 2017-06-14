package in.ac.iiti.gymakhanaiiti.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ankit on 11/2/17.
 */

public class TextViewRoundFont extends TextView {
    public TextViewRoundFont(Context context) {
        super(context);
        init();
    }

    public TextViewRoundFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRoundFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init()
    {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"vaground.ttf");
        setTypeface(tf);
    }

}
