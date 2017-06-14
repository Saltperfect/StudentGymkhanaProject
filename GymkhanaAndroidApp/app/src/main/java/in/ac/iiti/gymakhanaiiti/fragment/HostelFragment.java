package in.ac.iiti.gymakhanaiiti.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.activity.MainActivity;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.MyTagHandler;
import in.ac.iiti.gymakhanaiiti.other.Vars;

/**
 * A simple {@link Fragment} subclass.
 */
public class HostelFragment extends Fragment {

    private SliderLayout sliderLayout;
    ArrayList<Integer> sliderImages;
    public HostelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_hostel, container, false);
        TextView about = (TextView)root.findViewById(R.id.hostel_about_tv);
        about.setText(Html.fromHtml(getString(R.string.hostel_description),null,new MyTagHandler()));
        sliderLayout = (SliderLayout)root. findViewById(R.id.slider);
        setImageMap();
        setImageSlider();
        Button contacts =(Button) root.findViewById(R.id.hostel_contacts);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Vars.globalTag,"hostel fragment button click");
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("todo","contacts");
                startActivity(intent);
            }
        });
        return root;
    }
    private void setImageMap()
    {
        //see AndroidImageSlider Library demo on github to understand the implementation of the below code;

        sliderImages = new ArrayList<Integer>();
        sliderImages.add(R.drawable.hostel_one);
        sliderImages.add(R.drawable.hostel_two);
        sliderImages.add(R.drawable.hostel_three);
        sliderImages.add(R.drawable.hostel_four);
        sliderImages.add(R.drawable.hostel_five);
        for(int i = 0; i<sliderImages.size();i++)
        {
            DefaultSliderView defaultSliderView =  new DefaultSliderView(getContext());
            defaultSliderView.image(sliderImages.get(i)).setScaleType(BaseSliderView.ScaleType.CenterCrop);
            sliderLayout.addSlider(defaultSliderView);
        }

    }

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
    public void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }
}
