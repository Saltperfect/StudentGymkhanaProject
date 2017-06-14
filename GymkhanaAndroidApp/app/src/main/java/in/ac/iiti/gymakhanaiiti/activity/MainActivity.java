package in.ac.iiti.gymakhanaiiti.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.fragment.ContactsFragment;
import in.ac.iiti.gymakhanaiiti.fragment.GymkhanaTeam;
import in.ac.iiti.gymakhanaiiti.fragment.HostelFragment;
import in.ac.iiti.gymakhanaiiti.imageManipulation.CircleTransform;
import in.ac.iiti.gymakhanaiiti.other.SharedPref;
import in.ac.iiti.gymakhanaiiti.other.Vars;


/**
 * Created by AnkitGaur on 1/3/2017.
 */
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, texEmail;
    private Toolbar toolbar;
    private FrameLayout frame;
    private SliderLayout sliderLayout;
    private ImageView testImageView;


    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home"
            ,TAG_NEWS = "news",
            TAG_GYMKHANA_TEAM ="GymkhanaTeam",
            TAG_CONTACTS = "contacts",
            TAG_HOSTEL = "hostel",
            TAG_SOCIAL= "social";

    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private String name;
    private String email;
    private String photoURL;
    private boolean isGuest = false;
    ArrayList<Integer> sliderImages;


    //myVars_____________________________________________________________________________________________
    boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




       // startActivity(new Intent(this, DiscussionActivity.class));


        // start login activity if user is not logged in;
        if(!SharedPref.getBoolean(getApplicationContext(), Vars.isLoggedIn,false))
        {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }else{
            name = SharedPref.getString(getApplicationContext(),Vars.name);
            email = SharedPref.getString(getApplicationContext(),Vars.email);
            photoURL = SharedPref.getString(getApplicationContext(),Vars.picUrl);
            isGuest = SharedPref.getBoolean(getApplicationContext(),Vars.isGuest,false);
        }



        //*************************************
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        setImageMap();
        setImageSlider();

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        texEmail = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        frame = (FrameLayout)findViewById(R.id.frame);
        frame.setVisibility(View.GONE);
        navItemIndex = 0;
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            //loadHomeFragment();
        }
        if(getIntent().hasExtra("todo"))
        {
            String intent = getIntent().getExtras().getString("todo");
            if(intent.equals("contacts"))
            {
                navItemIndex = 7;
                CURRENT_TAG = TAG_CONTACTS;
                loadHomeFragment();
                navigationView.getMenu().getItem(navItemIndex).setChecked(true);
            }
        }
    }

    /***
     * Setting image map for image slider;
     */
    private void setImageMap()
    {
        //see AndroidImageSlider Library demo on github to understand the implementation of the below code;

        sliderImages = new ArrayList<Integer>();
        sliderImages.add(R.drawable.front_four);
        sliderImages.add(R.drawable.front_one);
        sliderImages.add(R.drawable.hostel_three);
        sliderImages.add(R.drawable.robotics_club);
        sliderImages.add(R.drawable.front_three);
        for(int i = 0; i<sliderImages.size();i++)
        {
            DefaultSliderView sliderView =  new DefaultSliderView(this);
            sliderView.image(sliderImages.get(i)).setScaleType(BaseSliderView.ScaleType.CenterCrop);
            sliderLayout.addSlider(sliderView);
        }

    }

    /**
     * Setting Image Slider
     */
    private void setImageSlider()
    {
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setDuration(4000);
        sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sliderLayout.startAutoCycle();

    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }

    /**
     * Show about the app;
     */
    private void showAboutAppDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.about_app_layout);
        dialog.show();
    }


    private void showContributeDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.contribute_dialog);
        Button ok,discussionActivitybtn;
        ok = (Button)dialog.findViewById(R.id.dialog_OK_button);
        discussionActivitybtn = (Button)dialog.findViewById(R.id.discussionActivityButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        discussionActivitybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DiscussionActivity.class));
            }
        });
        dialog.show();
    }

    /**
     * Load navigation header , name ,email ,profile;
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText(name);
        texEmail.setText(email);

        // loading header background image
        imgNavHeaderBg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.nav_header));

        // Loading profile image
        if(photoURL==null)
        {
            imgProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.acount_pic));
        }else
            Glide.with(this).load(photoURL)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);

        // showing dot next to notifications label
        setNavigationDot(true);
    }

    private void setNavigationDot(boolean doit)
    {   if(doit)
        navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot);
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        frame.setVisibility(View.VISIBLE);
        setToolBarColor(R.color.colorPrimary);

        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 2:
                GymkhanaTeam gymkhanaTeam = new GymkhanaTeam();
                return gymkhanaTeam;
            case  6 :
                HostelFragment hostelFragment = new HostelFragment();
                return hostelFragment;
            case 7:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            default:
                break;
        }

        return null;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }
    private void setToolbarTitle(String title){getSupportActionBar().setTitle(title);}
    private void setToolBarColor(int id)
    {
        toolbar.setBackgroundColor(ContextCompat.getColor(this,id));
    }
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadHomeList()
    {    setToolbarTitle();
        frame.setVisibility(View.GONE);
        setToolBarColor(R.color.transparent);
    }
    public void onClick(View view)
    {
        boolean isFragment = true;
        int id = view.getId();
        switch(id)
        {
            case R.id.news_events_layout:
                isFragment = false;
                navItemIndex = 1;
                startActivity(new Intent(this,NewsAndEventsActivity.class));
                break;
            case R.id.gymkhana_office_layout:
                isFragment = true;
                CURRENT_TAG = TAG_GYMKHANA_TEAM;
                navItemIndex = 2;
                break;
            case R.id.technical_clubs_layout:
                isFragment =false;
                CURRENT_TAG = TAG_HOME;
                startActivity(new Intent(this,TechnicalClubs.class));
                navItemIndex =3;
                break;
            case R.id.cultural_clubs_layout:
                isFragment = false;
                CURRENT_TAG = TAG_HOME;
                startActivity(new Intent(this,CulturalClubs.class));
                navItemIndex = 4;
                break;
            case R.id.photo_gallery_layout:
                isFragment = false;
                CURRENT_TAG = TAG_HOME;
                startActivity(new Intent(this,PhotoGalleryActivity.class));
                navItemIndex = 5;
                break;
            case R.id.achievements_layout:
                isFragment = false;
                CURRENT_TAG = TAG_HOME;
                Intent intent = new Intent(this,PhotoGalleryActivity.class);
                intent.putExtra("todo","show_achievements");
                startActivity(intent);
                break;
            case R.id.hostel_layout:
                CURRENT_TAG = TAG_HOSTEL;
                isFragment = true;
                navItemIndex = 6;
                break;
            case R.id.mess_menu_layout:
                isFragment = false;
                CURRENT_TAG = TAG_HOME;
                startActivity(new Intent(this,MessMenuActivity.class));
                break;
            case R.id.bus_schedule_layout:
                isFragment = false;
                CURRENT_TAG = TAG_HOME;
                showBusSchedule();
                break;
            case R.id.avana_layout:
                isFragment  = false;
                CURRENT_TAG = TAG_HOME;
                Bundle bundle = new Bundle();
                bundle.putInt("description",R.string.avana_description);
                bundle.putInt("clubHeadPicId",0);
                bundle.putInt("wallId",R.drawable.avana_wal);
                bundle.putString("clubName","Avana IIT Indore");
                Intent desIntent = new Intent(this,AboutClubActivity.class);
                desIntent.putExtra("details",bundle);
                startActivity(desIntent);
                break;
        }
        MenuItem menuItem = navigationView.getMenu().getItem(navItemIndex);
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }
        menuItem.setChecked(true);
        if(isFragment)
            loadHomeFragment();
        else navItemIndex = 0; //home when returning from somewhere
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener  to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                boolean isFragment = true;
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        loadHomeList();
                        isFragment = false;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_news_events:
                        navItemIndex = 1;
                        startActivity(new Intent(MainActivity.this,NewsAndEventsActivity.class));
                        isFragment = false;
                        break;
                    case R.id.nav_gymkhana_team:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_GYMKHANA_TEAM;
                        isFragment = true;
                        break;
                    case R.id.nav_technical_clubs:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_HOME;
                        isFragment = false;
                        startActivity(new Intent(MainActivity.this,TechnicalClubs.class));
                        break;
                    case R.id.nav_cultural_club:
                        isFragment = false;
                        CURRENT_TAG = TAG_HOME;
                        startActivity(new Intent(MainActivity.this,CulturalClubs.class));
                        navItemIndex = 4;
                        break;
                    case R.id.nav_photo_gallery:
                        isFragment  = false;
                        CURRENT_TAG = TAG_HOME;
                        startActivity(new Intent(MainActivity.this,PhotoGalleryActivity.class));
                        navItemIndex = 5;
                        break;
                    case R.id.nav_hostel:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_HOSTEL;
                        isFragment = true;
                        break;
                    case R.id.nav_contacts:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_CONTACTS;
                        isFragment = true;
                        break;
                    case R.id.nav_placements:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://placement.iiti.ac.in/"));
                        startActivity(browserIntent);
                        isFragment = false;
                        navItemIndex = 8;
                        break;

                    case R.id.nav_about_us:
                        drawer.closeDrawers();
                        showAboutAppDialog();
                        return true;
                    case R.id.nav_contribute:
                        drawer.closeDrawers();
                        showContributeDialog();
                        return true;
                    case R.id.nav_log_out:
                        logOut();
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                if(isFragment)
                    loadHomeFragment();
                else navItemIndex = 0;

                drawer.closeDrawers();
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
               super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                  super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    private void showBusSchedule() {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "busschedule.pdf");
        try {
            in = assetManager.open("busschedule.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.d(Vars.globalTag, e.getMessage());
            // Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/busschedule.pdf"),
                "application/pdf");
        Log.d(Vars.globalTag, "path " + getFilesDir());
        try {
            startActivity(intent);
        }catch (Exception e)
        {
            Toast.makeText(this,"No application found to open pdf file.",Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Logging out user starting login activity;
     */
    private void logOut()
    {
        SharedPref.saveBoolean(getApplicationContext(),Vars.isLoggedIn,false);
        Intent loginIntent = new Intent(this,LoginActivity.class);
        loginIntent.putExtra(Vars.signOut,true);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeList();
            selectNavMenu();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //automaticaly handles item clicks if parent activity is defined in manifest

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
