package in.ac.iiti.gymakhanaiiti.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.imageManipulation.BlurClass;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.models.News;

public class NewsAndEventsActivity extends AppCompatActivity {

    private static final String endpoint = "http://www.frinkyapps.16mb.com/news/news.json";
    private String sharedPrefsPath = "newsandevents";
    private ProgressDialog progressDialog;
    private ArrayList<News> newses; //news itself is a plural ;)
    private LinearLayout container;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_and_events);
        progressDialog = new ProgressDialog(this);
       toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enabling
        setToolbarTitle("News and Events");
        container = (LinearLayout)findViewById(R.id.container);
        newses = new ArrayList<News>();
        fetchNews();
    }
    private void setToolbarTitle(String title){getSupportActionBar().setTitle(title);}
    private void fetchNews() {

        progressDialog.setMessage("Loading News...");
        progressDialog.show();

        //if internet is not available then show previously loaded news
        if(!Methods.isNetworkConnected(this))
        {
            JSONArray response = getNewsJsonArray();
            if(response == null||response.length()==0)return;
            newses.clear();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    News news = new News();
                    news.setTitle(object.getString("title"));
                    news.setDescription(object.getString("description"));
                    news.setImageUrl(object.getString("imageurl"));
                    news.setTimestamp(object.getString("timestamp"));
                    newses.add(news);
                    Log.d("ankit",news.getImageUrl()+news.getTitle()+news.getDescription());
                } catch (JSONException e) {
                    Log.d("ankit", "Json parsing error: " + e.getMessage());
                }
            }
            showNews();
            return;
        }

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("ankit","got json response");
                        newses.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                News news = new News();
                                news.setTitle(object.getString("title"));
                                news.setDescription(object.getString("description"));
                                news.setImageUrl(object.getString("imageurl"));
                                news.setTimestamp(object.getString("timestamp"));
                                newses.add(news);
                               Log.d("ankit",news.getImageUrl()+news.getTitle()+news.getDescription());
                            } catch (JSONException e) {
                                Log.d("ankit", "Json parsing error: " + e.getMessage());
                            }
                        }
                        showNews();
                        saveJsonArray(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(),"Error while loading news",Toast.LENGTH_LONG);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void showNews(){

       if(newses==null||newses.size()==0)
           return;

        for(int i = 0; i<newses.size(); i++) {

            View view = getLayoutInflater().inflate(R.layout.single_news_layout, null, false);
            ImageView newsImage = (ImageView) view.findViewById(R.id.newsImage);
            ImageView blurBackground = (ImageView) view.findViewById(R.id.blurImage);
            TextView titleTV, timestampTV, descriptionTV;
            titleTV = (TextView) view.findViewById(R.id.title);
            timestampTV = (TextView) view.findViewById(R.id.timestamp);
            descriptionTV = (TextView) view.findViewById(R.id.description);
            News news = newses.get(i);
            titleTV.setText(news.getTitle());
            descriptionTV.setText(news.getDescription());
            timestampTV.setText(news.getTimestamp());
            loadImage(news.getImageUrl(),newsImage,blurBackground);

            container.addView(view);
            if(progressDialog.isShowing()) progressDialog.hide(); //hiding progress dialog when first image is loaded
        }


    }
    private void saveJsonArray(JSONArray jsonArray)
    {
        String json = jsonArray.toString();
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefsPath,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("newsJson",json);
        editor.apply();
    }
    private JSONArray getNewsJsonArray()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefsPath,MODE_PRIVATE);
        String json = sharedPreferences.getString("newsJson","");
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
    private void loadImage(String url, ImageView news, final ImageView blur)
    {
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        new AsyncTask<Drawable,Void,Bitmap>(){
                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                super.onPostExecute(bitmap);
                                blur.setImageBitmap(bitmap);
                            }

                            @Override
                            protected Bitmap doInBackground(Drawable... params) {
                                Drawable img = params[0];
                                Bitmap bitmap = BlurClass.fastblur(BlurClass.convetToBmpFromDrawable(img),26);
                                bitmap = BlurClass.changeBitmapContrastBrightness(bitmap,0.9f,-10);
                                return bitmap;
                            }
                        }.execute(resource);
                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(news);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    }

