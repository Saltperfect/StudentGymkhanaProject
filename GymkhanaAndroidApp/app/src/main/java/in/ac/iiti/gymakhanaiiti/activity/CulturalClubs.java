package in.ac.iiti.gymakhanaiiti.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.ac.iiti.gymakhanaiiti.R;

public class CulturalClubs extends AppCompatActivity {
   Toolbar toolbar;
    private SliderLayout sliderLayout;
    HashMap<String,Integer> sliderImages = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_clubs);
        toolbar =(Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enabling
        setToolbarTitle("Cultural Clubs");

        sliderLayout = (SliderLayout)findViewById(R.id.slider);
        setImageMap();
        setImageSlider();


    }
    private void setImageMap()
    {
        //see AndroidImageSlider Library demo on github to understand the implementation of the below code;

        sliderImages.put("Music Club",R.drawable.music_club);
        sliderImages.put("Photography Club",R.drawable.hostel_three);
        sliderImages.put("Literary Club",R.drawable.literary_club);
        sliderImages.put("Dance Club",R.drawable.dance_club);
        sliderImages.put("Quiz Club",R.drawable.quiz_club);
        sliderImages.put("Dramatics Club",R.drawable.drama_club);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void onClick(int positionInLayout)
    {
        int clubHeadPicId = 0,wallId = 0 ,descriptionID = 0;
        String clubName = "";
        switch(positionInLayout)
        {
            case 0:
                clubHeadPicId = 0;
                wallId = R.drawable.music_club;
                clubName = "Music Club";
                descriptionID = R.string.music_club_description;
                break;
            case 1:
                clubHeadPicId = 0;
                wallId = R.drawable.hostel_three;
                clubName = "Photography Club";
                descriptionID = R.string.photography_club_description;
                break;
            case 2:
                clubHeadPicId = 0;
                wallId = R.drawable.literary_club;
                clubName = "Literary Club";
                descriptionID = R.string.literary_club_description;
                break;
            case 3:
                clubHeadPicId = 0;
                wallId = R.drawable.dance_club;
                clubName = "Dance Club";
                descriptionID = R.string.dance_club_description;
                break;
            case 4:
                clubHeadPicId = 0;
                wallId = R.drawable.quiz_club;
                clubName = "Quiz Club";
                descriptionID = R.string.quiz_club_description;
                break;
            case 5:
                clubHeadPicId = 0;
                wallId = R.drawable.drama_club;
                clubName = "Drama Club";
                descriptionID = R.string.dramatics_club_description;
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
        int clubHeadPicId = 0,wallId = 0 ,descriptionID = 0;
        String clubName = "";
        int id = view.getId();
        switch(id)
        {
            case R.id.music_club_layout:
                clubHeadPicId = 0;
                wallId = R.drawable.music_club;
                clubName = "Music Club";
                descriptionID = R.string.music_club_description;
            break;
            case R.id.photography_club_layout:
                clubHeadPicId = 0;
                wallId = R.drawable.hostel_three;
                clubName = "Photography Club";
                descriptionID = R.string.photography_club_description;
                break;
            case R.id.literary_club:
                clubHeadPicId = 0;
                wallId = R.drawable.literary_club;
                clubName = "Literary Club";
                descriptionID = R.string.literary_club_description;
                break;
            case R.id.dance_club:
                clubHeadPicId = 0;
                wallId = R.drawable.dance_club;
                clubName = "Dance Club";
                descriptionID = R.string.dance_club_description;
                break;
            case R.id.quiz_club:
                clubHeadPicId = 0;
                wallId = R.drawable.quiz_club;
                clubName = "Quiz Club";
                descriptionID = R.string.quiz_club_description;
                break;
            case R.id.drama_club:
                clubHeadPicId = 0;
                wallId = R.drawable.drama_club;
                clubName = "Drama Club";
                descriptionID = R.string.dramatics_club_description;
                break;
            case R.id.docm_layout:
                clubHeadPicId = 0;
                wallId = 0;
                clubName = "DoCM";
                descriptionID = R.string.docm_club_description;
                break;
            case R.id.fine_art_club_layout:
                clubHeadPicId = 0;
                wallId = 0;
                clubName = "The Fine Arts Club";
                descriptionID = R.string.fine_arts_club_description;
                break;
            case R.id.hindi_sahitya_samiti_layout:
                clubHeadPicId = 0;
                wallId = R.drawable.srijan_wall;
                clubName = "Srijan";
                descriptionID = R.string.hindi_sahitya_samiti_description;
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
