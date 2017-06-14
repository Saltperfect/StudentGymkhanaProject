package in.ac.iiti.gymakhanaiiti.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.adapters.ContactsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {


    public ContactsFragment() {
        // Required empty public constructor
    }
   ArrayList<String[]> contacts = new ArrayList<String[]>();
   ArrayList<String[]> gymOffice  = new ArrayList<String[]>();
    ArrayList<String[]> clubs  = new ArrayList<String[]>();
    ArrayList<String[]> senators  = new ArrayList<String[]>();



    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {

            InputStream is = getActivity(). getAssets().open(fileName);//filename should contain json;

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
//        JSONObject obj = new JSONObject(json_return_by_the_function);
    }

   private void insertContacts()
   {

       String[] c1 = new String[] {"B","Dr. Biswarup Pathak", "Chief Warden" , "biswarup@iiti.ac.in","07312438772"};
       contacts.add(c1);
       c1 = new String[]{"A","Dr. Amit Kumar","Hostel Warden","amitk@iiti.ac.in","07312438771"};
       contacts.add(c1);
       c1 = new String[]{"R","Dr. Ruchi Sharma","Girls' Hostel Warden","ruchi@iiti.ac.in","07312438956"};
       contacts.add(c1);
       c1 = new String[]{"S","Dr. Sharad Gupta","Mess Warden","shgupta@iiti.ac.in","07312438743"};
       contacts.add(c1);
       c1 = new String[]{"D","Mr. Digant Karve","Hostel Supervisor","digantk@iiti.ac.in","9753970010"};
       contacts.add(c1);
       c1 = new String[]{"R","Mr. Ramakant Kaushik","Chief Security Officer","cso@iiti.ac.in","7581055527"};
       contacts.add(c1);
       c1 = new String[]{"S","Ms. Sarita Batra","Hostel Coordinator","batras@iiti.ac.in","9685469561"};
       contacts.add(c1);
       c1 = new String[]{"A","Mr. Arvind Banarasi","Housekeeping Supervisor","","9522498048"};
       contacts.add(c1);
       c1 = new String[]{"M","Mr. Mukesh Tiwari","In-charge La-Fresco","","8962241236"};
       contacts.add(c1);
       c1 = new String[]{"J","Mr. J.C. Upadhyay","Electrician","","9981034224"};
       contacts.add(c1);
       c1 = new String[]{"J","Mr. Janu Kandekar","Plumber","","9981034220"};
       contacts.add(c1);
       c1 = new String[]{"S","SecurityHelpDesk","Security","securityhelpdesk@iiti.ac.in","7509062839"};
       contacts.add(c1);

       fillClub();
       fillGym();
       fillSenates();
   }
   private void fillClub()
   {
       try {
           JSONArray array = new JSONArray(loadJSONFromAsset("clubheads.json"));
           for(int i=0;i<array.length();i++)
           {
               JSONObject contact = array.getJSONObject(i);
               String[] details = new String[5];
               details[1] = contact.getString("head");
               details[0] = ""+details[1].charAt(0);
               details[2] = contact.getString("club name");
               details[3] = contact.getString("email id");
               details[4] = ""+contact.getString("mobile");
               clubs.add(details);
           }
       }catch (org.json.JSONException e)
       {Log.d("ankit","error in json in club");
           e.printStackTrace();
       }

   }
   private void fillGym()
   {
       try {
           JSONArray array = new JSONArray(loadJSONFromAsset("gymkhana_office.json"));
           for(int i=0;i<array.length();i++)
           {
               JSONObject contact = array.getJSONObject(i);
               String[] details = new String[5];
               details[1] = contact.getString("name");
               details[0] = ""+details[1].charAt(0);
               details[2] = contact.getString("post");
               details[3] = contact.getString("email id");
               details[4] = ""+contact.getString("mobile");
               gymOffice.add(details);
           }
       }catch (org.json.JSONException e)
       {Log.d("ankit","error in json");
           e.printStackTrace();
       }
   }
   private void fillSenates()
   {
       try {
           JSONArray array = new JSONArray(loadJSONFromAsset("senators.json"));
           for(int i=0;i<array.length();i++)
           {
               JSONObject contact = array.getJSONObject(i);
               String[] details = new String[5];
               details[1] = contact.getString("senator name");
               details[0] = ""+details[1].charAt(0);
               details[2] = contact.getString("senator post");
               details[3] = contact.getString("email id");
               details[4] = ""+contact.getString("mobile");
               senators.add(details);
           }
       }catch (org.json.JSONException e)
       {
           Log.d("ankit","error in json");
           e.printStackTrace();
       }
   }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        insertContacts();
        View root =  inflater.inflate(R.layout.fragment_contacts, container, false);
        ContactsListAdapter adapter = new
                ContactsListAdapter(getActivity(),contacts);
       ListView  list=(ListView)root.findViewById(R.id.hostelcontacts);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
              final  String phone = contacts.get(position)[4];
                String name = contacts.get(position)[1];
                String des  = contacts.get(position)[2];
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.call_layout);
                TextView alert =(TextView) dialog.findViewById(R.id.content_diaolog_tv);
                alert.setText("Call "+name+" ("+des+") ?");
                TextView yes = (TextView)dialog.findViewById(R.id.yestv);
                TextView no= (TextView)dialog.findViewById(R.id.notv);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phone));
                        startActivity(callIntent);
                        dialog.cancel();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();


            }
        });
        adapter = new ContactsListAdapter(getActivity(),gymOffice);
        ListView  gymlist=(ListView)root.findViewById(R.id.gymkhana_contacts);
        gymlist.setAdapter(adapter);
        gymlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final  String phone = gymOffice.get(position)[4];
                String name = gymOffice.get(position)[1];
                String des  = gymOffice.get(position)[2];
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.call_layout);
                TextView alert =(TextView) dialog.findViewById(R.id.content_diaolog_tv);
                alert.setText("Call "+name+" ("+des+") ?");
                TextView yes = (TextView)dialog.findViewById(R.id.yestv);
                TextView no= (TextView)dialog.findViewById(R.id.notv);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phone));
                        startActivity(callIntent);
                        dialog.cancel();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();


            }
        });
        adapter = new ContactsListAdapter(getActivity(),clubs);
        final ListView clubsList=(ListView)root.findViewById(R.id.club_contacts);
        clubsList.setAdapter(adapter);
        clubsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final  String phone = clubs.get(position)[4];
                String name = clubs.get(position)[1];
                String des  = clubs.get(position)[2];
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.call_layout);
                TextView alert =(TextView) dialog.findViewById(R.id.content_diaolog_tv);
                alert.setText("Call "+name+" ("+des+") ?");
                TextView yes = (TextView)dialog.findViewById(R.id.yestv);
                TextView no= (TextView)dialog.findViewById(R.id.notv);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phone));
                        startActivity(callIntent);
                        dialog.cancel();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();


            }
        });
        adapter = new ContactsListAdapter(getActivity(),senators);
        final ListView  senatorsList = (ListView)root.findViewById(R.id.senators_contacts);
        senatorsList.setAdapter(adapter);
        senatorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final  String phone = senators.get(position)[4];
                String name = senators.get(position)[1];
                String des  = senators.get(position)[2];
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.call_layout);
                TextView alert =(TextView) dialog.findViewById(R.id.content_diaolog_tv);
                alert.setText("Call "+name+" ("+des+") ?");
                TextView yes = (TextView)dialog.findViewById(R.id.yestv);
                TextView no= (TextView)dialog.findViewById(R.id.notv);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phone));
                        startActivity(callIntent);
                        dialog.cancel();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();


            }
        });
        ListUtils.setDynamicHeight(list);
        ListUtils.setDynamicHeight(gymlist);
        ListUtils.setDynamicHeight(clubsList);
        ListUtils.setDynamicHeight(senatorsList);

        return root;
    }
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                Log.d("ankit","adapter is null");
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}

