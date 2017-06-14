package in.ac.iiti.gymakhanaiiti.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.activity.MessMenuActivity;
import in.ac.iiti.gymakhanaiiti.other.SharedPref;
import in.ac.iiti.gymakhanaiiti.other.Vars;

import static in.ac.iiti.gymakhanaiiti.other.Vars.globalTag;

public class MessMenuAlarmReceiver extends BroadcastReceiver {

    final String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private final String messMenuSharedPrefKey = "messMenuJson";

    public MessMenuAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context , Intent intent) {
        showNotification(context);
        Log.d(globalTag,"In Broadcast Receiver");
    }
    private void showNotification(Context ctx) {
        RemoteViews remoteViews = new RemoteViews(ctx.getPackageName(),R.layout.custom_mess_notification);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(ctx);
            noti.setContent(remoteViews)
                .setSmallIcon(R.drawable.gymkhana_logo)
                .setAutoCancel(true)
                .build();
        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(ctx, MessMenuActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, 0,resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        noti.setContentIntent(pIntent);

        String menu = getMenu(ctx);
        if(menu!=null)
        {
            remoteViews.setTextViewText(R.id.menuTV,menu);
            mNotificationManager.notify(5000, noti.build());
        }
    }

    private String getMenu(Context context)
    {
        String messMenuJsonString = SharedPref.getString(context,messMenuSharedPrefKey);
        if(messMenuJsonString==null)
        {
            //if not updated than load from march;
            messMenuJsonString = loadJSONFromAsset(context,"messMenu.json");
        }
        try {
            JSONObject messMenu = new JSONObject(messMenuJsonString);

            for (int i = 0; i < 8; i++) {
                String day = days[i];

                JSONObject daysMenu = messMenu.getJSONObject(day);
                String breakfast, lunch, hightea, dinner;
                breakfast = daysMenu.getString("Breakfast");
                lunch = daysMenu.getString("Lunch");
                hightea = daysMenu.getString("High Tea");
                dinner = daysMenu.getString("Dinner");
                String menu = "";
                if (getToday().equals(day)) {
                    if (hourOfday() >=7 && hourOfday()< 11) {
                        menu = "Breakfast : "+breakfast;
                    }else if(hourOfday()>=11&&hourOfday()<16)
                    {
                         menu = "Lunch : " + lunch;

                    }else if(hourOfday()>=16&&hourOfday()<19){
                        menu = "High Tea : "+hightea;
                    }else{
                        menu = "Dinner : "+dinner;
                    }

                    return menu;
                }
            }
        }catch (JSONException e)
        {
            Log.d(globalTag,e.getMessage());
            return null;
        }
      return null;
    }

    private int hourOfday()
    {
        Calendar c = Calendar.getInstance();
        return  c.get(Calendar.HOUR_OF_DAY);
    }

    private String getToday()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return days[day-1];
        //starts from sunday as 1 to saturday as 7;
    }
    private String loadJSONFromAsset(Context context,String fileName) {
        String json = null;
        try {

            InputStream is =  context.getAssets().open(fileName);//filename should contain json;

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
