package in.ac.iiti.gymakhanaiiti.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.fragment.SingleTopicDiscussionFragment;
import in.ac.iiti.gymakhanaiiti.other.Vars;
import in.ac.iiti.gymakhanaiiti.views.TextViewRoundFont;

import static android.R.attr.action;

public class DiscussionActivity extends AppCompatActivity {

    private String Tag = Vars.globalTag;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageButton muteUnmuteButton;
    private ImageButton popUpNavMenuButton;
    private TextViewRoundFont toolbarTitleTV;
    private FrameLayout fragmentContainer;
    private TextView navHeaderUserNameTV;
    private FloatingActionButton createPostButton;

    private Handler mHandler;

    private boolean isMute = false;

    private String currentTag = Vars.TopicGeneralDiscussion;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        //Initialising views
        initViews();

        mHandler = new Handler();

        loadCurrentFragment(false);

    }

    public void onClickNavItem(View view)
    {
        drawerLayout.closeDrawers();
        boolean isFragment = true; //not every  inputs handled by this function has to load a fragment.
        switch (view.getId())
        {
            case R.id.discussion_activity_navigation_drawer_button :
                drawerLayout.openDrawer(Gravity.LEFT);
                isFragment = false;
                break;
            case R.id.mute_unmute_topic_button:
                muteUnmuteCurrentTopic();
                isFragment = false;
                break;
            case R.id.home_button_nav :
                startActivity(new Intent(this,MainActivity.class));
                isFragment = false;
                break;
            case R.id.user_name_nav_TV:
                isFragment = false;
                break;
            case R.id.discussion_activity_pop_up_menu_button:
                isFragment = false;
                showPopUpMenu(); // refresh and logout
                break;
            case R.id.discussion_add_post_fab:
                isFragment = false;
                Intent createActivityIntent =  new Intent(this,CreatePostActivity.class);
                createActivityIntent.putExtra("topic",currentTag);
                startActivity(createActivityIntent);
                break;
            //Fragment handling
            case R.id.general_discussion_nav_button:
                currentTag  = Vars.TopicGeneralDiscussion;
                break;
            case R.id.programming_discussion_nav_button:
                currentTag = Vars.TopicProgramming;
                break;
            case R.id.robotics_nav_button:
                currentTag = Vars.TopicRobotics;
                break;
            case R.id.electronic_nav_button:
                currentTag = Vars.TopicElectronics;
                break;
            case R.id.discussion_art_work_nav_button:
                currentTag = Vars.TopicMyArtWork;
                break;
            case R.id.tutor_disscusion_icon:
                currentTag = Vars.TopicTeachMe;
                break;
            case R.id.share_discussion_button:
                currentTag = Vars.TopicShare;
                break;
            case R.id.discussion_nav_mess_button:
                currentTag = Vars.TopicMess;
                break;
            case R.id.lost_and_found_nav_button:
                currentTag = Vars.TopicLostAndFound;
                break;
            case R.id.nav_log_out:
                logout();
                break;
            case R.id.bug_report_nav_button:
                reportBug();
                break;
            default:
                currentTag = Vars.TopicGeneralDiscussion;
                break;
        }
        if(isFragment){
            loadCurrentFragment(false);
        }

    }
    private void logout()
    {
        //TODO complete this function
    }
    private void reportBug()
    {
        //TODO complete it too ;)
    }
    private void loadCurrentFragment(boolean isReload)
    {
        //Changing topic name in toolbar
        toolbarTitleTV.setText(currentTag);

        //If current fragment is already the same fragment which you want to load and you don't want to reload fragment
        if (!isReload && getSupportFragmentManager().findFragmentByTag(currentTag) != null) {
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // changin fragment according to selected topic;
                // tag helps system to reuse the previously used same fragment without creating it again;
                Fragment fragment = getCurrentFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.fragment_container, fragment, currentTag );
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }
    private Fragment getCurrentFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putString("topic", currentTag);
        SingleTopicDiscussionFragment frag  = new SingleTopicDiscussionFragment();
        frag.setArguments(bundle);
        return frag;
    }
    private void muteUnmuteCurrentTopic()
    {
        if(isMute){
            isMute = false;
            muteUnmuteButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.discussion_mute_disabled_icon));
            Toast.makeText(this,"Notifications enabled for "+currentTag,Toast.LENGTH_SHORT).show();
        }
        else{
            isMute = true;
            muteUnmuteButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.discussion_mute_enabled_icon));
            Toast.makeText(this,"Notifications disabled for "+currentTag,Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopUpMenu()
    {
        PopupMenu menu = new PopupMenu(this,popUpNavMenuButton);
        menu.getMenuInflater().inflate(R.menu.discussion_menu_main,menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_refresh :
                        loadCurrentFragment(true);
                        break;
                    case R.id.action_logout:
                        logout();
                        break;
                }

                return true;
            }
        });
        menu.show();
    }



    private void initViews()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        muteUnmuteButton = (ImageButton)findViewById(R.id.mute_unmute_topic_button);
        popUpNavMenuButton = (ImageButton)findViewById(R.id.discussion_activity_pop_up_menu_button);
        toolbarTitleTV = (TextViewRoundFont)findViewById(R.id.discussion_topic_TV);
        navHeaderUserNameTV = (TextView)findViewById(R.id.user_name_nav_TV);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        createPostButton = (FloatingActionButton)findViewById(R.id.discussion_add_post_fab);
    }


    @Override
    public void onBackPressed() {
        //TODO handle it;
        super.onBackPressed();
    }


    /*  private void changeStatusBarColorToBlack()
    {
        if(Build.VERSION.SDK_INT<21) return;
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
    }*/
}
