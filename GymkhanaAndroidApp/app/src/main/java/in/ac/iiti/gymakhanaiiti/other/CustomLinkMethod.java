package in.ac.iiti.gymakhanaiiti.other;

import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import in.ac.iiti.gymakhanaiiti.activity.MainActivity;

/**
 * Created by ankit on 17/2/17.
 */

public class CustomLinkMethod extends LinkMovementMethod {
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event ) {
        try {
            //when anchor tag contain href without http android system crashes so cheking buffer and adding http;
            //Log.d(Vars.globalTag,"Link clicked "+ Methods.toHtml(buffer));
            String html = Methods.toHtml(buffer);
            if(!html.contains("http://www.")||!html.contains("https://www.")||!html.contains("href"))
            {
                return true; //we have handled it if http is not present TODO make it better by adding http in front of such strings;
            }
            return super.onTouchEvent( widget, buffer, event );
        } catch( Exception ex ) {
            Log.d(Vars.globalTag,"error while clicking link "+Log.getStackTraceString(ex));
            return true;
        }
    }

    public CustomLinkMethod() {
        super();
    }

}
