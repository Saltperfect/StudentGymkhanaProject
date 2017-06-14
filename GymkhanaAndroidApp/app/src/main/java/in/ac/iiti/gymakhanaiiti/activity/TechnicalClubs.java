package in.ac.iiti.gymakhanaiiti.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.ac.iiti.gymakhanaiiti.R;

import com.daimajia.slider.library.SliderTypes.TextSliderView;


public class TechnicalClubs extends AppCompatActivity {
    Toolbar toolbar;
    private SliderLayout sliderLayout;
    HashMap<String,Integer> sliderImages = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_clubs);
        toolbar =(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enabling
        setToolbarTitle("Technical Clubs");

        sliderLayout = (SliderLayout)findViewById(R.id.slider);
        setImageMap();
        setImageSlider();


    }
    private void setImageMap()
    {
        //see AndroidImageSlider Library demo on github to understand the implementation of the below code;

        sliderImages.put("Robotics Club",R.drawable.robotics_club);
        sliderImages.put("Programming Club",R.drawable.prog_members);
        sliderImages.put("Electronics Club",R.drawable.electronic_club);
        sliderImages.put("Astronomy Club",R.drawable.astronomy_club);
        sliderImages.put("Unmanned Club",R.drawable.unmanned_club);
        sliderImages.put("Quiz Club",R.drawable.quiz_club);
        //tp get index of name in hash map.  so that for each name a particular club will be opened on slider click.
        final List<String> indexes = new ArrayList<String>(sliderImages.keySet());
        for(String name : sliderImages.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
              textSliderView
                    .description(name)
                    .image(sliderImages.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            String name = slider.getDescription();
                            int index = indexes.indexOf(name);
                          //  onClick(index);
                        }
                    });

            sliderLayout.addSlider(textSliderView);
        }

    }

    private void setImageSlider()
    {
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setDuration(4000);
        sliderLayout.startAutoCycle();

    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }


    private void setToolbarTitle(String title){getSupportActionBar().setTitle(title);}
    private void onClick(int layoutPosition)
    {
        int clubHeadPicId = 0,wallId = 0 ,descriptionID = 0;
        String clubName = "";
        switch (layoutPosition)
        {
            case 0:
                clubHeadPicId = 0;
                wallId = R.drawable.robotics_club;
                clubName = "Robotics Club";
                descriptionID = R.string.robotics_club_des;
                break;
            case 1:
                clubHeadPicId = 0;
                wallId = R.drawable.prog_members;
                clubName = "Programming Club";
                descriptionID = R.string.programming_club_des;
                break;
            case 2:
                clubHeadPicId = 0;
                wallId = R.drawable.electronic_club;
                clubName = "Electronics Club";
                descriptionID = R.string.electronics_club_des;
                break;
            case 3:
                clubHeadPicId = 0;
                wallId = R.drawable.astronomy_club;
                clubName = "Astronomy Club";
                descriptionID = R.string.astronomy_club_des;
                break;
            case 4:
                clubHeadPicId = 0;
                wallId = R.drawable.unmanned_club;
                clubName = "Unmanned Club";
                descriptionID = R.string.unmanned_club_des;
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("description",descriptionID);
        bundle.putInt("clubHeadPicId",clubHeadPicId);
        bundle.putInt("wallId",wallId);
        bundle.putString("clubName",clubName);
        Intent desIntent = new Intent(this,AboutClubActivity.class);
        desIntent.putExtra("details",bundle);
        startActivity(desIntent);
    }
    public void onClick(View view)
    {
        int id = view.getId();
        int clubHeadPicId = 0,wallId = 0 ,descriptionID = 0;
        String clubName = "";
        switch (id)
        {
            case R.id.robotics_club_layout:
                clubHeadPicId = 0;
                wallId = R.drawable.robotics_club;
                clubName = "Robotics Club";
                descriptionID = R.string.robotics_club_des;
                break;
            case R.id.programming_club:
                clubHeadPicId = 0;
                wallId = R.drawable.prog_members;
                clubName = "Programming Club";
                descriptionID = R.string.programming_club_des;
                break;
            case R.id.electronic_club:
                clubHeadPicId = 0;
                wallId = R.drawable.electronic_club;
                clubName = "Electronics Club";
                descriptionID = R.string.electronics_club_des;
                break;
            case R.id.astronomy_club:
                clubHeadPicId = 0;
                wallId = R.drawable.astronomy_club;
                clubName = "Astronomy Club";
                descriptionID = R.string.astronomy_club_des;
                break;
            case R.id.unmanned_club:
                clubHeadPicId = 0;
                wallId = R.drawable.unmanned_club;
                clubName = "Unmanned Club";
                descriptionID = R.string.unmanned_club_des;
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("description",descriptionID);
        bundle.putInt("clubHeadPicId",clubHeadPicId);
        bundle.putInt("wallId",wallId);
        bundle.putString("clubName",clubName);
        Intent desIntent = new Intent(this,AboutClubActivity.class);
        desIntent.putExtra("details",bundle);
        startActivity(desIntent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

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
}
