package in.ac.iiti.gymakhanaiiti.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.BroadcastReceivers.MessMenuAlarmReceiver;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.other.SharedPref;
import in.ac.iiti.gymakhanaiiti.other.Vars;
import in.ac.iiti.gymakhanaiiti.views.TextViewRoundFont;

public class MessMenuActivity extends AppCompatActivity {

   private final String globalTag = Vars.globalTag;


   final String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Compulsory Items"};

   private final String isNotificationEnabled = "isNotificationEnabled";
   private final String messMenuSharedPrefKey = "messMenuJson";
    String  messMenuJsonString;

   TextViewRoundFont titleTv;

   private ProgressDialog progressDialog;
   private LinearLayout container;
   private ImageButton messMenuPopUpButton,returnBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_menu);
        container = (LinearLayout)findViewById(R.id.mess_menu_container);
        titleTv = (TextViewRoundFont)findViewById(R.id.title_TV);
        messMenuPopUpButton = (ImageButton)findViewById(R.id.mess_menu_pop_up_menu_button);
        progressDialog = new ProgressDialog(this);

        if(SharedPref.getBoolean(this,"messMenuFirstTime",true))
        {
            SharedPref.saveBoolean(this,"messMenuFirstTime",false);
            setNotificationAlarm();
        }

        loadMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void loadMenu()
    {
         messMenuJsonString = SharedPref.getString(this,messMenuSharedPrefKey);
        if(messMenuJsonString==null)
        {
            //if not updated than load from march;
            messMenuJsonString = loadJSONFromAsset("messMenu.json");
        }
        else{
            Log.d(globalTag,"Mess menu json string is not null");
        }
        try{
            JSONObject messMenu  = new JSONObject(messMenuJsonString);
            Log.d(globalTag,messMenu.toString());
            String month = messMenu.getString("month");
            titleTv.setText("Mess Menu ("+month+")");
            container.removeAllViews();
            for(int i = 0 ; i<8; i++)
            {
                String day = days[i];

                JSONObject daysMenu = messMenu.getJSONObject(day);

                View view = getLayoutInflater().inflate(R.layout.single_day_mess_menu_layout,null,false);

                TextView dayTV,breakfastTV,lunchTV,highteaTV,dinnerTV;

                dayTV = (TextView)view.findViewById(R.id.dayTv);
                breakfastTV = (TextView)view.findViewById(R.id.breakfastTv);
                lunchTV = (TextView)view.findViewById(R.id.lunchTv);
                highteaTV = (TextView)view.findViewById(R.id.highTeaTv);
                dinnerTV = (TextView) view.findViewById(R.id.dinnerTv);

                String breakfast,lunch, hightea, dinner;

                breakfast = daysMenu.getString("Breakfast");
                lunch = daysMenu.getString("Lunch");
                hightea = daysMenu.getString("High Tea");
                dinner = daysMenu.getString("Dinner");

                View dayIndicator = view.findViewById(R.id.dayIndicator);

                if(getToday().equals(day)){
                    dayIndicator.setVisibility(View.VISIBLE);
                    showCurrentMenu(breakfast,lunch,hightea,dinner);
                }

                dayTV.setText(day);
                breakfastTV.setText(breakfast);
                lunchTV.setText(lunch);
                highteaTV.setText(hightea);
                dinnerTV.setText(dinner);

                //loading view in container
                container.addView(view);
            }

        }catch (JSONException e)
        {
            Toast.makeText(this,"Failed to fetch menu",Toast.LENGTH_SHORT).show();
            Log.d(globalTag,e.toString());
        }

    }

    private void showCurrentMenu(String breakfast,String lunch, String hightea, String dinner){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.current_mess_menu_dialog);
        TextView messSessionTv = (TextView)dialog.findViewById(R.id.messSessionTv);
        TextView currentMenuTv = (TextView)dialog.findViewById(R.id.currentMenuTv);
        CheckBox notificationEnabledCheckbox = (CheckBox)dialog.findViewById(R.id.menuNotificationCheckBox);

        if(SharedPref.getBoolean(this,isNotificationEnabled,true)){
            notificationEnabledCheckbox.setChecked(true);
        }

        int dayhour = getCurrentHour();
        String session = "Today in ";
        if(dayhour>=0&&dayhour<10)
        {
            session +="Breakfast";
           currentMenuTv.setText(breakfast);
        }else if(dayhour>=10&&dayhour<14)
        {
            session +="Lunch";
            currentMenuTv.setText(lunch);
        }else if(dayhour>=14&&dayhour<19)
        {
            session += "High Tea";
            currentMenuTv.setText(hightea);
        }else
        {
            session+="Dinner";
            currentMenuTv.setText(dinner);
        }

        messSessionTv.setText(session);

        notificationEnabledCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                     SharedPref.saveBoolean(MessMenuActivity.this,isNotificationEnabled,isChecked);
                     if(!isChecked)
                     {
                         cancelNotificationAlarm();
                     }else{
                         setNotificationAlarm();
                     }

            }
        });

        dialog.show();
    }

    private void setNotificationAlarm()
    {
        Log.d(globalTag,"Setting Notification Alarm");
        Intent myIntent = new Intent(this, MessMenuAlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        pendingIntent = PendingIntent.getBroadcast(this, 1, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        pendingIntent = PendingIntent.getBroadcast(this, 2, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        pendingIntent = PendingIntent.getBroadcast(this, 3, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
    }

    private void cancelNotificationAlarm()
    {
        Log.d(globalTag,"Cancel Notification Alarm");
        Intent myIntent = new Intent(this, MessMenuAlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, 1, myIntent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, 2, myIntent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, 3, myIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.return_back_button:
                finish();
                break;
            case R.id.mess_menu_pop_up_menu_button:
                showPopUpMenu();
                break;
        }
    }
    private String getToday()
    {
        Calendar c = Calendar.getInstance();
        int dayofweek = c.get(Calendar.DAY_OF_WEEK);
        String day = days[dayofweek-1];
        return day;
    }

    private int getCurrentHour()
    {
        Calendar c = Calendar.getInstance();
        int hour   = c.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    private String getMonth()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("MMMM");
        String month = dateformat.format(c.getTime());
        Log.d(globalTag,"current Month is "+ month);
        return month;
    }

    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {

            InputStream is =  getAssets().open(fileName);//filename should contain json;

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

    private void showPopUpMenu()
    {
        PopupMenu menu = new PopupMenu(this,messMenuPopUpButton);
        menu.getMenuInflater().inflate(R.menu.messmenuoptions,menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                  case R.id.action_update:
                      downloadNewMenu();
                    break;
                  case  R.id.action_current_menu:
                      loadMenu();
                    break;
                }

                return true;
            }
        });
        menu.show();
    }
    private void downloadNewMenu()
    {
        progressDialog.setMessage("Downloading mess menu...");
        progressDialog.show();
        String url = "https://raw.githubusercontent.com/ankit-gaur/StaticData/master/messMenu.json";
        JsonObjectRequest request = new JsonObjectRequest(url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String jsonString= response.toString();
                if(jsonString.length()>0)
                {
                    SharedPref.saveString(MessMenuActivity.this,messMenuSharedPrefKey,jsonString);
                    loadMenu();
                }
                progressDialog.hide();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error while downloading mess menu",Toast.LENGTH_LONG);
                progressDialog.hide();
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }
}
