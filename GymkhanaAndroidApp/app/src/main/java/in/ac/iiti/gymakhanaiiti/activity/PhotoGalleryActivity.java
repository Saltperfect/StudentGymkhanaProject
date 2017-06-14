package in.ac.iiti.gymakhanaiiti.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.fragment.SlideshowDialogFragment;
import in.ac.iiti.gymakhanaiiti.adapters.GalleryAdapter;
import in.ac.iiti.gymakhanaiiti.other.Vars;
import in.ac.iiti.gymakhanaiiti.views.Image;
import in.ac.iiti.gymakhanaiiti.other.Methods;


public class PhotoGalleryActivity extends AppCompatActivity {

    private String TAG = PhotoGalleryActivity.class.getSimpleName();
    private static  String endpoint = "http://www.frinkyapps.16mb.com/images.json";//"http://www.fluxus.in/assets/images/icons/gymapp/images.json"
    private String sharedPrefsPath = "photogallery";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enabling
        setToolbarTitle("Photo Gallery");

        if(getIntent().hasExtra("todo"))
        {
            //showing Achievements;
            setToolbarTitle("IIT Indore In News");
            endpoint = "http://frinkyapps.16mb.com/Achievments/images_achievements.json";
        }else{
            endpoint = "http://www.frinkyapps.16mb.com/images.json";
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
       // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        fetchImages();
    }
    private void setToolbarTitle(String title){getSupportActionBar().setTitle(title);}
    private void fetchImages() {

        pDialog.setMessage("Downloading images....");
        pDialog.show();


     /*   //if internet is not available then show previous loaded images.
      if(!Methods.isNetworkConnected(this))
        {
            JSONArray response = getPhotoGalleryJsonArray();
            Log.d(Vars.globalTag,response.toString());
            if(response==null||response.length()==0) return;
            images.clear();
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    Image image = new Image();
                    image.setName(object.getString("name"));

                    JSONObject url = object.getJSONObject("url");
                    image.setSmall(url.getString("small"));
                    image.setMedium(url.getString("medium"));
                    image.setLarge(url.getString("large"));
                    image.setTimestamp(object.getString("timestamp"));

                    images.add(image);
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }
            mAdapter.notifyDataSetChanged();
            pDialog.hide();
            return;
        }
*/

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                        Log.d("ankit","got json response photo gallery");
                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));
                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.d("ankit", "photo-->Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                       //saveJsonArray(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("ankit", " photo gallery Error: " + error.getMessage()+error+error.toString());
                Toast.makeText(PhotoGalleryActivity.this,"Error while Downloading Images",Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        });

        AppController.getInstance().addToRequestQueue(req);
    }

    private void saveJsonArray(JSONArray jsonArray)
    {
        String json = jsonArray.toString();
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefsPath,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("photoGalleryJson",json);
        editor.apply();
    }
    private JSONArray getPhotoGalleryJsonArray()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefsPath,MODE_PRIVATE);
        String json = sharedPreferences.getString("photoGalleryJson","");
        if(json.length()==0)return null;
        else{
            try {
                JSONArray jsonArray = new JSONArray(json);
                return jsonArray;
            }catch (JSONException e){
                Log.d("ankit", "news -- can't parse string to json array");
                return null;
            }


        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}