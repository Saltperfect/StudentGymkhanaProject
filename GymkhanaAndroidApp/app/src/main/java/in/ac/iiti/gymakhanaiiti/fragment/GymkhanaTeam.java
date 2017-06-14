package in.ac.iiti.gymakhanaiiti.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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
import in.ac.iiti.gymakhanaiiti.adapters.GymkhanaTeamListAdapter;

public class GymkhanaTeam extends Fragment {



    ArrayList<String[]> team_details = new ArrayList<String[]>();
    int[] ids = {R.drawable.gen_sec,R.drawable.mess_sec,R.drawable.scie_tech,R.drawable.sports_sec,R.drawable.academic_sec,R.drawable.hostel_sec,R.drawable.cultural_sec};
    public GymkhanaTeam() {
        // Required empty public constructor
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
                details[0] = ""+ids[i];
                details[2] = contact.getString("post");
                details[3] = contact.getString("email id");
                details[4] = ""+contact.getString("mobile");
                team_details.add(details);
            }
        }catch (org.json.JSONException e)
        {
            Log.d("ankit","error in json");
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fillGym();

       View root =  inflater.inflate(R.layout.fragment_gymkhana_team, container, false);
       final ListView list = (ListView)root.findViewById(R.id.listViewGymkhanateam);
        GymkhanaTeamListAdapter gymkhanaTeamListAdapter = new GymkhanaTeamListAdapter(getActivity(),team_details);
        list.setAdapter(gymkhanaTeamListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //********************************  fix this


                view.performLongClick();
                if(true)
                return;


                //******************

                TextView description = (TextView)view.findViewById(R.id.description_of_member);
                if(position==4)return;//academic secretary description is not available;

                if(description.getVisibility()==View.GONE)
                {
                    description.setVisibility(View.VISIBLE);
                    ListUtils.setDynamicHeight(list);
                }else{
                    description.setVisibility(View.GONE);
                    ListUtils.setDynamicHeight(list);
                }


                switch(position)
                {
                    case 0:
                         description.setText(Html.fromHtml(getString(R.string.gen_sec_description)));
                        break;
                    case 1:
                        description.setText(Html.fromHtml(getString(R.string.research_sec_description)));
                        break;
                    case 2:
                        description.setText(Html.fromHtml(getString(R.string.scietech_description)));
                        break;
                    case 3:
                        description.setText(Html.fromHtml(getString(R.string.sports_sec_description)));
                        break;
                    case 4:
                        break;
                    case 5:
                        description.setText(Html.fromHtml(getString(R.string.hostel_sec_description)));
                        break;
                    case 6:
                        description.setText(Html.fromHtml(getString(R.string.cultural_sec_description)));
                        break;
                    default:
                        break;
                }
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final  String phone = team_details.get(position)[4];
                String name = team_details.get(position)[1];
                String des  = team_details.get(position)[2];
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
                return true ;
            }
        });

        ListUtils.setDynamicHeight(list);

        return root;
    }
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
