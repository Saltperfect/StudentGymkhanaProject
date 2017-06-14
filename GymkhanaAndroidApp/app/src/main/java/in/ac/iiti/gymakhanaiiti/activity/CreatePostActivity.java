package in.ac.iiti.gymakhanaiiti.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iiti.gymakhanaiiti.AppController;
import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.User.AuthenticationHeader;
import in.ac.iiti.gymakhanaiiti.other.Methods;
import in.ac.iiti.gymakhanaiiti.other.Vars;

public class CreatePostActivity extends AppCompatActivity {

    private String TAG = Vars.globalTag;

    String requestTag = "CreatePost";
    private String title = "";
    private String body;
    private String addPostUrl = Vars.CreatePostUrl;
    private String topic = Vars.TopicGeneralDiscussion; //taking general as default;
    private boolean isBoldSelected = false;
    private boolean isItalicSelected  = false;
    private int actionOffset = 0;
    private boolean isPostUploading = false;

    private int PICK_IMAGE_REQUEST = 1;

    private EditText titleET;
    private EditText bodyEt;
    private ImageView boldIv;
    private ImageView italicIv;
    private LinearLayout imageContainer;
    private ProgressDialog progressDialog;

    private ArrayList<String> stringImages = new ArrayList<String>();
    private ArrayList<String> uploadedImageUrls = new ArrayList<String>();
    private int currentImageUploadIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);


        if(getIntent().hasExtra("topic"))
        {
            topic = getIntent().getExtras().getString("topic");
        }else{
            finish();
        }

        initViews();

    }


    private void initViews()
    {
        progressDialog = new ProgressDialog(this);
        bodyEt = (EditText)findViewById(R.id.post_body_ET);
        titleET = (EditText)findViewById(R.id.post_title_ET);
        boldIv = (ImageView)findViewById(R.id.bold_image_view);
        italicIv = (ImageView)findViewById(R.id.italic_image_view);
        imageContainer = (LinearLayout)findViewById(R.id.image_container);
    }


    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.send_post_button:
                uploadImageAndThenPost();
                break;


            case R.id.return_back_button:
                savePostAsDraft();
                finish();
                break;



            case R.id.upload_image_layout:
                showImageChooser();
                break;

            case R.id.add_link_layout: //TODO complete it; and also add Linkmethod in body tv;
                break;

            case R.id.add_attachment_layout:
                Toast.makeText(this,"Attachment option not implemented yet.",Toast.LENGTH_SHORT).show();
                break;

        }
    }






    private void savePostAsDraft()
    {
        //TODO save post as draft with a boolean has draft;
    }



    private void uploadImageAndThenPost()
    {
        uploadedImageUrls = new ArrayList<String>();

        if(isPostUploading) return;

        isPostUploading = true; //to prevent multiple send request at the same time for the same post if user clicks by mistake;

        progressDialog.setMessage("Uploading Post....");

        progressDialog.show();

        if(stringImages.size()>0)
        {
            uploadImage();

        }else{
            uploadPost();
        }
    }




    private void uploadImage()
    {
        //Here we will upload images one by one asynchronously;
        if(currentImageUploadIndex>=stringImages.size())
        {
            uploadPost();
            return; //why do you want infinite loop? or outofbound exception so just return it.
        }

        StringRequest stringImageUploadRequest = new StringRequest(Request.Method.POST, Vars.UploadImageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"IN upload image request "+response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isError = jsonResponse.getBoolean(Vars.KeyError);
                    if(isError)
                    {
                        progressDialog.hide();
                        isPostUploading = false;
                        showToast("Post Uploading Failed");
                    }else{
                        String imageUrl = jsonResponse.getString(Vars.KeyImagePath);

                        uploadedImageUrls.add(imageUrl);

                        currentImageUploadIndex++;

                        uploadImage(); //upload next image;

                    }
                }catch (JSONException e)
                {
                    Log.d(TAG,"error in json in create post activity uploading image "+Log.getStackTraceString(e));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                showToast("Post Uploading Failed");
                isPostUploading = false;
                Log.d(TAG,"Volley Error while uploading post image in postcreate activity");
              }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String,String>();
                params.put(Vars.KeyImageStr,stringImages.get(currentImageUploadIndex));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringImageUploadRequest);
                                                                                                                                                                 /**/
    }





    private void uploadPost()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addPostUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                isPostUploading = false;
                progressDialog.hide();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isError = jsonResponse.getBoolean(Vars.KeyError);
                    if(!isError)
                    {
                        showToast("Post Created Successfully.");
                    }else{
                        showToast("Post Uploading Failed.");
                    }
                    Log.d(TAG, response);
                } catch (JSONException e) {
                    Log.d(TAG,"Create Post , Json Error "+Log.getStackTraceString(e));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isPostUploading =false;
                progressDialog.hide();
                showToast("Post Uploading Failed");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationHeader.getAuthenticationHeader(Vars.authKey);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String > params = new HashMap<String,String>();
                params.put(Vars.KeyPostTopic, topic.replaceAll(" ","_")); //topic should not contain any whitespace as it will be unsafe to add such topic in the url RFC 1738;
                params.put(Vars.KeyPostContent,getBodyJsonString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,requestTag);
    }


    public String getBodyJsonString()
    {
        title = "";
        body = "";

        if(titleET.getText()!=null)
        {
            title = titleET.getText().toString();
        }
        if(bodyEt.getText()!=null) {
            body = bodyEt.getText().toString();
        }


        JSONArray images = new JSONArray();
        for(String imageUrl : uploadedImageUrls)
        {
            images.put(imageUrl);
        }

        JSONObject post = new JSONObject();

        try{
            post.put("title",title);
            post.put("body",body);
            post.put("images",images);
        }catch (JSONException e)
        {
            Log.d(TAG,""+Log.getStackTraceString(e));
            return null;
        }
        Log.d(TAG,"post looks like this "+post.toString());
        return post.toString();
    }

    // Start Image Chooser Activity
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Get result from Image Chooser Activity;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Bitmap   bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
               View mView =  this.getLayoutInflater().inflate(R.layout.post_preview_image_view,null,false);
                ImageView imageView =(ImageView) mView.findViewById(R.id.image_view);
                imageView.setImageBitmap(Methods.decodeBase64(Methods.encodeToBase64(bitmap)));
                imageContainer.addView(mView);
                stringImages.add(getStringImage(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // We will upload image as string after encoding base64;
    public String getStringImage(Bitmap bmp){
       String strImage = Methods.encodeToBase64(bmp);
        return "data:image/jpeg;base64,"+strImage;
    }


    private void showToast(String message)
    {
       Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}