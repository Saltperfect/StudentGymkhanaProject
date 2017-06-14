package in.ac.iiti.gymakhanaiiti.activity;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.imageManipulation.BlurClass;
import in.ac.iiti.gymakhanaiiti.other.MyTagHandler;

public class AboutClubActivity extends AppCompatActivity {
   Toolbar toolbar;
   ImageView clubWall,clubHead,blurBackgroundWall;
   TextView descriptionTV;
    /*
    “He who asks a question is a fool for five minutes; he who does not ask a question remains a fool forever”
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_club);
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enabling

        clubWall = (ImageView)findViewById(R.id.club_wall_image_view);
        descriptionTV = (TextView)findViewById(R.id.club_description);
        blurBackgroundWall = (ImageView)findViewById(R.id.club_wall_background);

        if(!getIntent().hasExtra("details")) finish();
        Bundle detailsOfClub =  getIntent().getExtras().getBundle("details");
        int wallId = detailsOfClub.getInt("wallId");
        int description = detailsOfClub.getInt("description");
        int clubHeadIdPicId = detailsOfClub.getInt("clubHeadPicId");
        String clubName = detailsOfClub.getString("clubName");

        if(wallId!=0)
        {
            clubWall.setImageDrawable(ContextCompat.getDrawable(this,wallId));
            Bitmap blurredBackground = BlurClass.fastblur(BlurClass.convertToBmpfromResourceDrawable(this,wallId),15);
            blurredBackground = BlurClass.changeBitmapContrastBrightness(blurredBackground,0.9f,-25);
            blurBackgroundWall.setImageBitmap(blurredBackground);
        }
        else {
            clubWall.setVisibility(View.GONE);
            blurBackgroundWall.setVisibility(View.GONE);
        }


        descriptionTV.setText(Html.fromHtml(getString(description),null,new MyTagHandler()));

        setToolbarTitle(clubName);
    }
    private void setToolbarTitle(String title){getSupportActionBar().setTitle(title);}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
