package in.ac.iiti.gymakhanaiiti.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.R;

/*
 * Created by Ankit Gaur on 1/7/2017.
 */

public class ContactsListAdapter extends ArrayAdapter<String[]> {
    Activity mContext;
    ArrayList<String[]> contacts;
    int count = 0;
    int[] circle_ids =
            new int[]{R.drawable.contacts_one,R.drawable.contacts_two,R.drawable.contacts_three,R.drawable.contacts_four,R.drawable.contacts_five};
    public ContactsListAdapter(Activity context,ArrayList<String[]> contacts) {
        super(context, R.layout.contacts_list_layout,contacts);
        mContext = context;
        this.contacts =contacts;
        count = 0;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.contacts_list_layout, null, true);
        TextView nameTV = (TextView) rowView.findViewById(R.id.name);
        TextView designationTV = (TextView) rowView.findViewById(R.id.designation_text);
        TextView emailTV = (TextView) rowView.findViewById(R.id.email);
        TextView phoneTV= (TextView) rowView.findViewById(R.id.phone);
        TextView contactsCircle = (TextView)rowView.findViewById(R.id.contact_circle);
        int id = position%5;
        contactsCircle.setBackground(ContextCompat.getDrawable(getContext(),circle_ids[id]));
        String name = contacts.get(position)[1],
               designation = contacts.get(position)[2],
                email = contacts.get(position)[3],
                phone = contacts.get(position)[4],
                circle_char = contacts.get(position)[0]
                ;


        nameTV.setText(name);
        designationTV.setText(designation);
        if(email!=null&&email.length()!=0){
            emailTV.setText(email);
        }else{
           emailTV.setText(phone);
           phone = " ";
        }
        contactsCircle.setText(circle_char);
        phoneTV.setText(phone);
       // imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
