package in.ac.iiti.gymakhanaiiti.adapters;

import android.app.Activity;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.R;

/**
 * Created by Ankit Gaur on 1/12/2017.
 */

public class GymkhanaTeamListAdapter extends ArrayAdapter<String[]> {
    Activity mContext;
    ArrayList<String[]> team_details;
    public GymkhanaTeamListAdapter(Activity context,ArrayList<String[]> team_details) {
        super(context, R.layout.gymkhana_team_layout,team_details);
        mContext = context;
        this.team_details =team_details;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.gymkhana_team_layout, null, true);
        TextView nameTV = (TextView) rowView.findViewById(R.id.name);
        TextView designationTV = (TextView) rowView.findViewById(R.id.designation_text);
        TextView emailTV = (TextView) rowView.findViewById(R.id.email);
        TextView phoneTV= (TextView) rowView.findViewById(R.id.phone);
        ImageView profilePic = (ImageView) rowView.findViewById(R.id.profile_pic);
        //int id = position%5;
        String name = team_details.get(position)[1],
                designation = team_details.get(position)[2],
                email = team_details.get(position)[3],
                phone = team_details.get(position)[4],
                image_id = team_details.get(position)[0];
        Log.d("ankit",image_id);
        int id = Integer.parseInt(image_id);
        profilePic.setImageDrawable(ContextCompat.getDrawable(getContext(),id));
        nameTV.setText(name);
        designationTV.setText(designation);
        if(email!=null&&email.length()!=0){
            emailTV.setText(email);
        }else{
            emailTV.setText(phone);
            phone = " ";
        }
        phoneTV.setText(phone);
        // imageView.setImageResource(imageId[position]);
        return rowView;
    }
}